
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;

@Named(value = "student")
@SessionScoped
@ManagedBean
public class Student implements Serializable {

    @ManagedProperty(value = "#{login}")
    private Login login;

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    private DBConnect dbConnect = new DBConnect();
    private Integer id;
    private String studentLogin;
    private String studentPassword;
    private String studentOldPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String postalAddress;
    private String classDistributionJS = calculateClassDistributionJS(new HashMap<String, List<Assignment>>());
    private Date createdDate = new Date();
    
    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getStudentLogin() {
        return studentLogin;
    }

    public void setStudentLogin(String studentLogin) {
        this.studentLogin = studentLogin;
    }

    public String getStudentPassword() {
        return studentPassword;
    }

    public void setStudentPassword(String studentPassword) {
        this.studentPassword = studentPassword;
    }

    public String getStudentOldPassword() {
        return studentOldPassword;
    }

    public void setStudentOldPassword(String studentOldPassword) {
        this.studentOldPassword = studentOldPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getName() {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
    
        return login.getStudentLogin();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date created_date) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        this.createdDate = created_date;
    }

    public String getClassDistributionJS() {
        return classDistributionJS;
    }

    public void setClassDistributionJS(String classDistributionJS) {
        this.classDistributionJS = classDistributionJS;
    }

    public String createStudent() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("insert into customer(login, password, first_name, last_name, email, postal_address, credit_card_number, expiration_date, cvv_code, created_date) values(?,?,?,?,?,?,?,?,?,?)");
        preparedStatement.setString(1, studentLogin);
        preparedStatement.setString(2, studentPassword);
        preparedStatement.setString(3, firstName);
        preparedStatement.setString(4, lastName);
        preparedStatement.setString(5, email);
        preparedStatement.setString(6, postalAddress);
        preparedStatement.setDate(7, new java.sql.Date(createdDate.getTime()));
        preparedStatement.executeUpdate();
        
        statement.close();
        con.commit();
        con.close();
        Util.validateStudentSession(studentLogin);
        
        System.out.println(Util.getStudentLogin());
        
        return "studentDashboard";
    }
    
    public String deleteStudent() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);
        
        PreparedStatement preparedStatement = con.prepareStatement("delete from student where login = ?");
        
        preparedStatement.setString(1, studentLogin);
        preparedStatement.executeUpdate();

        con.commit();
        con.close();
                
        return "viewStudents";
    }
    
    public void validateLogin(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        
        Connection con = dbConnect.getConnection();
        int count;
        String submittedLogin = (String) value;

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement = con.prepareStatement("Select count(*) as count from student where login = ?");
        preparedStatement.setString(1, submittedLogin);
        
        ResultSet result = preparedStatement.executeQuery();

        result.next();
        
        count = result.getInt("count");
        
        if (count != 0) {
            FacesMessage errorMessage = new FacesMessage("This login already exists, please pick another one.");
            throw new ValidatorException(errorMessage);
        }
        
        result.close();
        con.close();
    }
    
    public void validateLoginExistence(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        
        Connection con = dbConnect.getConnection();
        int count;
        String submittedLogin = (String) value;
        
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement = con.prepareStatement("select count(*) as count from student where login = ?");
        preparedStatement.setString(1, submittedLogin);
        
        ResultSet result = preparedStatement.executeQuery();

        result.next();
        
        count = result.getInt("count");
        
        result.close();
        con.close();
        
        if (count == 0) {
            FacesMessage errorMessage = new FacesMessage("This login does not exist.");
            throw new ValidatorException(errorMessage);
        }
    }
    
    public List<Student> getStudentList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement
                = con.prepareStatement(
                        "select login, first_name, last_name, email, postal_address, created_date from student order by first_name, last_name");

        //get customer data from database
        ResultSet result = preparedStatement.executeQuery();

        List<Student> studentList = new ArrayList<>();

        while (result.next()) {
            Student student = new Student();

            student.setStudentLogin(result.getString("login"));
            student.setFirstName(result.getString("first_name"));
            student.setLastName(result.getString("last_name"));
            student.setEmail(result.getString("email"));
            student.setPostalAddress(result.getString("postal_address"));
            student.setCreatedDate(result.getDate("created_date"));

            //store all data into a List
            studentList.add(student);
        }
        
        result.close();
        con.close();
        
        return studentList;
    }
    
    public Integer getStudentId(String studentLogin) throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement
                = con.prepareStatement(
                        "select id from student where login = ?");

        preparedStatement.setString(1, studentLogin);
        
        //get customer data from database
        ResultSet result = preparedStatement.executeQuery();
        
        result.next();
        
        return result.getInt("id");
    }
    
    public void clear() {
        setStudentLogin(null);
        setStudentPassword(null);
    }
    
    public String changePassword() throws SQLException {
       Connection con = dbConnect.getConnection();

       if (con == null) {
           throw new SQLException("Can't get database connection");
       }
       con.setAutoCommit(false);

       Statement statement = con.createStatement();

       PreparedStatement preparedStatement = con.prepareStatement("update student set password = ? where login = ?");
       preparedStatement.setString(1, studentPassword);
       preparedStatement.setString(2, Util.getStudentLogin());
       preparedStatement.executeUpdate();

       statement.close();
       con.commit();
       con.close();
       clear();

       return "index";
    }
    
    public void validateOldPassword(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        
        String submittedPassword = (String) value;

        Connection con = dbConnect.getConnection();
        int count;

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement = con.prepareStatement("select count(*) as count from student where login = ? and password = ?");
        preparedStatement.setString(1, Util.getStudentLogin());
        preparedStatement.setString(2, submittedPassword);
        
        ResultSet result = preparedStatement.executeQuery();
        
        result.next();
        
        count = result.getInt("count");
        
        if (count == 0) {
            FacesMessage errorMessage = new FacesMessage("Incorrect old password.");
            throw new ValidatorException(errorMessage);
        }
        
        result.close();
        con.close();
    }
    
    public String setUserProfile() throws SQLException  {
        Connection con = dbConnect.getConnection();
        int count;

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement = con.prepareStatement("select first_name, last_name from student where login = ?");
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        result.next();
        
        this.firstName = result.getString("first_name");
        this.lastName = result.getString("last_name");
        
        return "userProfile";
    }
    
    // Maps week to an assignment list, so statistics can be calculated on the
    // assignment grade and graphed, "week 1" -> List<Assignment>, "week 2" -> List<Assignment>
    public String calculateClassDistributionJS(HashMap<String, List<Assignment>> dateAssignmentMap) {
        return "$(function() { Morris.Area({\n" +
"        element: 'morris-area-chart',\n" +
"        data: [{\n" +
"            period: '2010 Q1',\n" +
"            iphone: 2666,\n" +
"            ipad: null,\n" +
"            itouch: 2647\n" +
"        }, {\n" +
"            period: '2010 Q2',\n" +
"            iphone: 2778,\n" +
"            ipad: 2294,\n" +
"            itouch: 2441\n" +
"        }, {\n" +
"            period: '2010 Q3',\n" +
"            iphone: 4912,\n" +
"            ipad: 1969,\n" +
"            itouch: 2501\n" +
"        }, {\n" +
"            period: '2010 Q4',\n" +
"            iphone: 3767,\n" +
"            ipad: 3597,\n" +
"            itouch: 5689\n" +
"        }, {\n" +
"            period: '2011 Q1',\n" +
"            iphone: 6810,\n" +
"            ipad: 1914,\n" +
"            itouch: 2293\n" +
"        }, {\n" +
"            period: '2011 Q2',\n" +
"            iphone: 5670,\n" +
"            ipad: 4293,\n" +
"            itouch: 1881\n" +
"        }, {\n" +
"            period: '2011 Q3',\n" +
"            iphone: 4820,\n" +
"            ipad: 3795,\n" +
"            itouch: 1588\n" +
"        }, {\n" +
"            period: '2011 Q4',\n" +
"            iphone: 15073,\n" +
"            ipad: 5967,\n" +
"            itouch: 5175\n" +
"        }, {\n" +
"            period: '2012 Q1',\n" +
"            iphone: 10687,\n" +
"            ipad: 4460,\n" +
"            itouch: 2028\n" +
"        }, {\n" +
"            period: '2012 Q2',\n" +
"            iphone: 8432,\n" +
"            ipad: 5713,\n" +
"            itouch: 1791\n" +
"        }],\n" +
"        xkey: 'period',\n" +
"        ykeys: ['iphone', 'ipad', 'itouch'],\n" +
"        labels: ['iPhone', 'iPad', 'iPod Touch'],\n" +
"        pointSize: 2,\n" +
"        hideHover: 'auto',\n" +
"        resize: true\n" +
"    }); });";
    }
}
