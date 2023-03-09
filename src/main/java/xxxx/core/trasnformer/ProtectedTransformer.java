package xxxx.core.trasnformer;

import org.xxxx.agent.Config;
import org.xxxx.javassist.ClassPool;
import org.xxxx.javassist.CtClass;
import org.xxxx.javassist.CtMethod;
import org.xxxx.javassist.NotFoundException;
import org.xxxx.utils.Cache;
import org.xxxx.utils.JavassistUtil;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

public class ProtectedTransformer extends TransformerBase implements ClassFileTransformer {
    static final List<String> witheTransformerNames;
    private final static String interFaceName = "java.lang.instrument.ClassFileTransformer";

    private boolean isMatchedInterFaces(CtClass clazz) throws NotFoundException {
        CtClass[] classes = clazz.getInterfaces();
        for (CtClass c : classes) {
            if (c.getName().equals(interFaceName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMatchedPremainOrAgentmain(CtClass clazz) {
        CtMethod cm = null;
        CtMethod cm1 = null;
        try {
            cm1 = clazz.getDeclaredMethod("premain");
            cm = clazz.getDeclaredMethod("agentmain");
        } catch (NotFoundException e) {
        }
        return cm != null || cm1 != null;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String clazzName = className.replace("/", ".");
        byte[] newClassByte = Cache.classByteCache.get(clazzName);
        if (newClassByte != null) {
            classfileBuffer = newClassByte;
            return classfileBuffer;
        }

        CtClass ctClass = null;
        try {
            ClassPool cp = new ClassPool();
            ctClass = cp.makeClass(new ByteArrayInputStream(classfileBuffer));
            this.addLoader(cp, loader);
            CtMethod cm = null;
            String methodSrc = null;
            // 不加载后续所有的agent
            if (isMatchedPremainOrAgentmain(ctClass)) {
                methodSrc = "{return;}";
                JavassistUtil javassistUtil = new JavassistUtil();
                cm = ctClass.getDeclaredMethod("premain");
                cm.setBody(methodSrc);
                javassistUtil.setBody(ctClass, "premain", (String)null, methodSrc);
                javassistUtil.setBody(ctClass, "agentmain", (String)null, methodSrc);
                javassistUtil.setBody(ctClass, "doAgentShell", (String)null, methodSrc);
                classfileBuffer = ctClass.toBytecode();
                Cache.classByteCache.put(clazzName, classfileBuffer);
                return classfileBuffer;
            }
            // 后面可以将限制条件放松一点，设置白名单，白名单中所有的transformer不进行拦截
//            if (!witheTransformerNames.contains(clazzName) && isMatchedInterFaces(ctClass)) {
//                methodSrc = "{String clazzName = $2.replace(\"/\", \".\"); byte[] newClassfileBuffer = (byte[])org.xxxx.utils.Cache.classByteCache.get(clazzName); if(newClassfileBuffer != null){return newClassfileBuffer; }return null;}";
//                cm = ctClass.getDeclaredMethod("transform");
//                cm.setBody(methodSrc);
//                classfileBuffer = ctClass.toBytecode();
//                Cache.classByteCache.put(clazzName, classfileBuffer);
//                return classfileBuffer;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ctClass != null) {
                ctClass.detach();
            }
        }
        return null;
    }

    static {
        witheTransformerNames = Config.witheTransformerNames;
    }

    @Override
    public void retransform() {

    }

    @Override
    public void release() {

    }
}
