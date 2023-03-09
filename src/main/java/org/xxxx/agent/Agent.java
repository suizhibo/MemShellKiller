package org.xxxx.agent;


import java.lang.instrument.Instrumentation;

public class Agent {
    public static String projectVersion;
    public static String buildTime;
    public static String gitCommit;

    public Agent() {
    }

    public static void main(String[] args) {
    }

    public static void premain(String agentArg, Instrumentation inst) {
        init("normal", agentArg, inst);
    }

    public static void agentmain(String agentArg, Instrumentation inst) {
        init("attach", agentArg, inst);
    }

    public static synchronized void init(String mode, String agentArg, Instrumentation inst) {
        try {
            String[] args = agentArg.split(",");
            if(args.length != 2){
                System.err.println("[MemShellKiller] Failed to initialize, The format of args must be: action,configPath");
                return;
            }
            String action = args[0];
            String configPath = args[1];
            JarFileHelper.addJarToBootstrap(inst);
            Config.loadConfig(configPath);
            if(action.equals("release")){
                ModuleLoader.release(mode);
            }
            else{
                ModuleLoader.load(mode, action, inst);}
        } catch (Throwable throwable) {
            System.err.println("[MemShellKiller] Failed to initialize, will continue without security protection.");
            throwable.printStackTrace();
        }
    }
}
