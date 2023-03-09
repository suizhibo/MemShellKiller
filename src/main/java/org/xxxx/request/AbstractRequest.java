package org.xxxx.request;


import org.xxxx.agent.Config;
import org.xxxx.utils.Reflections;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public abstract class AbstractRequest {
    protected static final Class[] EMPTY_CLASS = new Class[0];
    protected static final Class[] STRING_CLASS = new Class[]{String.class};
    protected Object request;
    protected Object inputStream;
    protected Object charReader;
    protected ByteArrayOutputStream bodyOutputStream;
    protected CharArrayWriter bodyWriter;
    protected int maxBodySize;
    protected String requestId;
    protected boolean canGetParameter;
    protected HashMap<String, String[]> formItemCache;

    public AbstractRequest() {
        this((Object)null);
    }

    public AbstractRequest(Object request) {
        this.inputStream = null;
        this.charReader = null;
        this.bodyOutputStream = null;
        this.bodyWriter = null;
        this.maxBodySize = 4096;
        this.canGetParameter = false;
        this.formItemCache = null;
        this.request = request;
        this.requestId = UUID.randomUUID().toString().replace("-", "");
    }

    public AbstractRequest(Object request, String requestId) {
        this.inputStream = null;
        this.charReader = null;
        this.bodyOutputStream = null;
        this.bodyWriter = null;
        this.maxBodySize = 4096;
        this.canGetParameter = false;
        this.formItemCache = null;
        this.request = request;
        this.requestId = requestId;
    }

    public AbstractRequest(int request) {
        this.inputStream = null;
        this.charReader = null;
        this.bodyOutputStream = null;
        this.bodyWriter = null;
        this.maxBodySize = 4096;
        this.canGetParameter = false;
        this.formItemCache = null;
    }

    public boolean isCanGetParameter() {
        return this.canGetParameter;
    }

    public void setCanGetParameter(boolean canGetParameter) {
        this.canGetParameter = canGetParameter;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public Object getRequest() {
        return this.request;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public abstract String getLocalAddr();

    public abstract String getMethod();

    public abstract String getProtocol();

    public abstract String getAuthType();

    public abstract String getContextPath();

    public abstract String getRemoteAddr();

    public abstract String getRequestURI();

    public abstract StringBuffer getRequestURL();

    public String getRequestURLString() {
        Object ret = this.getRequestURL();
        return ret != null ? ret.toString() : null;
    }


    public abstract String getParameter(String var1);

    public abstract Enumeration<String> getParameterNames();

    public abstract Map<String, String[]> getParameterMap();

    public abstract String getHeader(String var1);

    public abstract Enumeration<String> getHeaderNames();

    public String[] getHeadersArray() {
        ArrayList<String> headers = new ArrayList();
        Enumeration<String> headerNames = this.getHeaderNames();
        if (headerNames != null) {
            while(headerNames.hasMoreElements()) {
                String key = (String)headerNames.nextElement();
                String value = this.getHeader(key);
                headers.add(key.toLowerCase());
                headers.add(value);
            }
        }

        return (String[])headers.toArray(new String[0]);
    }

    public abstract String getQueryString();

    public abstract String getAppBasePath();

    public abstract String getContentType();



    public byte[] getBody() {
        return this.bodyOutputStream != null ? this.bodyOutputStream.toByteArray() : null;
    }

    public String getStringBody() {
        if (this.bodyOutputStream != null) {
            byte[] body = this.bodyOutputStream.toByteArray();
            if (body != null) {
                String encoding = this.getCharacterEncoding();
                if (encoding != null || encoding.length() > 0) {
                    try {
                        return new String(body, encoding);
                    } catch (UnsupportedEncodingException var4) {
                        return new String(body);
                    }
                } else {
                    return new String(body);
                }
            } else {
                return null;
            }
        } else {
            return this.bodyWriter != null ? this.bodyWriter.toString() : null;
        }
    }

    public ByteArrayOutputStream getBodyStream() {
        return this.bodyOutputStream;
    }

    public Object getInputStream() {
        return this.inputStream;
    }

    public void setInputStream(Object inputStream) {
        this.inputStream = inputStream;
    }

    public Object getCharReader() {
        return this.charReader;
    }

    public void setCharReader(Object charReader) {
        this.charReader = charReader;
    }

    public void appendByteBody(int b) {
        if (this.bodyOutputStream == null) {
            this.bodyOutputStream = new ByteArrayOutputStream();
        }

        if (this.bodyOutputStream.size() < this.maxBodySize) {
            this.bodyOutputStream.write(b);
        }

    }

    public void appendBody(byte[] bytes, int offset, int len) {
        if (this.bodyOutputStream == null) {
            this.bodyOutputStream = new ByteArrayOutputStream();
        }

        len = Math.min(len, this.maxBodySize - this.bodyOutputStream.size());
        if (len > 0) {
            this.bodyOutputStream.write(bytes, offset, len);
        }

    }

    public void appendBody(char[] cbuf, int offset, int len) {
        if (this.bodyWriter == null) {
            this.bodyWriter = new CharArrayWriter();
        }

        len = Math.min(len, this.maxBodySize / 2 - this.bodyWriter.size());
        if (len > 0) {
            this.bodyWriter.write(cbuf, offset, len);
        }

    }

    public void appendCharBody(int b) {
        if (this.bodyWriter == null) {
            this.bodyWriter = new CharArrayWriter();
        }

        if (this.bodyWriter.size() < this.maxBodySize / 2) {
            this.bodyWriter.write(b);
        }

    }

    public abstract String getCharacterEncoding();



    protected boolean setCharacterEncodingFromConfig() {
        try {
            String paramEncoding = Config.properties.getProperty("param-encoding");
            if (paramEncoding != null || paramEncoding.length() > 0) {
                Reflections.invokeMethod(this.request, "setCharacterEncoding", STRING_CLASS, new Object[]{paramEncoding});
                return true;
            }
        } catch (Exception var2) {
        }

        return false;
    }

}
