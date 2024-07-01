package org.xxxx.core.trasnformer;


import org.xxxx.agent.Config;
import org.xxxx.core.genie.GenieBase;
import org.xxxx.javassist.ClassPool;
import org.xxxx.javassist.CtClass;
import org.xxxx.utils.Cache;
import org.xxxx.utils.Utils;

import java.io.ByteArrayInputStream;
import java.lang.instrument.*;
import java.security.ProtectionDomain;
import java.util.List;

public class GenieTransformer extends TransformerBase implements ClassFileTransformer {
    private Instrumentation instrumentation;
    private GenieBase genie;
    private ProtectedTransformer protectedTransformer;
    private static List<String> witheTransformerNames = Config.witheTransformerNames;

    public GenieTransformer(Instrumentation instrumentation, GenieBase genie){
        this.instrumentation = instrumentation;
        this.genie = genie;
        this.protectedTransformer = new ProtectedTransformer();
        instrumentation.addTransformer(this, true);
        instrumentation.addTransformer(this.protectedTransformer, true); // open protect
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String clazzName = className.replace("/", ".");
        byte[] newClassByte = Cache.classByteCache.get(clazzName);
        if(newClassByte != null){
            classfileBuffer = newClassByte;
            return classfileBuffer;
        }
        if(this.genie.isClassMatched(className)){
            CtClass ctClass = null;
            try {
                ClassPool classPool = new ClassPool();
                this.addLoader(classPool, loader);
                // hook 要hook在最原始的字节码
//                if(loader != null){
//                    InputStream in =  loader.getResourceAsStream(className + ".class");
//                    ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
//                    byte[] buff = new byte[1024];
//                    int rc = 0;
//                    while ((rc = in.read(buff, 0, 100)) > 0) {
//                        swapStream.write(buff, 0, rc);
//                    }
//                    classfileBuffer = swapStream.toByteArray();
//                }
                ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                if (loader == null) {
                    this.genie.setLoadedByBootstrapLoader(true);
                }
                classfileBuffer = this.genie.transformClass(ctClass);
                Cache.classByteCache.put(clazzName, classfileBuffer);
                return classfileBuffer;
            } catch (Throwable throwable) {
                throw new IllegalClassFormatException("Install failed");
            } finally {
                if (ctClass != null) {
                    ctClass.detach();
                }
            }
        }
        return null;
    }


    @Override
    public void retransform() throws Throwable {
        Class[] loadedClasses = this.instrumentation.getAllLoadedClasses();
        Class[] arr = loadedClasses;
        int len = loadedClasses.length;

        for(int i = 0; i < len; ++i) {
            Class clazz = arr[i];
            if (this.genie.isClassMatched(clazz.getName().replace(".", "/")) &&
                    this.instrumentation.isModifiableClass(clazz) && !clazz.getName().startsWith("java.lang.invoke.LambdaForm")){
                try {
                    this.instrumentation.retransformClasses(new Class[]{clazz});
                    break;
                } catch (Throwable throwable) {
                    throw throwable;
                }finally {
                    // hook指定类的方法后，可以释放改transformer
                    this.instrumentation.removeTransformer(this);
                }
            }
        }
    }

    @Override
    public void release() throws UnmodifiableClassException, ClassNotFoundException {
        // 还没测试，下周测试
        this.instrumentation.removeTransformer(this);
        this.instrumentation.removeTransformer(this.protectedTransformer);
        Class[] loadedClasses = this.instrumentation.getAllLoadedClasses();
        int len = loadedClasses.length;
        for(int i = 0; i < len; ++i) {
            Class clazz = loadedClasses[i];
            if (this.genie.isClassMatched(clazz.getName().replace(".", "/")) &&
                    this.instrumentation.isModifiableClass(clazz) && !clazz.getName().startsWith("java.lang.invoke.LambdaForm")){
                byte[] data = Utils.getClassByte(clazz.getName().replace(".", "/"), clazz.getClassLoader());
                this.instrumentation.redefineClasses(new ClassDefinition[] { new ClassDefinition(clazz, data) });
            }
        }



    }
}
