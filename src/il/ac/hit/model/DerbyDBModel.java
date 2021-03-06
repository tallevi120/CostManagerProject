package il.ac.hit.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * This is the class for all model method.
 * @author Inbal mishal and Tal levi.
 */
public class DerbyDBModel implements IModel {
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String JDBC_URL = "jdbc:derby:CostDB;create=true";

    /**
     * Create the connection to the DB.
     * @throws CostManagerException Problem with the connection to the db driver.
     */
    public DerbyDBModel() throws CostManagerException {
        createDB();
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new CostManagerException("Problem with the connection to the db driver");
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addCostOrIncome(CostOrIncome item) throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        try {
            //Connect to the DB.
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            //check what is the max id in the table and set this id for the new item
            item.setId(maxId() + 1);
            //Insert cost or income to the DB.
            statement.execute("insert into InOutCome(id, description, cost, date, category) values (" + item.getId() + ", '" +
                    item.getDescription() + "', " + item.getCost() + ", '" + item.getDate() + "', '" + item.getCategory().getCategoryName() + "')");
        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int maxId() throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        int max;
        try {
            //Connect to the DB.
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            //check what is the max id in the table
            rs = statement.executeQuery("select COALESCE(max(id),0) AS MaxID from InOutCome");
            rs.next();
            max = rs.getInt("MaxID");
        } catch (Exception e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void deleteCostOrIncome(int id) throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        //Check if the id exist.
        if (!checkIfIdExists(id)) {
            throw new CostManagerException("This id not exists");
        }
        try {
            //Connect to the DB.
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            String sql = "delete from InOutCome where id = " + id;
            //Delete cost or income from the DB.
            statement.execute(sql);
        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkIfIdExists(int id) throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            //Connect to the DB.
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            String sql = "SELECT * FROM InOutCome WHERE id = " + id;
            //Search if this id exist in the table InOutCome in the DB.
            rs = statement.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void addNewCategory(Category item) throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        //Check if the category already exist.
        if (checkIfCategoryExists(item.getCategoryName())) {
            throw new CostManagerException("This category already exists");
        }
        try {
            //Connect to the DB.
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            String sql = "insert into category values ('" + item.getCategoryName() + "')";
            //Insert new category to the table category in the DB.
            statement.execute(sql);
        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkIfCategoryExists(String categoryName) throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            //Connect to the DB.
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            String sql = "SELECT * FROM category WHERE name = '" + categoryName + "'";
            //Search if this category name exist in the table category in the DB.
            rs = statement.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCategory(Category item) throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        //Check if the category doesn't exist.
        if (!checkIfCategoryExists(item.getCategoryName())) {
            throw new CostManagerException("This category doesn't exist");
        }
        try {
            //Connect to the DB.
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            String sql = "delete from category where name = '" + item.getCategoryName() + "'";
            //Delete category from the table category in the DB.
            statement.execute(sql);
        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ArrayList<CostOrIncome> getAllCostsBetweenDates(Date from, Date to) throws CostManagerException {
        if (from.after(to)) {
            throw new CostManagerException("The date 'from' come after the date 'to'");
        }
        ArrayList<CostOrIncome> items = getAllCostsAndIncomesBetweenDates(from, to);
        ArrayList<CostOrIncome> result = new ArrayList<>();
        //Insert to array result every cost from the items array.
        for (CostOrIncome item : items) {
            if (item.getCost() < 0) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ArrayList<CostOrIncome> getAllIncomesBetweenDates(Date from, Date to) throws CostManagerException {
        if (from.after(to)) {
            throw new CostManagerException("The date 'from' come after the date 'to'");
        }
        ArrayList<CostOrIncome> items = getAllCostsAndIncomesBetweenDates(from, to);
        ArrayList<CostOrIncome> result = new ArrayList<>();
        //Insert to array result every income from the items array.
        for (CostOrIncome item : items) {
            if (item.getCost() >= 0) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ArrayList<CostOrIncome> getAllCostsAndIncomesBetweenDates(Date from, Date to) throws CostManagerException {
        if (from.after(to)) {
            throw new CostManagerException("The date 'from' come after the date 'to'");
        }
        ArrayList<CostOrIncome> items = getAllCostsAndIncomes();
        ArrayList<CostOrIncome> result = new ArrayList<>();
        //Check every item in items array if the date between from and to, if yes put inside the result array.
        for (CostOrIncome item : items) {
            if (item.getDate().compareTo(from) >= 0 && item.getDate().compareTo(to) <= 0) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTheBalance() throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        double balance = 0;
        try {
            //Connect to the DB.
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();

            String sql = "SELECT cost FROM InOutCome";
            //Get all cost and income from the DB.
            rs = statement.executeQuery(sql);
            //Sum up all cost and income.
            while (rs.next()) {
                balance += rs.getDouble("cost");
            }
        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return balance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ArrayList<CostOrIncome> getAllCostsAndIncomes() throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        ArrayList<CostOrIncome> items;

        try {
            items = new ArrayList<>();
            //Connect to the DB.
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            //Get all details from cost and income table in the DB.
            rs = statement.executeQuery("SELECT * FROM InOutCome");
            //Put inside items array all the cost and income.
            while (rs.next()) {
                //int id, String description, double cost, Date date, Category category
                Category category = new Category(rs.getString("category"));
                CostOrIncome item = new CostOrIncome(rs.getInt("id"), rs.getString("description"), rs.getDouble("cost"), rs.getDate("date"), category);
                items.add(item);
            }

        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return items;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ArrayList<CostOrIncome> getAllCosts() throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        ArrayList<CostOrIncome> items,result;

        try {
            items = new ArrayList<>();
            //Connect to the DB.
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            //Get all details from cost and income table in the DB.
            rs = statement.executeQuery("SELECT * FROM InOutCome");
            //Put inside items array all the cost and income.
            while (rs.next()) {
                //int id, String description, double cost, Date date, Category category
                Category category = new Category(rs.getString("category"));
                CostOrIncome item = new CostOrIncome(rs.getInt("id"), rs.getString("description"), rs.getDouble("cost"), rs.getDate("date"), category);
                items.add(item);
            }

        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        result = new ArrayList<>();
        //Insert to array result every cost from the items array.
        for (CostOrIncome item : items) {
            if (item.getCost() < 0) {
                result.add(item);
            }
        }
        return result;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ArrayList<CostOrIncome> getAllIncomes() throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        ArrayList<CostOrIncome> items,result;

        try {
            items = new ArrayList<>();
            //Connect to the DB.
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            //Get all details from cost and income table in the DB.
            rs = statement.executeQuery("SELECT * FROM InOutCome");
            //Put inside items array all the cost and income.
            while (rs.next()) {
                //int id, String description, double cost, Date date, Category category
                Category category = new Category(rs.getString("category"));
                CostOrIncome item = new CostOrIncome(rs.getInt("id"), rs.getString("description"), rs.getDouble("cost"), rs.getDate("date"), category);
                items.add(item);
            }

        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        result = new ArrayList<>();
        //Insert to array result every income from the items array.
        for (CostOrIncome item : items) {
            if (item.getCost() >= 0) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Category> getAllCategories() throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        ArrayList<Category> categories;

        try {
            //Connect to the DB.
            categories = new ArrayList<>();
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            //Get all categories from category table in the DB.
            rs = statement.executeQuery("SELECT * FROM Category");
            //Put inside array categories all the categories from DB.
            while (rs.next()) {
                Category category = new Category(rs.getString("name"));
                categories.add(category);
            }

        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return categories;
    }

    private void createDB() throws CostManagerException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            try {
                //Create the tables.
                statement.execute("create table category(name VARCHAR(255))");
                statement.execute("create table InOutCome(id int, description VARCHAR(255), cost DOUBLE, date DATE, category VARCHAR(255))");
                statement.execute("insert into Category values ('clothes')");
                statement.execute("insert into Category values ('food')");
                statement.execute("insert into Category values ('maintenance')");
            } catch (SQLException e) {
                //Check if the exception throw because the tables already exist or because something else.
                if (!e.getSQLState().equals("X0Y32")) {
                    throw new CostManagerException(e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new CostManagerException(e.getMessage());
        } finally {
            //Disconnect from the DB.
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
