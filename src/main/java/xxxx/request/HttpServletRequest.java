package xxxx.request;

import org.xxxx.utils.Reflections;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public final class HttpServletRequest extends AbstractRequest {
    private static final Map<String, String[]> EMPTY_PARAM = new HashMap();
    private static final Pattern PATTERN = Pattern.compile("\\d+(\\.\\d+)*");

    public HttpServletRequest(Object request) {
        super(request);
    }

    public HttpServletRequest(Object request, String requestId) {
        super(request, requestId);
    }

    public String getLocalAddr() {
        return Reflections.invokeStringMethod(this.request, "getLocalAddr", EMPTY_CLASS, new Object[0]);
    }

    public String getMethod() {
        return Reflections.invokeStringMethod(this.request, "getMethod", EMPTY_CLASS, new Object[0]);
    }

    public String getProtocol() {
        return Reflections.invokeStringMethod(this.request, "getProtocol", EMPTY_CLASS, new Object[0]);
    }

    public String getAuthType() {
        return Reflections.invokeStringMethod(this.request, "getAuthType", EMPTY_CLASS, new Object[0]);
    }

    public String getContentType() {
        return Reflections.invokeStringMethod(this.request, "getContentType", EMPTY_CLASS, new Object[0]);
    }

    public String getContextPath() {
        return Reflections.invokeStringMethod(this.request, "getContextPath", EMPTY_CLASS, new Object[0]);
    }

    public String getRemoteAddr() {
        return Reflections.invokeStringMethod(this.request, "getRemoteAddr", EMPTY_CLASS, new Object[0]);
    }

    public String getRequestURI() {
        return Reflections.invokeStringMethod(this.request, "getRequestURI", EMPTY_CLASS, new Object[0]);
    }

    public StringBuffer getRequestURL() {
        try {
            Object ret = Reflections.invokeMethod(this.request, "getRequestURL", EMPTY_CLASS, new Object[0]);
            return ret != null ? (StringBuffer)ret : null;
        } catch (Throwable var2) {
            return null;
        }
    }

    public String getServerName() {
        return Reflections.invokeStringMethod(this.request, "getServerName", EMPTY_CLASS, new Object[0]);
    }

    public String getParameter(String key) {
        return  Reflections.invokeStringMethod(this.request, "getParameter", STRING_CLASS, new Object[]{key});
    }

    public Enumeration<String> getParameterNames() {
        Object ret = Reflections.invokeMethod(this.request, "getParameterNames", EMPTY_CLASS, new Object[0]);
        return ret != null ? (Enumeration)ret : null;

    }

    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> normalMap = null;
        if (this.canGetParameter || this.setCharacterEncodingFromConfig()) {
            normalMap = (Map)Reflections.invokeMethod(this.request, "getParameterMap", EMPTY_CLASS, new Object[0]);
        }

        return this.getMergeMap(normalMap, this.formItemCache);
    }

    public String getHeader(String key) {
        return Reflections.invokeStringMethod(this.request, "getHeader", STRING_CLASS, new Object[]{key});
    }

    public Enumeration<String> getHeaderNames() {
        Object ret = Reflections.invokeMethod(this.request, "getHeaderNames", EMPTY_CLASS, new Object[0]);
        return ret != null ? (Enumeration)ret : null;
    }

    public String getQueryString() {
        return Reflections.invokeStringMethod(this.request, "getQueryString", EMPTY_CLASS, new Object[0]);
    }


    public static String extractType(String serverInfo) {
        if (serverInfo == null) {
            return null;
        } else {
            serverInfo = serverInfo.toLowerCase();
            if (serverInfo.contains("tomcat")) {
                return "Tomcat";
            } else if (serverInfo.contains("jboss")) {
                return "JBoss";
            } else {
                return serverInfo.contains("jetty") ? "Jetty" : serverInfo;
            }
        }
    }

    public String getAppBasePath() {
        try {
            String realPath;
            realPath = Reflections.invokeStringMethod(this.request, "getRealPath", new Class[]{String.class}, new Object[]{"/"});
            return realPath == null ? "" : realPath;
        } catch (Exception var2) {
            return "";
        }
    }

    public String getCharacterEncoding() {
        return Reflections.invokeStringMethod(this.request, "getCharacterEncoding", EMPTY_CLASS, new Object[0]);
    }


    private Map<String, String[]> getMergeMap(Map<String, String[]> map1, Map<String, String[]> map2) {
        if (map1 == null && map2 == null) {
            return null;
        } else {
            Map<String, String[]> result = new HashMap();
            if (map1 != null && !map1.isEmpty()) {
                this.mergeMap(map1, result);
            }

            if (map2 != null && !map2.isEmpty()) {
                this.mergeMap(map2, result);
            }

            return result;
        }
    }

    private void mergeMap(Map<String, String[]> src, Map<String, String[]> dst) {
        try {
            Iterator i$ = src.entrySet().iterator();

            while(i$.hasNext()) {
                Entry<String, String[]> entry = (Entry)i$.next();
                if (dst.containsKey(entry.getKey())) {
                    dst.put(entry.getKey(), this.mergeArray((String[])dst.get(entry.getKey()), (String[])entry.getValue()));
                } else {
                    dst.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (Throwable var5) {
        }

    }

    private String[] mergeArray(String[] s1, String[] s2) {
        int str1Length = s1.length;
        int str2length = s2.length;
        s1 = (String[])Arrays.copyOf(s1, str1Length + str2length);
        System.arraycopy(s2, 0, s1, str1Length, str2length);
        return s1;
    }

}
