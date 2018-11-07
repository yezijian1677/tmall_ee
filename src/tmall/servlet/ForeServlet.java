package tmall.servlet;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.web.util.HtmlUtils;
import tmall.DAO.CategoryDAO;
import tmall.DAO.OrderDAO;
import tmall.DAO.ProductDAO;
import tmall.DAO.ProductImageDAO;
import tmall.bean.*;
import tmall.comparator.*;
import tmall.util.Page;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(name = "ForeServlet")
public class ForeServlet extends BaseForeServlet {

    /*
    * 填充导航页的分类内容
    * */
    public String home(HttpServletRequest request, HttpServletResponse response, Page page) {
        List<Category> cs = new CategoryDAO().list();
        new ProductDAO().fill(cs);
        new ProductDAO().fillByRow(cs);
        request.setAttribute("cs", cs);
        return "home.jsp";
    }

    /*
    * 注册
    * */
    public String register(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        name = HtmlUtils.htmlEscape(name);
        System.out.println(name);
        boolean exist = userDAO.isExist(name);

        if (exist) {
            request.setAttribute("msg", "用户名已经被使用");
            return "register.jsp";
        }
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        System.out.println(user.getName());
        System.out.println(user.getPassword());
        userDAO.add(user);

        return "@registerSuccess.jsp";
    }

    /*
    * 登录
    * */
    public String login(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        name = HtmlUtils.htmlEscape(name);
        String password = request.getParameter("password");
        User user = userDAO.get(name, password);

        if (null == user){
            request.setAttribute("msg","账号密码错误");
            return "login.jsp";
        }
        request.getSession().setAttribute("user", user);
        return "@forehome";
    }

    /*
    * 退出
    * */
    public String logout(HttpServletRequest request, HttpServletResponse response, Page page) {
        request.getSession().removeAttribute("user");
        return "@forehome";
    }

    /*
    * 产品详情页
    * */
    public String product(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = productDAO.get(pid);

        List<ProductImage> productSingleImages = productImageDAO.list(p, ProductImageDAO.type_single);
        List<ProductImage> productDetailImages = productImageDAO.list(p, ProductImageDAO.type_detail);

        p.setProductSingleImages(productSingleImages);
        p.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueDAO.list(p.getId());

        List<Review> reviews = reviewDAO.list(p.getId());

        productDAO.setSaleAndReviewNumber(p);

        request.setAttribute("reviews", reviews);

        request.setAttribute("p", p);
        request.setAttribute("pvs", pvs);
        return "product.jsp";
    }

    /*
    * 检查登录
    * */
    public String checkLogin(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user = (User) request.getSession().getAttribute("user");
        if (null != user)
            return "%success";
        return "%fail";
    }

    /*
    * loginAjax
    * */
    public String loginAjax(HttpServletRequest request, HttpServletResponse response, Page page) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        User user = userDAO.get(name, password);
        if (null == user){
            return  "%fail";
        }
        request.getSession().setAttribute("user", user);
        return "%success";
    }

    /*
    * 填充分类
    * */
    public String category(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));

        Category c = new CategoryDAO().get(cid);
        new ProductDAO().fill(c);
        new ProductDAO().setSaleAndReviewNumber(c.getProducts());

        String sort = request.getParameter("sort");
        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(c.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(c.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(c.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(),new ProductAllComparator());
                    break;
            }
        }

        request.setAttribute("c", c);
        return "category.jsp";
    }

    /*
    * 搜索
    * */
    public String search(HttpServletRequest request, HttpServletResponse response, Page page){
        String keyword = request.getParameter("keyword");
        List<Product> ps= new ProductDAO().search(keyword,0,20);
        productDAO.setSaleAndReviewNumber(ps);
        request.setAttribute("ps",ps);
        return "searchResult.jsp";
    }

    /*
    * 立即购买
    * */
    public String buyone(HttpServletRequest request, HttpServletResponse response, Page page){
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        Product p = productDAO.get(pid);
        int oiid = 0;

        User user = (User) request.getSession().getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for(OrderItem oi : ois){
            if(oi.getProduct().getId() == p.getId()){
                oi.setNumber(oi.getNumber()+num);
                found = true;
                oiid = oi.getId();
                break;
            }
        }
        if (!found){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setNumber(num);
            oi.setProduct(p);
            orderItemDAO.add(oi);
            oiid = oi.getId();
        }
        return "@forebuy?oiid="+oiid;
    }

    /*
    * 购买
    * */
    public String buy(HttpServletRequest request, HttpServletResponse response, Page page){
        String[] oiids = request.getParameterValues("oiid");
        List<OrderItem> ois = new ArrayList<>();
        float total = 0;

        for(String strid : oiids){
            int oiid = Integer.parseInt(strid);
            OrderItem oi = orderItemDAO.get(oiid);
            total += oi.getProduct().getPromotePrice()*oi.getNumber();
            ois.add(oi);
        }
        request.getSession().setAttribute("ois", ois);
        request.setAttribute("total", total);
        return  "buy.jsp";
    }

    /*
    * 加入购物车
    * */
    public String addCart(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = productDAO.get(pid);
        int num = Integer.parseInt(request.getParameter("num"));

        User user =(User) request.getSession().getAttribute("user");
        boolean found = false;

        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==p.getId()){
                oi.setNumber(oi.getNumber()+num);
                orderItemDAO.update(oi);
                found = true;
                break;
            }
        }

        if(!found){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setNumber(num);
            oi.setProduct(p);
            orderItemDAO.add(oi);
        }
        return "%success";
    }

    /*
    * 查看购物车
    * */
    public String cart(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user =(User) request.getSession().getAttribute("user");
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        request.setAttribute("ois", ois);
        return "cart.jsp";
    }

    /*
    * 调整订单商品数量
    * */
    public String changeOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user =(User) request.getSession().getAttribute("user");
        if(null==user)
            return "%fail";

        int pid = Integer.parseInt(request.getParameter("pid"));
        int number = Integer.parseInt(request.getParameter("number"));
        List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==pid){
                oi.setNumber(number);
                orderItemDAO.update(oi);
                break;
            }

        }
        return "%success";
    }

    /*
    * 删除订单项
    * */
    public String deleteOrderItem(HttpServletRequest request, HttpServletResponse response, Page page){
        User user =(User) request.getSession().getAttribute("user");
        if(null==user)
            return "%fail";
        int oiid = Integer.parseInt(request.getParameter("oiid"));
        orderItemDAO.delete(oiid);
        return "%success";
    }

    /*
    * 生成订单
    * */
    public String createOrder(HttpServletRequest request, HttpServletResponse response, Page page){
        User user =(User) request.getSession().getAttribute("user");

        List<OrderItem> ois= (List<OrderItem>) request.getSession().getAttribute("ois");
        if(ois.isEmpty())
            return "@login.jsp";

        String address = request.getParameter("address");
        String post = request.getParameter("post");
        String receiver = request.getParameter("receiver");
        String mobile = request.getParameter("mobile");
        String userMessage = request.getParameter("userMessage");

        Order order = new Order();
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) +RandomUtils.nextInt(10000);

        order.setOrderCode(orderCode);
        order.setAddress(address);
        order.setPost(post);
        order.setReceiver(receiver);
        order.setMobile(mobile);
        order.setUserMessage(userMessage);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderDAO.waitPay);

        orderDAO.add(order);
        float total =0;
        for (OrderItem oi: ois) {
            oi.setOrder(order);
            orderItemDAO.update(oi);
            total+=oi.getProduct().getPromotePrice()*oi.getNumber();
        }

        return "@forealipay?oid="+order.getId() +"&total="+total;
    }

    /*
    * 我的订单
    * */
    public String bought(HttpServletRequest request, HttpServletResponse response, Page page) {
        User user =(User) request.getSession().getAttribute("user");
//        查询user所有的状态不是"delete" 的订单集合os
        List<Order> os= orderDAO.list(user.getId(),OrderDAO.delete);
//        为这些订单填充订单项
        orderItemDAO.fill(os);

        request.setAttribute("os", os);

        return "bought.jsp";
    }

    /*
    * 确认支付
    * */
    public String confirmPay(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);
        orderItemDAO.fill(o);
        request.setAttribute("o", o);
        return "confirmPay.jsp";
    }

    /*
    * 确认收货
    * */
    public String orderConfirmed(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);
        o.setStatus(OrderDAO.waitReview);
        o.setConfirmDate(new Date());
        orderDAO.update(o);
        return "orderConfirmed.jsp";
    }

    /*
    * 删除订单
    * */
    public String deleteOrder(HttpServletRequest request, HttpServletResponse response, Page page){
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);
        o.setStatus(OrderDAO.delete);
        orderDAO.update(o);
        return "%success";
    }

    /*
    * 评论
    * */
    public String review(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);
        orderItemDAO.fill(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewDAO.list(p.getId());
        productDAO.setSaleAndReviewNumber(p);
        request.setAttribute("p", p);
        request.setAttribute("o", o);
        request.setAttribute("reviews", reviews);
        return "review.jsp";
    }

    /*
    * 提交评论
    * */
    public String doreview(HttpServletRequest request, HttpServletResponse response, Page page) {
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDAO.get(oid);
        o.setStatus(OrderDAO.finish);
        orderDAO.update(o);
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = productDAO.get(pid);

        String content = request.getParameter("content");

        content = HtmlUtils.htmlEscape(content);

        User user =(User) request.getSession().getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewDAO.add(review);

        return "@forereview?oid="+oid+"&showonly=true";
    }


}

