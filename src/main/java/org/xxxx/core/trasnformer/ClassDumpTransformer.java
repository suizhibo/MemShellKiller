package org.xxxx.core.trasnformer;

import org.xxxx.utils.Cache;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class ClassDumpTransformer extends TransformerBase implements ClassFileTransformer {
    private String className;
    private Instrumentation instrumentation;
    private byte[] out;
    public ClassDumpTransformer(Instrumentation instrumentation, String className){
        this.className = className;
        this.instrumentation = instrumentation;
        instrumentation.addTransformer(this, true);
    }

    public void release(){
        this.instrumentation.removeTransformer(this);
    }

    @Override
    public void retransform() throws Throwable {
        Class[] loadedClasses = this.instrumentation.getAllLoadedClasses();
        Class[] arr = loadedClasses;
        int len = loadedClasses.length;

        for(int i = 0; i < len; ++i) {
            Class clazz = arr[i];
            if (clazz.getName().equals(this.className) && !clazz.getName().startsWith("java.lang.invoke.LambdaForm")){
                try {
                    this.instrumentation.retransformClasses(new Class[]{clazz});
                    break;
                } catch (Throwable throwable) {
                    throw throwable;
                }
            }
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        
        String clazzName = className.replace("/", ".");
        if (clazzName.equals(this.className)){
            this.setOut(classfileBuffer);
        }
        return null;
    }

    public byte[] getOut() {
        return out;
    }

    public void setOut(byte[] out) {
        this.out = out;
    }
}
