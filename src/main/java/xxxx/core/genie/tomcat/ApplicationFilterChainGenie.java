package xxxx.core.genie.tomcat;

import org.xxxx.core.genie.GenieBase;
import org.xxxx.core.trasnformer.ClassDumpTransformer;
import org.xxxx.core.trasnformer.KillerTransformer;
import org.xxxx.core.trasnformer.ScanTransformer;
import org.xxxx.core.trasnformer.TransformerBase;
import org.xxxx.javassist.CannotCompileException;
import org.xxxx.javassist.CtClass;
import org.xxxx.javassist.NotFoundException;
import org.xxxx.request.HttpServletRequest;
import org.xxxx.response.HttpServletResponse;
import org.xxxx.utils.CheckStruct;
import org.xxxx.utils.JavassistUtil;
import org.xxxx.utils.Reflections;

import java.io.UnsupportedEncodingException;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationFilterChainGenie extends GenieBase {
    private static Object context;
    private static String genieClassName = "apache/catalina/core/ApplicationFilterChain";
    private static String genieMethodName = "doFilter";

    @Override
    public boolean isClassMatched(String className) {
        return className.endsWith(genieClassName);
    }

    public static Object[] getFilterMaps(Object context) throws Exception {
        Object filterMaps = Reflections.getField(context, "filterMaps");
        Object[] filterArray = null;
        try {
            // tomcat 789
            filterArray = (Object[]) Reflections.getField(filterMaps, "array");
        } catch (Exception e) {
            // tomcat 6
            filterArray = (Object[]) filterMaps;
        }

        return filterArray;
    }

    public static void scan(Object context, HttpServletRequest request, HttpServletResponse response) {
        StringBuffer sendContext = new StringBuffer();
        try {
            List<Object> listeners = (List<Object>) Reflections.getField(context, "applicationEventListenersList");
            if (listeners != null) {
                List<Object> newListeners = new ArrayList<Object>();
                for (Object o : listeners) {
                    if (o.getClass().getName().endsWith("ServletRequestListener")) {
                        newListeners.add(o);
                    }
                }
                String listenerTName = null;
                for (Object listener : newListeners) {
                    listenerTName = listener.getClass().getName();
                    sendContext.append(Template(listener.hashCode(), null, null, listenerTName, listener.getClass().getClassLoader().getClass().getName(), listener.getClass()));
                    HashMap<String, Object> memShellInfo = CheckStruct.newMemShellInfo(null, null, listenerTName,
                            listener.getClass().getClassLoader().getClass().getName(), classFileIsExists(listener.getClass()), "Listener", "normal");
                    CheckStruct.set(listener.hashCode(), memShellInfo);
                }
            }

            Object[] filterMaps = getFilterMaps(context);
            for (int i = 0; i < filterMaps.length; i++) {
                try {
                    Object fm = filterMaps[i];
                    Method getFilterName = fm.getClass().getDeclaredMethod("getFilterName");
                    getFilterName.setAccessible(true);
                    String filterName = (String) getFilterName.invoke(fm, null);

                    HashMap<String, Object> filterConfigs = (HashMap<String, Object>) Reflections.getField(context, "filterConfigs");
                    Object appFilterConfig = filterConfigs.get(filterName);


                    Method getURLPatterns = fm.getClass().getDeclaredMethod("getURLPatterns");
                    getFilterName.setAccessible(true);
                    String[] urlpatterns = (String[]) getURLPatterns.invoke(fm, null);

                    if (appFilterConfig != null) {
                        Object filter = Reflections.getField(appFilterConfig, "filter");
                        String filterClassName = filter.getClass().getName();
                        String filterClassLoaderName = filter.getClass().getClassLoader().getClass().getName();
                        HashMap<String, Object> memShellInfo = CheckStruct.newMemShellInfo(filterName, arrayToString(urlpatterns),
                                filterClassName, filterClassLoaderName, classFileIsExists(filter.getClass()), "Filter", "normal");
                        CheckStruct.set(filter.hashCode(), memShellInfo);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            Map<String, Object> children = (Map<String, Object>) Reflections.getField(context, "children"); //Tomcat此处是HashMap
            HashMap<String, String> servletMappings = (HashMap<String, String>) Reflections.getField(context, "servletMappings");

            for (Map.Entry<String, String> map : servletMappings.entrySet()) {
                String servletMapPath = map.getKey();
                String servletName = map.getValue();
                Object wrapper = children.get(servletName);
                Class servletClass = null;
                Class wrapperC = wrapper.getClass();
                try {
                    Method getServletClass = wrapperC.getDeclaredMethod("getServletClass");
                    getServletClass.setAccessible(true);
                    servletClass = Class.forName((String) getServletClass.invoke(wrapper));
                } catch (Exception e) {
                    Method getServletClass = wrapperC.getDeclaredMethod("getServlet");
                    getServletClass.setAccessible(true);
                    Object servlet = getServletClass.invoke(wrapper);
                    if (servlet != null) {
                        servletClass = servlet.getClass();
                    }
                }
                if (servletClass != null) {
                    String servletClassName = servletClass.getName();
                    String servletClassLoaderName = null;
                    try {
                        servletClassLoaderName = servletClass.getClassLoader().getClass().getName();
                    } catch (Exception e) {
                    }
                    HashMap<String, Object> memShellInfo = CheckStruct.newMemShellInfo(servletName,
                            servletMapPath, servletClassName, servletClassLoaderName, classFileIsExists(servletClass), "Servlet", "normal");
                    CheckStruct.set(servletClass.hashCode(), memShellInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*Transform OR BlackList*/
        TransformerBase scanTransformer = new ScanTransformer(instrumentation);
        try{
            scanTransformer.retransform();
            List<Class> classes = ((ScanTransformer) scanTransformer).getScanResult();
            for (Class clazz : classes){
                String classLoaderName;
                try{
                    classLoaderName = clazz.getClassLoader().getClass().getName();
                }catch (NullPointerException e){
                    classLoaderName = "BootStrap ClassLoader";
                }
                String killType = "";
                try{
                    clazz.getDeclaredMethod("transform", new Class[]{ClassLoader.class, String.class, Class.class, ProtectionDomain.class, byte[].class});
                    killType = "transform";
                }catch (NoSuchMethodException e){
                    killType = "agent";
                }catch (Exception e){}
                HashMap<String, Object> memShellInfo = CheckStruct.newMemShellInfo(clazz.getName(), "", clazz.getName(),
                        classLoaderName, classFileIsExists(clazz), "Transform", killType);
                CheckStruct.set(clazz.hashCode(), memShellInfo);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            scanTransformer.release();
        }

        String result = scanResult();
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.sendContent(result, true);
    }

    public static void dump(int id,  HttpServletRequest request, HttpServletResponse response){
        String className = (String) CheckStruct.get(Integer.valueOf(id), "class");
        if(className != null && className.equals("")){
            className = (String) CheckStruct.get(Integer.valueOf(id), "name");
        }
        dump(className, request, response);
    }

    public static void dump(String className,  HttpServletRequest request, HttpServletResponse response){
        TransformerBase classDumpTransformer = new ClassDumpTransformer(instrumentation, className);
        try {
            classDumpTransformer.retransform();
            byte[] classBytes = ((ClassDumpTransformer) classDumpTransformer).getOut();
            response.addHeader("content-Type", "application/octet-stream");
            String filename = className + ".class";
            String agent = request.getHeader("User-Agent");
            if (agent != null && agent.toLowerCase().indexOf("chrome") > 0) {
                response.addHeader("content-Disposition", "attachment;filename=" + new String(filename.getBytes("UTF-8"), "ISO8859-1"));
            } else {
                response.addHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            }
            response.sendContent(classBytes, true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            classDumpTransformer.release();
        }
    }

    public static void kill(int id, HttpServletRequest request, HttpServletResponse response){
        String className = (String) CheckStruct.get(Integer.valueOf(id), "class");
        if(className != null && className.equals("")){
            className = (String) CheckStruct.get(Integer.valueOf(id), "name");
        }
        String memShellType = (String) CheckStruct.get(Integer.valueOf(id), "type");
        String type = request.getParameter("type");
        kill(className, request, response, type, memShellType);
    }

    public static void kill(String className, HttpServletRequest request, HttpServletResponse response, String type, String subClasses){
        TransformerBase killerTransformer;
//        // first skill all transformer
//        TransformerBase killerTransformer = new KillerTransformer(instrumentation, "", "transformer", subClasses);
//        try{
//            killerTransformer.retransform();
//        }catch (Throwable throwable) {
//            throwable.printStackTrace();
//        } finally {
//            killerTransformer.release();
//        }


        killerTransformer = new KillerTransformer(instrumentation, className, type, subClasses);
        try{
            killerTransformer.retransform();
        }catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            killerTransformer.release();
        }
    }

    @Override
    public void hookMethod(CtClass ctClass) throws CannotCompileException, NotFoundException {
        String methodSrc = this.getInvokeStaticSrc(ApplicationFilterChainGenie.class, "genieMethod", "$1,$2",
                new Class[]{Object.class, Object.class, Object.class});
        new JavassistUtil().insertBefore(ctClass, genieMethodName, (String) null, methodSrc);
    }


    public static void genieMethod(Object request, Object response) {
        HttpServletRequest req = new HttpServletRequest(request);
        HttpServletResponse resp = new HttpServletResponse(response);
        String action = req.getParameter("action");
        if (action != null) {
            try {
                String id = req.getParameter("id");
                if (action.equalsIgnoreCase("scan")) {
                    if (context == null) {
                        context = Reflections.getField((Reflections.getField(Reflections.getField(req.getRequest(), "request"), "mappingData")), "context");
                    }
                    scan(context, req, resp);
                }else if(action.equalsIgnoreCase("dump")){
                    if (id != null && id.length() > 0){
                        dump(Integer.valueOf(id), req, resp);
                    }
                }else if (action.equalsIgnoreCase("kill")){
                    if (id != null && id.length() > 0){
                        kill(Integer.valueOf(id), req, resp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
