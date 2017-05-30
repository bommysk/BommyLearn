
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

@Named(value = "teacher")
@SessionScoped
@ManagedBean
public class Teacher implements Serializable {

    @ManagedProperty(value = "#{login}")
    private Login login;

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    private DBConnect dbConnect = new DBConnect();
    private String teacherLogin;
    private String teacherPassword;
    private String teacherOldPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String postalAddress;
    private Integer tenure;
    private Date createdDate = new Date();
    
    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }
    
    public String getTeacherLogin() {
        return teacherLogin;
    }

    public void setTeacherLogin(String teacherLogin) {
        this.teacherLogin = teacherLogin;
    }

    public String getTeacherPassword() {
        return teacherPassword;
    }

    public void setTeacherPassword(String teacherPassword) {
        this.teacherPassword = teacherPassword;
    }

    public String getTeacherOldPassword() {
        return teacherOldPassword;
    }

    public void setTeacherOldPassword(String teacherOldPassword) {
        this.teacherOldPassword = teacherOldPassword;
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

    public Integer getTenure() {
        return tenure;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getLoginFromSession() {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
    
        return login.getTeacherLogin();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date created_date) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        this.createdDate = created_date;
    }

    public String createTeacher() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("insert into teacher(login, password, first_name, last_name, email, postal_address, tenure, created_date) values(?,?,?,?,?,?,?,?,?,?)");
        preparedStatement.setString(1, teacherLogin);
        preparedStatement.setString(2, teacherPassword);
        preparedStatement.setString(3, firstName);
        preparedStatement.setString(4, lastName);
        preparedStatement.setString(5, email);
        preparedStatement.setString(6, postalAddress);
        preparedStatement.setInt(7, tenure);
        preparedStatement.setDate(8, new java.sql.Date(createdDate.getTime()));
        preparedStatement.executeUpdate();
        
        statement.close();
        con.commit();
        con.close();
        Util.validateTeacherSession(teacherLogin);
        
        System.out.println(Util.getTeacherLogin());
        
        return "teacherDashboard";
    }
    
    public String deleteTeacher() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);
        
        PreparedStatement preparedStatement = con.prepareStatement("delete from teacher where login = ?");
        
        preparedStatement.setString(1, teacherLogin);
        preparedStatement.executeUpdate();

        con.commit();
        con.close();
                
        return "adminDashboard.xhtml";
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

        PreparedStatement preparedStatement = con.prepareStatement("select count(*) as count from teacher where login = ?");
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

        PreparedStatement preparedStatement = con.prepareStatement("select count(*) as count from teacher where login = ?");
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
    
    public List<Teacher> getStudentList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement
                = con.prepareStatement(
                        "select login, first_name, last_name, email, postal_address, tenure, created_date from teacher order by first_name, last_name");

        //get customer data from database
        ResultSet result = preparedStatement.executeQuery();

        List<Teacher> teacherList = new ArrayList<>();

        while (result.next()) {
            Teacher teacher = new Teacher();

            teacher.setTeacherLogin(result.getString("login"));
            teacher.setFirstName(result.getString("first_name"));
            teacher.setLastName(result.getString("last_name"));
            teacher.setEmail(result.getString("email"));
            teacher.setPostalAddress(result.getString("postal_address"));
            teacher.setTenure(result.getInt("tenure"));
            teacher.setCreatedDate(result.getDate("created_date"));

            //store all data into a List
            teacherList.add(teacher);
        }
        
        result.close();
        con.close();
        
        return teacherList;
    }
    
    public Integer getTeacherId(String teacherLogin) throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement
                = con.prepareStatement(
                        "select id from teacher where login = ?");

        preparedStatement.setString(1, teacherLogin);
        
        //get customer data from database
        ResultSet result = preparedStatement.executeQuery();
        
        result.next();
        
        return result.getInt("id");
    }
    
    public String getTeacherLoginFromId(Integer id) throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement
                = con.prepareStatement(
                        "select login from teacher where id = ?");

        preparedStatement.setInt(1, id);
        
        //get customer data from database
        ResultSet result = preparedStatement.executeQuery();
        
        result.next();
        
        return result.getString("login");
    }
    
    
    public void clear() {
        setTeacherLogin(null);
        setTeacherPassword(null);
    }
    
    public String changePassword() throws SQLException {
       Connection con = dbConnect.getConnection();

       if (con == null) {
           throw new SQLException("Can't get database connection");
       }
       con.setAutoCommit(false);

       Statement statement = con.createStatement();

       PreparedStatement preparedStatement = con.prepareStatement("update teacher set password = ? where login = ?");
       preparedStatement.setString(1, teacherPassword);
       preparedStatement.setString(2, Util.getTeacherLogin());
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

        PreparedStatement preparedStatement = con.prepareStatement("select count(*) as count from teacher where login = ? and password = ?");
        preparedStatement.setString(1, Util.getTeacherLogin());
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

        PreparedStatement preparedStatement = con.prepareStatement("select first_name, last_name from teacher where login = ?");
        preparedStatement.setString(1, Util.getTeacherLogin());
        
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

        PreparedStatement preparedStatement = con.prepareStatement("select class.name as class_name, due_date, avg(grade) as grade_avg "
                + "from assignment_submit join assignment on assignment_submit.assignment_id = assignment.id join class on "
                + "assignment.class_id = class.id where assignment.teacher_id = (select id from teacher where login = ?) "
                + "group by class.id, due_date order by due_date", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
        preparedStatement.setString(1, Util.getTeacherLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        List<Schedule> teacherSchedule = (new Schedule()).getTeacherSchedule();
        
        for (Schedule sched : teacherSchedule) {
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

            classGrade.put(result.getString("class_name"), classGrade.get(result.getString("class_name")) + result.getInt("grade_avg"));
            
            while (result.next() && getFirstDayOfWeek(result.getDate("due_date")).equals(firstDayOfWeek)) {
                classGrade.put(result.getString("class_name"), classGrade.get(result.getString("class_name")) + result.getInt("grade_avg"));
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
        List<Schedule> teacherSchedule = (new Schedule()).getTeacherSchedule();
        
        for (Schedule sched : teacherSchedule) {
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
    
    public Integer getClassId(String className) throws SQLException {
        Connection con = dbConnect.getConnection();
        
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement = con.prepareStatement("select class_id from class_schedule join class on class_schedule.class_id = class.id where "
                + "class_schedule.teacher_id = (select id from teacher where login = ?) and class.name = ?", ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
        
        preparedStatement.setString(1, Util.getTeacherLogin());
        preparedStatement.setString(2, className);
        
        ResultSet result = preparedStatement.executeQuery();
        
        result.next();
        
        return result.getInt("class_id");
    }
}
