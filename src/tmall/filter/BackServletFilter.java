package tmall.filter;

import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * 设计思路是 根据浏览器返回的url上的参数来调用方法
 * 用filter过滤
 * 例如/admin_category_list
 * 判断是否以admin开头，是的话 取出后两个字母，是category
 * 去category下调用list方法
 * */

public class BackServletFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取上下文路径
        String contextPath = request.getServletContext().getContextPath();
        String uri = request.getRequestURI();
        uri = StringUtils.remove(uri, contextPath);

        if (uri.startsWith("/admin_")){
            //取出中间的元素
            String servletPath = StringUtils.substringBetween(uri,"_","_")+"Servlet";
            //取出方法
            String method = StringUtils.substringAfterLast(uri,"_");
            request.setAttribute("method", method);
            servletRequest.getRequestDispatcher("/"+servletPath).forward(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }



}
