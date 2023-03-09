package org.xxxx.core.genie;

import org.xxxx.agent.Config;
import org.xxxx.javassist.*;
import org.xxxx.utils.CheckStruct;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class GenieBase {
    private static final Logger LOGGER = Logger.getLogger(GenieBase.class.getName());
    public static Instrumentation instrumentation;
    private boolean isLoadedByBootstrapLoader = false;

    public abstract boolean isClassMatched(String var1);

    public abstract void hookMethod(CtClass ctClass) throws IOException, CannotCompileException, NotFoundException;

    public byte[] transformClass(CtClass ctClass) throws Throwable {
        try {
            this.hookMethod(ctClass);
            return ctClass.toBytecode();
        } catch (Throwable var3) {
            throw var3;
        }
    }

    public boolean isLoadedByBootstrapLoader() {
        return this.isLoadedByBootstrapLoader;
    }

    public void setLoadedByBootstrapLoader(boolean loadedByBootstrapLoader) {
        this.isLoadedByBootstrapLoader = loadedByBootstrapLoader;
    }

    public String getInvokeStaticSrc(Class invokeClass, String methodName, String paramString, Class... parameterTypes) {
        String invokeClassName = invokeClass.getName();
        String parameterTypesString = "";
        if (parameterTypes != null && parameterTypes.length > 0) {
            Class[] arr$ = parameterTypes;
            int len$ = parameterTypes.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Class parameterType = arr$[i$];
                if (parameterType.getName().startsWith("[")) {
                    parameterTypesString = parameterTypesString + "Class.forName(\"" + parameterType.getName() + "\"),";
                } else {
                    parameterTypesString = parameterTypesString + parameterType.getName() + ".class,";
                }
            }

            parameterTypesString = parameterTypesString.substring(0, parameterTypesString.length() - 1);
        }

        if (parameterTypesString.equals("")) {
            parameterTypesString = null;
        } else {
            parameterTypesString = "new Class[]{" + parameterTypesString + "}";
        }

        String src;
        if(this.isLoadedByBootstrapLoader) {
            src = "if ($1.getParameter(\"action\") != null){org.xxxx.agent.ModuleLoader.moduleClassLoader.loadClass(\"" + invokeClassName + "\").getMethod(\"" + methodName + "\"," + parameterTypesString + ").invoke(null";
            if (paramString != null && paramString.equals("")) {
                src = src + ",new Object[]{" + paramString + "});";
            } else {
                src = src + ",null);";
            }

            src = "return;}";

            src = "try {" + src + "} catch (Throwable t) {}";
        } else {
            src = invokeClassName + '.' + methodName + "(" + paramString + ");";
            src = "if ($1.getParameter(\"action\") != null){" +src+ " return;}";
            src = "try {" + src + "} catch (Throwable t) {}";
        }

        return src;
    }
    public static String Template(Integer ID, String Name, String Pattern, String Class, String ClassLoader, Class<?> aClass){
        if (Pattern!=null){
            return String.format("ID: %s\nName: %s\nPattern: %s\nClass: %s\nClassLoader: %s\nFile Path: %s\n",ID, Name,Pattern,Class,ClassLoader,classFileIsExists(aClass));
        }else {
            return String.format("Class: %s\nClassLoader: %s\nFile Path: %s\n", Class,ClassLoader,classFileIsExists(aClass));
        }

    }

    public static String classFileIsExists(Class clazz) {
        try{
            if (clazz == null) {
                return "class is null";
            }
            String className = clazz.getName();
            String classNamePath = className.replace(".", "/") + ".class";
            URL is = clazz.getClassLoader().getResource(classNamePath);
            if (is == null) {
                return "There is no corresponding class file on disk, it may be MemShell";
            } else {
                return is.getPath();
            }
        }catch (Exception e){
            return "The classloader of class may be BootStrap";
        }
    }

    public static String arrayToString(String[] str) {
        String res = "";
        for (String s : str) {
            res += String.format("%s,", s);
        }
        res = res.substring(0, res.length() - 1);
        return res;
    }

    public static String scanResult(){
        String filterHtml = "";
        String servletHtml = "";
        String listenerHtml = "";
        String transformBlackHtml = "";
        String temp = "";
        HashMap<Integer, HashMap<String, Object>> results = (HashMap<Integer, HashMap<String, Object>>) CheckStruct.getMemShells();
        for (Map.Entry result:
             results.entrySet()) {
            String id = String.valueOf(result.getKey());
            HashMap<String, Object> memShell = (HashMap<String, Object>) result.getValue();
            String type = (String) memShell.get("type");
            String name = (String) memShell.get("name");
            String pattern = (String) memShell.get("pattern");
            String clazz = (String) memShell.get("class");
            String classloader = (String) memShell.get("classloader");
            String filepath = (String) memShell.get("filePath");
            String killType = (String) memShell.get("killType");
            temp += "<td>" + id + "</td>";
            temp += "<td>" + name + "</td>";
            temp += "<td>" + pattern + "</td>";
            temp += "<td>" + clazz + "</td>";
            temp += "<td>" + classloader + "</td>";
            temp += "<td>" + filepath + "</td>";
            temp += String.format("<td><a href=\"?action=dump&id=%s\">Dump</a></td>", id);
            temp += String.format("<td><a href=\"?action=kill&id=%s&type=%s\">Kill</a></td>", id, killType);
            temp = "<tr>" + temp + "</tr>";
            if(type.equals("Filter")){
                filterHtml += temp;
            }else if(type.equals("Servlet")){
                servletHtml += temp;
            }else if(type.equals("Listener")){
                listenerHtml += temp;
            }else if(type.equals("Transform")){
                transformBlackHtml += temp;
            }
            temp = "";
        }
        return Config.scanResultTemplate.replace("{filter}", filterHtml)
                .replace("{listener}", listenerHtml).replace("{servlet}", servletHtml).replace("{transformer}", transformBlackHtml);
    }

}
