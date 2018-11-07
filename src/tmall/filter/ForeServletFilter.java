package tmall.filter;

import org.apache.commons.lang.StringUtils;
import tmall.DAO.CategoryDAO;
import tmall.DAO.OrderItemDAO;
import tmall.bean.Category;
import tmall.bean.Order;
import tmall.bean.OrderItem;
import tmall.bean.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebFilter(filterName = "ForeServletFilter")
public class ForeServletFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String contextPath = request.getServletContext().getContextPath();
        request.getServletContext().setAttribute("contextPath", contextPath);

        User user = (User) request.getSession().getAttribute("user");
        int cartTotalItemNumber = 0;
        if (null != user){
            List<OrderItem> ois = new OrderItemDAO().listByUser(user.getId());
            for (OrderItem oi : ois) {
                cartTotalItemNumber += oi.getNumber();
            }
        }
        request.setAttribute("cartTotalItemNumber", cartTotalItemNumber);

        List<Category> cs = (List<Category>) request.getAttribute("cs");
        if (null == cs){
            cs = new CategoryDAO().list();
            request.setAttribute("cs", cs);
        }

        String uri = request.getRequestURI();
//        System.out.println("this is :" +contextPath);
        uri = StringUtils.remove(uri, contextPath);
//        System.out.println("this is :" +uri);
        if(uri.startsWith("/fore")&&!uri.startsWith("/foreServlet")){
            String method = StringUtils.substringAfterLast(uri,"/fore");
            request.setAttribute("method", method);
            req.getRequestDispatcher("/foreServlet").forward(request, response);
            return;
        }

        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
