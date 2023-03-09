package xxxx.agent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarFile;

public class JarFileHelper {
    public JarFileHelper() {
    }

    public static void addJarToBootstrap(Instrumentation inst) throws IOException {
        String localJarPath = getLocalJarPath();
        inst.appendToBootstrapClassLoaderSearch(new JarFile(localJarPath));
    }

    public static String getLocalJarPath() {
        URL localUrl = Agent.class.getProtectionDomain().getCodeSource().getLocation();
        String path = null;

        try {
            path = URLDecoder.decode(localUrl.getFile().replace("+", "%2B"), "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            System.err.println("[MemShellKiller] Failed to get jarFile path.");
            exception.printStackTrace();
        }

        return path;
    }

    public static String getLocalJarParentPath() {
        String jarPath = getLocalJarPath();
        return jarPath.substring(0, jarPath.lastIndexOf("/"));
    }
}
