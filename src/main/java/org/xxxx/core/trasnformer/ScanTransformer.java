package org.xxxx.core.trasnformer;

import org.xxxx.agent.Config;
import org.xxxx.utils.Cache;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScanTransformer extends TransformerBase implements ClassFileTransformer {
    private Instrumentation instrumentation;
    private List<Class> scanResult;
    private final static String interFaceName = "java.lang.instrument.ClassFileTransformer";
    private static List<String> blackList;
    private static List<String> witheTransformerNames = Config.witheTransformerNames;

    public ScanTransformer(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        this.scanResult = new ArrayList<Class>();
        instrumentation.addTransformer(this, true);
        Cache.initTransformers();
        Cache.initLastBlackList();
    }

    private void isMatchedInterFaces(Class clazz) {
        // skip withe name list
        if(witheTransformerNames.contains(clazz.getName())){
            return ;
        }
        Class[] classes = clazz.getInterfaces();
        for (Class c : classes) {
            if (c.getName().equals(interFaceName)) {
                Cache.addLastTransformer(clazz);
                return;
            }
        }
    }


    private void isMatchedBlackList(Class clazz) {
        String clazzName = clazz.getName();
        if(blackList.contains(clazzName)){
            Cache.addLastBlackList(clazz);
        }
    }

    private void isMatched(Class clazz){
        isMatchedBlackList(clazz);
        isMatchedInterFaces(clazz);
    }

    @Override
    public void retransform() {
        Class[] loadedClasses = this.instrumentation.getAllLoadedClasses();
        Class[] arr = loadedClasses;
        int len = loadedClasses.length;
        for (int i = 0; i < len; ++i) {
            Class clazz = arr[i];
            isMatched(clazz);
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String clazzName = className.replace("/", ".");
        classfileBuffer = Cache.classByteCache.get(clazzName);
        if(classfileBuffer != null){
            return classfileBuffer;
        }
        return null;
    }

    @Override
    public void release() {
        this.instrumentation.removeTransformer(this);
    }


    public List<Class> getScanResult() {
        this.scanResult.addAll(Cache.lastTransformers);
        this.scanResult.addAll(Cache.lastBlackList);
        return this.scanResult;
    }

    static {
        try {
            String blackName = Config.properties.getProperty("black-list");
            blackList = Arrays.asList(blackName.split(","));
        } catch (Exception e) {
            blackList = new ArrayList<String>();
        }

    }
}
