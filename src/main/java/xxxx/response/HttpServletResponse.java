package xxxx.response;


import org.xxxx.utils.Reflections;

public class HttpServletResponse {
    private static final int REDIRECT_STATUS_CODE = 302;
    public static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
    public static final String CONTENT_LENGTH_HEADER_KEY = "Content-Length";
    public static final String CONTENT_TYPE_REPLACE_REQUEST_ID = "%request_id%";
    public static final String CONTENT_TYPE_HTML_VALUE = "text/html";
    public static final String CONTENT_TYPE_JSON_VALUE = "application/json";
    public static final String CONTENT_TYPE_XML_VALUE = "application/xml";
    public static final String CONTENT_TYPE_TEXT_XML = "text/xml";
    private Object response;

    public HttpServletResponse(Object response) {
        this.response = response;
    }

    public Object getResponse() {
        return this.response;
    }

    public void setHeader(String key, String value) {
        if (this.response != null) {
            Reflections.invokeMethod(this.response, "setHeader", new Class[]{String.class, String.class}, new Object[]{key, value});
        }

    }

    public void setIntHeader(String key, int value) {
        if (this.response != null) {
            Reflections.invokeMethod(this.response, "setIntHeader", new Class[]{String.class, Integer.TYPE}, new Object[]{key, value});
        }

    }

    public void addHeader(String key, String value) {
        if (this.response != null) {
            Reflections.invokeMethod(this.response, "addHeader", new Class[]{String.class, String.class}, new Object[]{key, value});
        }

    }

    public String getHeader(String key) {
        if (this.response != null) {
            Object header = Reflections.invokeMethod(this.response, "getHeader", new Class[]{String.class}, new Object[]{key});
            if (header != null) {
                return header.toString();
            }
        }

        return null;
    }

    public String getCharacterEncoding() {
        if (this.response != null) {
            Object enc = Reflections.invokeMethod(this.response, "getCharacterEncoding", new Class[0], new Object[0]);
            if (enc != null) {
                return enc.toString();
            }
        }

        return null;
    }

    public String getContentType() {
        if (this.response != null) {
            Object contentType = Reflections.invokeMethod(this.response, "getContentType", new Class[0], new Object[0]);
            if (contentType != null) {
                return contentType.toString();
            }
        }

        return null;
    }

    public boolean resetBuffer() {
        if (this.response != null) {
            try {
                Reflections.invokeMethod(this.response, "resetBuffer", new Class[0], new Object[0]);
                return true;
            } catch (Exception var2) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean reset() {
        if (this.response != null) {
            try {
                Reflections.invokeMethod(this.response, "reset", new Class[0], new Object[0]);
                return true;
            } catch (Exception var2) {
                return false;
            }
        } else {
            return false;
        }
    }

    public void sendContent(String content, boolean close) {
        Object printer = null;
        printer = Reflections.invokeMethod(this.response, "getWriter", new Class[0], new Object[0]);
        if (printer == null) {
            printer = Reflections.invokeMethod(this.response, "getOutputStream", new Class[0], new Object[0]);
        }

        Reflections.invokeMethod(printer, "print", new Class[]{String.class}, new Object[]{content});
        Reflections.invokeMethod(printer, "flush", new Class[0], new Object[0]);
        if (close) {
            Reflections.invokeMethod(printer, "close", new Class[0], new Object[0]);
        }

    }

    public void sendContent(byte[] bytes, boolean close) {
        Object printer = null;
        printer = Reflections.invokeMethod(this.response, "getOutputStream", new Class[0], new Object[0]);
        Reflections.invokeMethod(printer, "write", new Class[]{byte[].class}, new Object[]{bytes});
        Reflections.invokeMethod(printer, "flush", new Class[0], new Object[0]);
        if (close) {
            Reflections.invokeMethod(printer, "close", new Class[0], new Object[0]);
        }

    }


}
