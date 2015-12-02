package com.weapon.smm3.filter;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by weapon on 2015-11-25.
 */
public class XssFilter  implements Filter {
    FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    private static final List<String> xssFilterPath = Lists.newArrayList();
    static {
        //�����ϸ���˵�·��
        xssFilterPath.add("/api/supply/add");
        xssFilterPath.add("/brand/req/save");
        xssFilterPath.add("/front/match/add");
        xssFilterPath.add("/front/match/addfuture");
        xssFilterPath.add("/login/member/post");
        xssFilterPath.add("/login/member/registe/post");
    }

    private boolean isUri(String path) {
        return StringUtils.startsWithAny(path, xssFilterPath.toArray(new String[0]));
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();
        if (path.indexOf("/error/") != -1 || path.indexOf("/static/") != -1 || path.indexOf("/inc/") != -1 || path.contains("/sys/")) {
            chain.doFilter(request, response);
        } else {
            if (isUri(path)) {
                if (XssFilterStringUtils.isFoundCSRF(req)) {//��⵽CSRF����ֱ����ת
                    res.sendRedirect("/static/400.html");
                } else if (XssFilterStringUtils.isFoundXss(req)) {//��⵽xssֱ����ת
                    res.sendRedirect("/static/400.html");
                } else {
                    chain.doFilter(new XssLevel2Wrapper(req), res);
                }
            } else {
                chain.doFilter(new XssHttpServletRequestWrapper(req), res); //��XSS����
            }

        }
    }
}
