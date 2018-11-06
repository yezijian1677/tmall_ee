package tmall.DAO;

import tmall.bean.User;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /*
    * 获取用户数
    * */
    public int getTotal(){
        int total = 0;
        try(Connection c = DBUtil.getConnection(); Statement s = c.createStatement()) {
            String sql = "select count(*) from User";
            ResultSet rs = s.executeQuery(sql);
            while(rs.next()){
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in getTotal() at UserDAO getTotal");
        }
        return  total;
    }

    /*
    * 添加新用户
    * */
    public void add(User bean) {
        String sql = "insert into category values(null, ?, ?)";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, bean.getName());
            ps.setString(2, bean.getPassword());
            ps.execute();
            //获取主键 如果返回成功，从数据库获取id 给Category bean
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()){
                int id = rs.getInt(1);
                bean.setId(id);
            }
        }catch (SQLException e ){
            e.printStackTrace();
            System.out.println("error in getTotal() at UserDAO add ");
        }
    }

    /*
     * 修改用户账号密码
     * */
    public void update(User bean) {
        String sql = "update category set name = ?, password = ? where id = ?";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, bean.getName());
            ps.setString(2, bean.getPassword());
            ps.setInt(3, bean.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in getTotal() at UserDAO update ");
        }
    }

    /*
     * 删除用户
     * */
    public void delete(int id) {
        try(Connection c = DBUtil.getConnection(); Statement s = c.createStatement()){
            String sql = "delete from user where id = " + id;
            s.execute(sql);
        } catch (SQLException e ) {
            e.printStackTrace();
            System.out.println("error in getTotal() at userDAO delete");
        }
    }

    /*
     * 根据id获取分类
     * */
    public User get(int id) {
        User bean = null;

        try(Connection c = DBUtil.getConnection(); Statement s = c.createStatement()){
            String sql = "select * from USER where id =" + id;
            ResultSet rs = s.executeQuery(sql);

            if (rs.next()){
                bean = new User();
                String name = rs.getString("name");
                String password = rs.getString("password");
                bean.setName(password);
                bean.setName(name);
                bean.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in getTotal() at UserDAO get ");
        }

        return  bean;
    }

    /*
     * 分页查询
     * */
    public List<User> list (int start, int count) {
        List<User> beans = new ArrayList<User>();
        String sql = "select * from USER order by id desc limit ?, ?";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, start);
            ps.setInt(2, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                User bean = new User();
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                bean.setId(id);
                bean.setName(name);
                bean.setPassword(password);
                beans.add(bean);
            }
        } catch (SQLException e ){
            e.printStackTrace();
            System.out.println("error in getTotal() at UserDAO list ");
        }

        return  beans;
    }

    /*
     * 查询所有
     * */
    public List<User> list (){
        return  list(0, Short.MAX_VALUE);
    }

    /*
    * 根据用户名获取信息
    * */
    public User get(String name) {
        User bean = null;
        String sql = "select * from User where name = ?";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                bean = new User();
                int id = rs.getInt("id");
                String password = rs.getString("password");
                bean.setId(id);
                bean.setName(name);
                bean.setPassword(password);
            }
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("error in getTotal() at UserDAO getUserByName ");
        }

        return bean;
    }

    /*
    * 根据用户信息判定是否存在
    * */
    public boolean isExist(String name){
        User user = get(name);
        return user!=null;
    }

    /*
    * 根据账号密码查询用户存在 判定登陆
    * */
    public User get(String name, String password){
        User bean = null;
        String sql = "select * from User where name = ? , password = ?";
        try(Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, name);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bean = new User();
                int id = rs.getInt("id");
                bean.setId(id);
                bean.setName(name);
                bean.setPassword(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in getTotal() at UserDAO login ");
        }

        return bean;
    }

}

