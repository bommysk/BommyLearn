
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
    
    public String getFirstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(date);
        
        while (calendar.get(Calendar.DAY_OF_WEEK) > calendar.getFirstDayOfWeek()) {
            calendar.add(Calendar.DATE, -1); // Substract 1 day until first day of week.
        }
        
        Date firstDayOfWeek = calendar.getTime();
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // Output "Wed Sep 26 14:23:28 EST 2012"

        String formatted = format.format(firstDayOfWeek);
        
        System.out.println(formatted);
        
        return formatted;
    }
    
    public HashMap<String, HashMap<String, Integer>> calculateClassDistribution() throws SQLException {
        HashMap<String, HashMap<String, Integer>> classDistribution = new HashMap<>();
        Connection con = dbConnect.getConnection();
        String firstDayOfWeek;
        HashMap<String, Integer> classGrade;
        ArrayList<String> classList = new ArrayList<>();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement = con.prepareStatement("select class.name as class_name, due_date, sum(grade) as grade_sum from assignment_submit join assignment on assignment_submit.assignment_id = assignment.id join class on assignment.class_id = class.id where assignment_submit.student_id = (select id from student where login = ?) group by class.id, due_date", ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        List<Schedule> studentSchedule = (new Schedule()).getStudentSchedule();
        
        for (Schedule sched : studentSchedule) {
            classList.add(sched.cl.getName());
        }
        
        Collections.sort(classList);
        
        while (result.next()) { 
            firstDayOfWeek = getFirstDayOfWeek(result.getDate("due_date"));

            classDistribution.put(firstDayOfWeek, new HashMap<String, Integer>());

            System.out.println(firstDayOfWeek);

            classGrade = classDistribution.get(firstDayOfWeek);

            for (String cl : classList) {
                classGrade.put(cl, 0);
            }

            classGrade.put(result.getString("class_name"), classGrade.get(result.getString("class_name")) + result.getInt("grade_sum"));
            
            while (result.next() && getFirstDayOfWeek(result.getDate("due_date")).equals(firstDayOfWeek)) {
                classGrade.put(result.getString("class_name"), classGrade.get(result.getString("class_name")) + result.getInt("grade_sum"));
            }
            
            result.previous();
        }
        
        return classDistribution;
    }
    
    // Maps week to an assignment list, so statistics can be calculated on the
    // assignment grade and graphed, "week 1 date" -> HashMap<class, grade>, "week 2 date" -> HashMap<class, grade>
    public String calculateClassDistributionJS(HashMap<String, HashMap<String, Integer>> dateAssignmentMap) throws SQLException {
        String classDistributionJS = "$(function() { Morris.Area({\n" +
            "element: 'morris-area-chart',\n" +
            "data: [";
        
        HashMap<String, Integer> classGrade;
        
        for (String key : dateAssignmentMap.keySet()) {
            classDistributionJS += 
                    "{\n period:'" + key + "',\n";
            
            classGrade = dateAssignmentMap.get(key);
            
            for (String cl : classGrade.keySet()) {
                classDistributionJS += "'" + cl + "': " + classGrade.get(cl) + ",\n";
            }
            
            classDistributionJS = classDistributionJS.substring(0, classDistributionJS.length() - 2);
            
            classDistributionJS += "\n},";
        }
        
        classDistributionJS = classDistributionJS.substring(0, classDistributionJS.length() - 1);
        
        ArrayList<String> classList = new ArrayList<>();
        List<Schedule> studentSchedule = (new Schedule()).getStudentSchedule();
        
        for (Schedule sched : studentSchedule) {
            classList.add(sched.cl.getName());
        }
        
        Collections.sort(classList);
        
        String ykeys = "[";
        
        for (String cl : classList) {
            ykeys += "'" + cl + "',";
        }
        
        ykeys = ykeys.substring(0, ykeys.length() - 1);
        ykeys += "]";
        
        classDistributionJS += "],\n" +
            "xkey: 'period',\n" +
            "ykeys: " + ykeys + ",\n" +
            "labels: " + ykeys + ",\n" +
            "pointSize: 2,\n" +
            "hideHover: 'auto',\n" +
            "resize: true\n" +
        "}); });";
         
         return classDistributionJS;
    }
}
