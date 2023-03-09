package xxxx.agent;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.net.URLDecoder;

public class ModuleLoader {
    private static String coreBoot;
    private static Module module;
    public static String baseDirectory;
    private static ModuleLoader instance;
    public static ClassLoader moduleClassLoader;

    private ModuleLoader(String mode, Instrumentation inst) throws Throwable {
        Class clazz = moduleClassLoader.loadClass(coreBoot);
        module = (Module) clazz.newInstance();
        module.start("tomcat", inst);

    }

    public static synchronized void release(String mode) {
        try {
            if (module != null) {
                System.out.println("[MemShellKiller] Start to release MemShellKiller");
                module.release(mode);
                module = null;
                instance = null;
            } else {
                System.out.println("[MemShellKiller] Module is initialized, skipped");
            }
        } catch (Throwable throwable) {
        }

    }

    public static synchronized void load(String mode, String action, Instrumentation inst) throws Throwable {
        if ("install".equals(action)) {
            if (instance == null) {
                try {
                    instance = new ModuleLoader(mode, inst);
                } catch (Throwable var4) {
                    instance = null;
                    throw var4;
                }
            } else {
                System.out.println("[MemShellKiller] The MemShellKiller has bean initialized and cannot be initialized again");
            }
        } else {
            if (!"uninstall".equals(action)) {
                throw new IllegalStateException("[MemShellKiller] Can not support the action: " + action);
            }

            release(mode);
        }

    }

    static {
        coreBoot = Config.properties.getProperty("coreBoot");
        Class clazz;
        try {
            clazz = Class.forName("java.nio.file.FileSystems");
            clazz.getMethod("getDefault").invoke((Object) null);
        } catch (Throwable var4) {
        }

        clazz = ModuleLoader.class;
        String path = clazz.getResource("/" + clazz.getName().replace(".", "/") + ".class").getPath();
        if (path.startsWith("file:")) {
            path = path.substring(5);
        }

        if (path.contains("!")) {
            path = path.substring(0, path.indexOf("!"));
        }

        try {
            baseDirectory = URLDecoder.decode((new File(path)).getParent(), "UTF-8");
        } catch (UnsupportedEncodingException var3) {
            baseDirectory = (new File(path)).getParent();
        }

        ClassLoader systemClassLoader;
        for (systemClassLoader = ClassLoader.getSystemClassLoader(); systemClassLoader.getParent() != null && !systemClassLoader.getClass().getName().equals("sun.misc.Launcher$ExtClassLoader"); systemClassLoader = systemClassLoader.getParent()) {
        }

        moduleClassLoader = systemClassLoader;
    }
}