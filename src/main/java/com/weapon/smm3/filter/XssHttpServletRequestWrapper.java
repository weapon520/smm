package com.weapon.smm3.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by weapon on 2015-11-25.
 */
public class XssHttpServletRequestWrapper  extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String[]> getParameterMap(){
        Map<String, String[]> result = new LinkedHashMap<String, String[]>();
        Enumeration<String> names = this.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            result.put(name, this.getParameterValues(name));
        }
        return result;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values==null)  {
            return null;
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = XssFilterStringUtils.cleanXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (value == null) {
            return null;
        }
        return XssFilterStringUtils.cleanXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null)
            return null;
        return XssFilterStringUtils.cleanXSS(value);
    }
}
