package com.weapon.smm3.filter;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by weapon on 2015-11-25.
 */
public class XssFilterStringUtils {
    public static String cleanXSS(String value) {
        if (StringUtils.isNotBlank(value)) {
            value = StringUtils.trim(value);
            value = value.replaceAll("\\<.*script.*\\>", "");
            value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            //value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
            value = value.replaceAll("'", "&#39;");
            //value = value.replaceAll("\"", "&#34;");
            value = value.replaceAll("eval\\((.*)\\)", "");
            value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        }
        return value;
    }

    /**
     * @param @param  value
     * @param @return �趨�ļ�
     * @return String    ��������
     * @throws
     * @Title: cancelXSSFilter
     * @Description: ȡ��XSS���ˣ����ǵ���Щ������html���ݣ�����ͨ���÷����ָ�������ǰ״̬��
     */
    public static String cancelXSSFilter(String value) {
        if (StringUtils.isNotBlank(value)) {
            value = value.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
            value = value.replaceAll("&#40;", "\\(").replaceAll("&#41;", "\\)");
            value = value.replaceAll("&#39;", "'").replaceAll("&#34;", "\"");
        }
        return value;
    }

    /**
     * ɾ���ر��ַ����Է�Xss
     * ע�⣺���ɻָ�������Ҫ���ϸ����ʱ��ʹ��
     * ��Ҫ��������paramΪString����ʱʹ��
     *
     * @param value
     * @return
     */
    public static String delXssChar(String value) {
        // & --> &amp;
        // < --> &lt;
        // > --> &gt;
        // " --> &quot;
        // ' --> &#x27;
        // / --> &#x2F;

        if (StringUtils.isNotBlank(value)) {
            value = value.replace("&#x2F;", "");
            value = value.replace("&#x27;", "");
            value = value.replace("&quot;", "");
            value = value.replace("&amp;", "");
            value = value.replace("&lt;", "");
            value = value.replace("&gt;", "");
            value = value.replace("&#39;", "");

            for (Pattern pattern : xssPatternList) {
                value = pattern.matcher(value).replaceAll("");
            }

            return value;
        }
        return "";
    }

    //Xss���˹���
    private static List<Pattern> xssPatternList = new LinkedList();
    static {
        xssPatternList.add(Pattern.compile("<.*script.*>|onmouseover|prompt|document.cookie|alert", Pattern.CASE_INSENSITIVE));
        xssPatternList.add(Pattern.compile("[\\\"\\'][\\s]*javascript:(.*)[\\\"\\']", Pattern.CASE_INSENSITIVE));
        xssPatternList.add(Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        xssPatternList.add(Pattern.compile("[()'<>\\\\]"));
    }

    public static boolean matches(String value){
        if(value==null){
            return false;
        }
        for (Pattern pattern : xssPatternList) {
            if (pattern.matcher(value).find()) {
                //System.out.println(" match by pattern = [" + pattern + "] value= " + value);
                return true;
            }
        }
        return false;
    }

    public static boolean isFoundXss(HttpServletRequest request) {
        // ��ʼheaderУ�飬��header��Ϣ����У��
     /*   if (this.checkHeader(request)) {
           return true;
        }*/
        // ��ʼparameterУ�飬��parameter��Ϣ����У��
        if (checkParameter(request)) {
            return true;
        }
        //��url����У��
        if (checkUri(request)) {
            return true;
        }
        return false;
    }



    /**
     * û��Υ������ݣ��ͷ���false;
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    private static boolean checkHeader(HttpServletRequest request){
        Enumeration<String> headerParams = request.getHeaderNames();
        while(headerParams.hasMoreElements()){
            String headerName = headerParams.nextElement();
            String headerValue = request.getHeader(headerName);
            if(matches(headerValue)){
                return true;
            }
        }
        return false;
    }

    /**
     * ���URI�Ƿ�Ƿ�
     * @param request
     * @return û�оͷ���false
     */
    private static boolean checkUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (StringUtils.isNotBlank(uri)) {
            return matches(uri);
        }
        return false;
    }

    /**
     * û��Υ������ݣ��ͷ���false;
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    private static boolean checkParameter(HttpServletRequest request){
        Enumeration<String> names = request.getParameterNames();
        boolean isFound = false;
        while (names.hasMoreElements() && !isFound) {
            String name = names.nextElement();
            String[] values = request.getParameterValues(name);
            if (values.length > 0) {
                for (String value : values) {
                    if (matches(value)) {
                        isFound = true;
                        break;
                    }
                }
            }
        }
        return isFound;
    }


    /**
     * ���CSRF����
     * @param request
     * @return û�оͷ���false
     */
    @SuppressWarnings("unchecked")
    public static boolean isFoundCSRF(HttpServletRequest request) {

        List<String> myDomainList = Lists.newArrayList();
        myDomainList.add("localhost");
        myDomainList.add("127.0.0.1");
        myDomainList.add("192.168.");
        myDomainList.add("fiidee.com");
        myDomainList.add("fiidee.inet");
        myDomainList.add("dayi.fiidee.inet");
        myDomainList.add("dayi35.com");
        myDomainList.add("test.spot.dayi35.com");
        myDomainList.add("pvc123.com");
        myDomainList.add("hjr360.com");
        myDomainList.add("tuonews.com");
        myDomainList.add("cr6868.com"); //���ŷ��ͷ�

        String referer = ObjectUtils.toString(request.getHeader("Referer"), "");

        if(StringUtils.isBlank(referer)) return false;//��ַ��ֱ��������ַ�����CSRF

        for(String myDomain : myDomainList) {
            if(referer.contains(myDomain)) {
                return false;
            }
        }

        return true;
    }
}
