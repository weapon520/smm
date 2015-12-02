package com.weapon.smm3.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by weapon on 2015-11-25.
 */
public class EmojiFilter implements Filter {
    FilterConfig filterConfig = null;


    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }


    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getRequestURI();
        if (path.indexOf("/s/") != -1 || path.indexOf("/static/") != -1
                || path.indexOf("/inc/") != -1) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(new EmojiHttpServletRequestWrapper((HttpServletRequest) request), response);
        }
    }


    public void destroy() {
        this.filterConfig = null;
    }
}
