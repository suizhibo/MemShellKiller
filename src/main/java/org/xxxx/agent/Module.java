package org.xxxx.agent;

import java.lang.instrument.Instrumentation;

public abstract class Module {

    public abstract void start(String serverType, Instrumentation instrumentation) throws Throwable;

    public  abstract void release(String mode) throws Throwable;
}