package org.xxxx.core.trasnformer;

import org.xxxx.core.killer.AgentKiller;
import org.xxxx.core.killer.KillerBase;
import org.xxxx.core.killer.NormalKiller;
import org.xxxx.core.killer.TransformKiller;
import org.xxxx.utils.Cache;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KillerTransformer extends TransformerBase implements ClassFileTransformer {
    private Instrumentation instrumentation;
    private String className;
    private String type;
    private String subClasses;
    private List<KillerBase> killTransformers;
    private List<String> classesName;
    static boolean isFirst = false;

    // 当要对普通型内存马进行操作时，要提供其类型（filter\servlet\listener）
    public KillerTransformer(Instrumentation instrumentation, String className, String type, String subClasses) {
        this.instrumentation = instrumentation;
        this.className = className;
        this.type = type;
        this.subClasses = subClasses;
        this.killTransformers = new ArrayList<KillerBase>();
        this.instrumentation.addTransformer(this, true);
        this.installTransform();

    }

    public void installTransform(){
        if(this.type != null && this.type.equals("transform")){
            KillerBase killTransformersTransform = new TransformKiller();
            this.killTransformers.add(killTransformersTransform);
        } else if(this.type != null && this.type.equals("agent")){
            KillerBase killAgentTransform = new AgentKiller();
            this.killTransformers.add(killAgentTransform);
        }else if(this.type != null && this.type.equals("normal")){
            KillerBase killNormalTransform = new NormalKiller(this.subClasses);
            this.killTransformers.add(killNormalTransform);
        }
    }

    @Override
    public void retransform() throws Throwable {
        Class[] loadedClasses = this.instrumentation.getAllLoadedClasses();
        Class[] arr = loadedClasses;
        int len = loadedClasses.length;
        Set<Class> classSet = new HashSet<Class>();
        // 最近一次的transformer扫描结果不为空且第一次执行kill
        if(!Cache.lastTransformers.isEmpty() && !isFirst){
            classSet.addAll(Cache.lastTransformers);
            isFirst = true;
        }
        for (int i$ = 0; i$ < len; ++i$) {
            Class clazz = arr[i$];
            String clazzName = clazz.getName();
            if (clazzName.equals(this.className) &&
                    this.instrumentation.isModifiableClass(clazz) && !clazz.getName().startsWith("java.lang.invoke.LambdaForm")){
                try {
                    classSet.add(clazz);
                    break;
                } catch (Throwable throwable) {
                    throw throwable;
                }
            }
        }
        this.classesName = new ArrayList<String>();
        for (Class cla:
             classSet) {
            this.classesName.add(cla.getName());
            this.instrumentation.retransformClasses(new Class[]{cla});
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String clazzName = className.replace("/", ".");
        byte[] newClassByte = Cache.classByteCache.get(clazzName);
        if(newClassByte != null){
            classfileBuffer = newClassByte;
            return classfileBuffer;
        }
        if (this.classesName.contains(clazzName)){
            for(KillerBase killTransformer: this.killTransformers){
                if(killTransformer.getType().equals(this.type)){
                    newClassByte = killTransformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                    if(classfileBuffer != null && !newClassByte.equals(classfileBuffer)){
                        Cache.classByteCache.put(clazzName, newClassByte);
                        return newClassByte;
                    }
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public void release() {
        this.instrumentation.removeTransformer(this);
        this.killTransformers.clear();
    }
}
