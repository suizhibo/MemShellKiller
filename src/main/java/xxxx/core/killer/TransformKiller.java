package xxxx.core.killer;

import org.xxxx.javassist.*;
import org.xxxx.utils.JavassistUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.ProtectionDomain;

public class TransformKiller extends KillerBase {

    @Override
    public String getType() {
        return "transformer";
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        
        CtClass ctClass = null;
        try {
            ClassPool classPool = new ClassPool();
            this.addLoader(classPool, loader);
            ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
            // 以下包括两种kill transform的方法
//            String methodSrc = "String clazzName = $2.replace(\"/\", \".\"); byte[] newClassfileBuffer = (byte[])org.xxxx.utils.Cache.classByteCache.get(clazzName); if(newClassfileBuffer != null){return newClassfileBuffer; }";
//            new JavassistUtil().insertBefore(ctClass, "transform", (String)null, methodSrc);
            String methodSrc = "{String clazzName = $2.replace(\"/\", \".\"); byte[] newClassfileBuffer = (byte[])org.xxxx.utils.Cache.classByteCache.get(clazzName); if(newClassfileBuffer != null){return newClassfileBuffer; }return null;}";
            new JavassistUtil().setBody(ctClass, "transform", (String)null, methodSrc);
            classfileBuffer = ctClass.toBytecode();
            return classfileBuffer;
        } catch (IOException var13) {
            var13.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        finally {
            if(ctClass != null){
                ctClass.detach();
            }
        }
        return null;
    }
}
