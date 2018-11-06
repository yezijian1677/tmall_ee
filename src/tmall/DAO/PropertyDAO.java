package tmall.DAO;

import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyDAO {

    /*
    * 根据分类id(cid)查询属性数量
    * */
    public int getTotal(int cid){
        int total = 0;
        try(Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select count(*) from Property where cid = "+ cid;
            ResultSet rs = s.executeQuery(sql);
            while(rs.next()){
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in getTotal() at PropertyDAO getTotal");
        }
        return  total;
    }

    /*
     * 添加新属性
     * */
    public void add(Property bean) {
        String sql = "insert into Property values(null, ?, ?)";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, bean.getCategory().getId());
            ps.setString(2, bean.getName());
            ps.execute();
            //获取主键 如果返回成功，从数据库获取id 给Category bean
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()){
                int id = rs.getInt(1);
                bean.setId(id);
            }
        }catch (SQLException e ){
            e.printStackTrace();
            System.out.println("error in getTotal() at PropertyDAO add ");
        }
    }

    /*
     * 修改属性值
     * */
    public void update(Property bean) {
        String sql = "update category set cid = ?, name = ? where id = ?";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, bean.getCategory().getId());
            ps.setString(2, bean.getName());
            ps.setInt(3, bean.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in getTotal() at PropertyDAO update ");
        }
    }

    /*
     * 删除用户
     * */
    public void delete(int id) {
        try(Connection c = DBUtil.getConnection(); Statement s = c.createStatement()){
            String sql = "delete from Property where id = " + id;
            s.execute(sql);
        } catch (SQLException e ) {
            e.printStackTrace();
            System.out.println("error in getTotal() at PropertyDAO delete");
        }
    }

    /*
     * 根据id获取属性值
     * */
    public Property get(int id) {
        Property bean = null;

        try(Connection c = DBUtil.getConnection(); Statement s = c.createStatement()){
            String sql = "select * from Property where id =" + id;
            ResultSet rs = s.executeQuery(sql);

            if (rs.next()){
                bean = new Property();
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                Category category = new CategoryDAO().get(cid);
                bean.setCategory(category);
                bean.setName(name);
                bean.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in getTotal() at PropertyDAO get ");
        }

        return  bean;
    }

    /*
     * 分页查询
     * */
    public List<Property> list (int cid, int start, int count) {
        List<Property> beans = new ArrayList<Property>();
        String sql = "select * from Property where cid = ? order by id desc limit ?, ?";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, cid);
            ps.setInt(2, start);
            ps.setInt(3,count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                Property bean = new Property();
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Category category = new CategoryDAO().get(cid);
                bean.setId(id);
                bean.setName(name);
                bean.setCategory(category);
                beans.add(bean);
            }
        } catch (SQLException e ){
            e.printStackTrace();
            System.out.println("error in getTotal() at PropertyDAO list ");
        }

        return  beans;
    }

    /*
     * 查询所有
     * */
    public List<Property> list (int cid){
        return  list(cid,0, Short.MAX_VALUE);
    }
}