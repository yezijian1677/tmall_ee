package tmall.DAO;

import tmall.bean.Category;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    /*
    * 查询所有分类的数量
    * */
    public int getTotal() {
        int total = 0;
        try(Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select count(*) from Category";
            ResultSet rs = s.executeQuery(sql);
            while(rs.next()){
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in getTotal() at CategoryDAO getTotal");
        }
        return  total;
    }

    /*
    * 增加分类
    * */
    public void add(Category bean) {
        String sql = "insert into category values(null, ?)";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, bean.getName());
            ps.execute();
            //获取主键 如果返回成功，从数据库获取id 给Category bean
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()){
                int id = rs.getInt(1);
                bean.setId(id);
            }
        }catch (SQLException e ){
            e.printStackTrace();
            System.out.println("error in getTotal() at CategoryDAO add ");
        }
    }

    /*
    * 修改分类
    * */
    public void update(Category bean) {
        String sql = "update category set name = ? where id = ?";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, bean.getName());
            ps.setInt(2, bean.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in getTotal() at CategoryDAO update ");
        }
    }

    /*
    * 删除分类
    * */
    public void delete(int id) {
        try(Connection c = DBUtil.getConnection(); Statement s = c.createStatement()){
            String sql = "delete from category where id = " + id;
            s.execute(sql);
        } catch (SQLException e ) {
            e.printStackTrace();
            System.out.println("error in getTotal() at CategoryDAO delete");
        }
    }

    /*
    * 根据id获取分类
    * */
    public Category get(int id) {
        Category bean = null;

        try(Connection c = DBUtil.getConnection(); Statement s = c.createStatement()){
            String sql = "select * from category where id =" + id;
            ResultSet rs = s.executeQuery(sql);

            if (rs.next()){
                bean = new Category();
                String name = rs.getString(2);
                bean.setName(name);
                bean.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in getTotal() at CategoryDAO getCategory ");
        }

        return  bean;
    }

    /*
    * 分页查询
    * */
    public List<Category> list (int start, int count) {
        List<Category> beans = new ArrayList<Category>();
        String sql = "select * from Category order by id desc limit ?, ?";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, start);
            ps.setInt(2, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                Category bean = new Category();
                int id = rs.getInt(1);
                String name = rs.getString(2);
                bean.setId(id);
                bean.setName(name);
                beans.add(bean);
            }
        } catch (SQLException e ){
            e.printStackTrace();
            System.out.println("error in getTotal() at CategoryDAO list ");
        }

        return  beans;
    }

    /*
    * 查询所有
    * */
    public List<Category> list (){
        return  list(0, Short.MAX_VALUE);
    }
}


















