package org.xxxx.core.killer;

import org.xxxx.agent.ModuleLoader;
import org.xxxx.javassist.ClassClassPath;
import org.xxxx.javassist.ClassPool;
import org.xxxx.javassist.LoaderClassPath;

import java.security.ProtectionDomain;

public abstract class KillerBase {

    public abstract String getType();
    public abstract byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer);

    public void addLoader(ClassPool classPool, ClassLoader loader) {
        classPool.appendSystemPath();
        classPool.appendClassPath(new ClassClassPath(ModuleLoader.class));
        if (loader != null) {
            classPool.appendClassPath(new LoaderClassPath(loader));
        }
    }
}
