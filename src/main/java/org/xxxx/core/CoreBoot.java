package org.xxxx.core;

import org.xxxx.agent.Config;
import org.xxxx.agent.Module;
import org.xxxx.agent.ModuleLoader;
import org.xxxx.core.genie.GenieBase;
import org.xxxx.core.trasnformer.GenieTransformer;
import org.xxxx.core.trasnformer.TransformerBase;
import org.xxxx.utils.Cache;
import org.xxxx.utils.Reflections;

import java.lang.instrument.Instrumentation;

public class CoreBoot extends Module{
    private String genieName;
    private TransformerBase transformer;
    private String serverType;

    @Override
    public void start(String serverType, Instrumentation instrumentation) throws Throwable {
        this.genieName = Config.properties.getProperty("genie-name");
        this.serverType = serverType.toLowerCase();
        this.initTransformer(instrumentation);
    }

    @Override
    public void release(String mode) throws Throwable {
        if(this.transformer != null){
            this.transformer.release();
        }
    }

    private void initTransformer(Instrumentation instrumentation) throws Throwable {
        Cache.initClassByteCache();
        String genieType = this.serverType + "." +this.genieName;
        Class genieClass  = ModuleLoader.moduleClassLoader.loadClass("org.xxxx.core.genie." + genieType);
        GenieBase genieObject = (GenieBase) genieClass.newInstance();
        Reflections.setFieldValue(genieObject, "instrumentation", instrumentation);
        this.transformer =  new GenieTransformer(instrumentation, genieObject);
        this.transformer.retransform();


    }
}
