package tmall.servlet;

import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.Page;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PropertyServlet")
public class PropertyServlet extends BaseBackServlet {

    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        //先获取cid 获取属性值所在的产品分类 及 属性值
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDAO.get(cid);
        String name = request.getParameter("name");
        //创建一个属性值 设置值 以及调用propertyDAO的添加属性
        Property p = new Property();
        p.setCategory(c);
        p.setName(name);
        propertyDAO.add(p);

        //跳转到分类所在的界面
        return "@admin_property_list?cid=" + cid;
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        //获取属性值id
        int id = Integer.parseInt(request.getParameter("id"));
        //保存一个副本 获取方便获取分类id 好返回原来的分类
        Property p = propertyDAO.get(id);
        //删除
        propertyDAO.delete(id);
        return "@admin_property_list?cid="+p.getCategory().getId();
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {

        int id = Integer.parseInt(request.getParameter("id"));
        Property p = propertyDAO.get(id);
        request.setAttribute("p", p);

        return "admin/editProperty.jsp";
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDAO.get(cid);

        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        Property p = new Property();
        p.setCategory(c);
        p.setName(name);
        p.setId(id);
        propertyDAO.update(p);

        return "@admin_property_list?cid="+p.getCategory().getId();
    }

    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        //获取某一个分类的属性列表
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDAO.get(cid);
        List<Property> ps = propertyDAO.list(cid, page.getStart(), page.getCount());
        //获取一分类属性的总个数 方便计算分页页数
        int total = propertyDAO.getTotal(cid);
        page.setTotal(total);
        page.setParam("&cid="+c.getId());

        //属性集合赋值到ps上
        request.setAttribute("ps", ps);
        //分类复制到c上
        request.setAttribute("c", c);
        //当前分页信息赋值到page上
        request.setAttribute("page", page);

        return "admin/listProperty.jsp";
    }
}
