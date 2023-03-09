package org.xxxx.utils;

import org.xxxx.javassist.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class JavassistUtil {

    public void insertBefore(CtClass ctClass, String methodName, String desc, String src) throws NotFoundException, CannotCompileException {
        LinkedList<CtBehavior> methods = this.getMethod(ctClass, methodName, desc, (String)null);
        if (methods != null && methods.size() > 0) {
            this.insertBefore(methods, src);
        }

    }

    public void setBody(CtClass ctClass, String methodName, String desc, String src) throws NotFoundException, CannotCompileException {
        LinkedList<CtBehavior> methods = this.getMethod(ctClass, methodName, desc, (String)null);
        if (methods != null && methods.size() > 0) {
            this.setBody(methods, src);
        }
    }


    private void setBody(LinkedList<CtBehavior> methods, String src) throws CannotCompileException {
        Iterator i$ = methods.iterator();

        while(i$.hasNext()) {
            CtBehavior method = (CtBehavior)i$.next();
            if (method != null) {
                this.setBody(method, src);
            }
        }
    }

    public void insertAfter(CtClass invokeClass, String methodName, String desc, String src) throws NotFoundException, CannotCompileException {
        this.insertAfter(invokeClass, methodName, desc, src, false);
    }

    public void insertAfter(CtClass ctClass, String methodName, String desc, String src, boolean asFinally) throws NotFoundException, CannotCompileException {
        LinkedList<CtBehavior> methods = this.getMethod(ctClass, methodName, desc, (String)null);
        if (methods != null && methods.size() > 0) {
            Iterator i$ = methods.iterator();

            while(i$.hasNext()) {
                CtBehavior method = (CtBehavior)i$.next();
                if (method != null) {
                    this.insertAfter(method, src, asFinally);
                }
            }
        }

    }

    public void insertAfter(CtBehavior method, String src, boolean asFinally) throws CannotCompileException {
        method.insertAfter(src, asFinally);
    }


    private void insertBefore(LinkedList<CtBehavior> methods, String src) throws CannotCompileException {
        Iterator i$ = methods.iterator();

        while(i$.hasNext()) {
            CtBehavior method = (CtBehavior)i$.next();
            if (method != null) {
                this.insertBefore(method, src);
            }
        }

    }

    public void insertBefore(CtClass ctClass, String methodName, String src, String[] allDesc) throws NotFoundException, CannotCompileException {
        String[] arr$ = allDesc;
        int len$ = allDesc.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String desc = arr$[i$];
            this.insertBefore(ctClass, methodName, desc, src);
        }

    }


    private LinkedList<CtBehavior> getConstructor(CtClass ctClass, String desc) throws NotFoundException {
        LinkedList<CtBehavior> methods = new LinkedList();
        if (desc == null || desc.length() == 0) {
            Collections.addAll(methods, ctClass.getDeclaredConstructors());
        } else {
            methods.add(ctClass.getConstructor(desc));
        }

        return methods;
    }

    protected LinkedList<CtBehavior> getMethod(CtClass ctClass, String methodName, String desc, String excludeDesc) throws NotFoundException {
        if ("<init>".equals(methodName)) {
            return this.getConstructor(ctClass, desc);
        } else {
            LinkedList<CtBehavior> methods = new LinkedList();
            if (desc == null || desc.length() == 0) {
                CtMethod[] allMethods = ctClass.getDeclaredMethods();
                if (allMethods != null) {
                    CtMethod[] arr$ = allMethods;
                    int len$ = allMethods.length;

                    for(int i$ = 0; i$ < len$; ++i$) {
                        CtMethod method = arr$[i$];
                        if (method != null && !method.isEmpty() && method.getName().equals(methodName) && !method.getSignature().equals(excludeDesc)) {
                            methods.add(method);
                        }
                    }
                }
            } else {
                CtMethod ctMethod = ctClass.getMethod(methodName, desc);
                if (ctMethod != null && !ctMethod.isEmpty()) {
                    methods.add(ctMethod);
                }
            }

            return methods;
        }
    }

    public void insertBefore(CtBehavior method, String src) throws CannotCompileException {
        method.insertBefore(src);
    }

    public void setBody(CtBehavior method, String src) throws CannotCompileException {
        method.setBody(src);
    }
}
