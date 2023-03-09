package org.xxxx.core.killer;

import org.xxxx.javassist.CannotCompileException;
import org.xxxx.javassist.ClassPool;
import org.xxxx.javassist.CtClass;
import org.xxxx.javassist.NotFoundException;
import org.xxxx.utils.JavassistUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class NormalKiller extends KillerBase {
    private String subClasses;
    static Map<String, Map<String, String>> Methods;

    public NormalKiller(String subClass){
        super();
        this.subClasses = subClass;
    }

    @Override
    public String getType() {
        return "normal";
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        
        CtClass ctClass = null;
        try {
            ClassPool cp = new ClassPool();
            ctClass = cp.makeClass(new ByteArrayInputStream(classfileBuffer));
            this.addLoader(cp, loader);
            Map<String, String> methods = Methods.get(this.subClasses);
            for (Map.Entry<String, String> entry:
                methods.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                new JavassistUtil().setBody(ctClass, key, (String)null, value);
            }
            classfileBuffer = ctClass.toBytecode();
            return classfileBuffer;
        } catch (IOException var13) {
            var13.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } finally {
            if(ctClass != null){
                ctClass.detach();
            }
        }
        return null;
    }

    static {
        Methods = new HashMap<String, Map<String, String>>();
        Map<String, String> filterMethod = new HashMap<String, String>();
        filterMethod.put("doFilter", "{$3.doFilter($1, $2);}");
        filterMethod.put("destroy", "{return;}");
        Map<String, String> servletMethod = new HashMap<String, String>();
        servletMethod.put("doGet", "{return;}");
        servletMethod.put("doHead", "{return;}");
        servletMethod.put("doPost", "{return;}");
        servletMethod.put("doPut", "{return;}");
        servletMethod.put("doDelete", "{return;}");
        servletMethod.put("doOptions", "{return;}");
        servletMethod.put("doTrace", "{return;}");
        servletMethod.put("service", "{return;}");
        Map<String, String> listenerMethod = new HashMap<String, String>();
        listenerMethod.put("requestDestroyed", "{return;}");
        listenerMethod.put("requestInitialized", "{return;}");
        listenerMethod.put("attributeAdded", "{return;}");
        listenerMethod.put("attributeRemoved", "{return;}");
        listenerMethod.put("attributeReplaced", "{return;}");
        listenerMethod.put("contextInitialized", "{return;}");
        listenerMethod.put("contextDestroyed", "{return;}");
        listenerMethod.put("sessionCreated", "{return;}");
        listenerMethod.put("sessionDestroyed", "{return;}");
        Methods.put("Filter", filterMethod);
        Methods.put("Servlet", filterMethod);
        Methods.put("Listener", filterMethod);
    }
}
