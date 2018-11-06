package tmall.DAO;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductImageDAO {
    /*
    * 定义单个图片和详情图片
    * */
    public static final String type_single = "type_single";
    public static final String type_detail = "type_detail";

    /*
     * 查询图片数量
     * */
    public int getTotal(){
        int total = 0;
        try(Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select count(*) from ProductImage";
            ResultSet rs = s.executeQuery(sql);
            while(rs.next()){
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in getTotal() at productImageDAO getTotal");
        }
        return  total;
    }

    /*
     * 添加新属性
     * */
    public void add(ProductImage bean) {
        String sql = "insert into ProductImage values(null, ?, ?)";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, bean.getProduct().getId());
            ps.setString(2, bean.getType());
            ps.execute();
            //获取主键 如果返回成功，从数据库获取id 给Category bean
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()){
                int id = rs.getInt(1);
                bean.setId(id);
            }
        }catch (SQLException e ){
            e.printStackTrace();
            System.out.println("error in getTotal() at productImageDAO add ");
        }
    }

    /*
     * 修改图片
     * */
    public void update(ProductImage bean) {

    }

    /*
     * 删除图片
     * */
    public void delete(int id) {
        try(Connection c = DBUtil.getConnection(); Statement s = c.createStatement()){
            String sql = "delete from ProductImage where id = " + id;
            s.execute(sql);
        } catch (SQLException e ) {
            e.printStackTrace();
            System.out.println("error in getTotal() at productImageDAO delete");
        }
    }

    /*
    * 根据id获取图片
    * */
    public ProductImage get(int id) {
        ProductImage bean = new ProductImage();

        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {

            String sql = "select * from ProductImage where id = " + id;

            ResultSet rs = s.executeQuery(sql);

            if (rs.next()) {
                int pid = rs.getInt("pid");
                String type = rs.getString("type");
                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);
                bean.setType(type);
                bean.setId(id);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return bean;
    }

    /*
    * 获取产品分类下所有type的图片
    * */
    public List<ProductImage> list(Product p, String type) {
        return list(p, type,0, Short.MAX_VALUE);
    }

    /*获取产品分类下所有type的图片
    *
    * */
    public List<ProductImage> list(Product p, String type, int start, int count) {
        List<ProductImage> beans = new ArrayList<ProductImage>();

        String sql = "select * from ProductImage where pid =? and type =? order by id desc limit ?,? ";

        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setInt(1, p.getId());
            ps.setString(2, type);

            ps.setInt(3, start);
            ps.setInt(4, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                ProductImage bean = new ProductImage();
                int id = rs.getInt(1);

                bean.setProduct(p);
                bean.setType(type);
                bean.setId(id);

                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }


}
