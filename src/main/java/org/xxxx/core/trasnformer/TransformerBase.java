package org.xxxx.core.trasnformer;


import org.xxxx.agent.ModuleLoader;
import org.xxxx.javassist.ClassClassPath;
import org.xxxx.javassist.ClassPool;
import org.xxxx.javassist.LoaderClassPath;

import java.lang.instrument.UnmodifiableClassException;

public abstract class TransformerBase {
    public abstract void retransform() throws Throwable;

    public abstract void release() throws UnmodifiableClassException, ClassNotFoundException;

    public void addLoader(ClassPool classPool, ClassLoader loader) {
        classPool.appendSystemPath();
        classPool.appendClassPath(new ClassClassPath(ModuleLoader.class));
        if (loader != null) {
            classPool.appendClassPath(new LoaderClassPath(loader));
        }
    }

}
