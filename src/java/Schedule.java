/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author shubham.kahal
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

@Named(value = "schedule")
@RequestScoped
@ManagedBean
public class Schedule {
    private DBConnect dbConnect = new DBConnect();
    Class cl = new Class();
    Student student = new Student();
    Teacher teacher = new Teacher();
    public String[] classList;
    private UIInput studentLoginUI;
    private UIInput classListUI;

    public UIInput getStudentLoginUI() {
        return studentLoginUI;
    }

    public void setStudentLoginUI(UIInput studentLoginUI) {
        this.studentLoginUI = studentLoginUI;
    }
    
    public UIInput getClassListUI() {
        return classListUI;
    }

    public void setClassListUI(UIInput classListUI) {
        this.classListUI = classListUI;
    }

    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }
    
    public Class getCl() {
        return cl;
    }

    public void setCl(Class cl) {
        this.cl = cl;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String[] getClassList() {
        return classList;
    }

    public void setClassList(String[] classList) {
        this.classList = classList;
    }
    
    public List<Schedule> getTeacherSchedule() throws SQLException {
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select distinct class.name as class_name, class.description as class_description, " +
                                        "class.day_schedule as day_schedule, class.start_time as start_time, " +
                                        "class.end_time as end_time from class_schedule join class " +
                                        "on class_schedule.class_id = class.id join teacher on class_schedule.teacher_id = " +
                                        "teacher.id where class_schedule.teacher_id = (select id from teacher where login = ?);");
        
        preparedStatement.setString(1, Util.getTeacherLogin());
        
        ResultSet result = preparedStatement.executeQuery();

        List<Schedule> schedule = new ArrayList<>();

        while (result.next()) {
            Schedule sched = new Schedule();
            Class cl = new Class();
            
            cl.setName(result.getString("class_name"));
            cl.setDescription(result.getString("class_description"));
            cl.setDaySchedule(result.getString("day_schedule"));
            cl.setStartTime(result.getString("start_time"));
            cl.setEndTime(result.getString("end_time"));
                        
            sched.setCl(cl);
   
            //store all data into a List
            schedule.add(sched);
        }
        
        result.close();
        con.close();
        
        return schedule;
    }
    
    public List<String> getTeacherClasses() throws SQLException {
        List<Schedule> teacherSchedule = getTeacherSchedule();
        List<String> classList = new ArrayList<>();
        
        for (Schedule sched : teacherSchedule) {
            classList.add(sched.getCl().getName());
        }
        
        return classList;
    }
    
    public List<Schedule> getStudentSchedule() throws SQLException {
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select distinct class.name as class_name, class.description as class_description, " +
                                        "class.day_schedule as day_schedule, class.start_time as start_time, " +
                                        "class.end_time as end_time, teacher.first_name as teacher_first_name, " +
                                        "teacher.last_name as teacher_last_name from class_schedule join class " +
                                        "on class_schedule.class_id = class.id join teacher on class_schedule.teacher_id = " +
                                        "teacher.id where class_schedule.student_id = (select id from student where login = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();

        List<Schedule> schedule = new ArrayList<>();

        while (result.next()) {
            Schedule sched = new Schedule();
            Class cl = new Class();
            Teacher teacher = new Teacher();
            
            cl.setName(result.getString("class_name"));
            cl.setDescription(result.getString("class_description"));
            cl.setDaySchedule(result.getString("day_schedule"));
            cl.setStartTime(result.getString("start_time"));
            cl.setEndTime(result.getString("end_time"));
            
            teacher.setFirstName(result.getString("teacher_first_name"));
            teacher.setFirstName(result.getString("teacher_last_name"));
            
            sched.setCl(cl);
            
            sched.setTeacher(teacher);
   
            //store all data into a List
            schedule.add(sched);
        }
        
        result.close();
        con.close();
        
        return schedule;
    }
    
    public String getStudentNavTabSchedule() throws SQLException {
        String navTabSchedule = "<ul class='nav nav-pills nav-justified' role='tablist'>";
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select distinct class.name as class_name from class_schedule join class "
                                        + "on class_schedule.class_id = class.id where "
                                        + "class_schedule.student_id = (select id from student where login = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            navTabSchedule += "<li id=\"" + result.getString("class_name").replaceAll("\\s","") + "Tab\" class=\"nav-item\">\n" +
                              "<a class=\"nav-link active\" data-toggle=\"tab\" href=\"#" + result.getString("class_name").replaceAll("\\s","") + "\" " +
                              "role=\"tab\">" + result.getString("class_name") + "</a></li>";
        }
        
        navTabSchedule += "</ul>";        
        
        return navTabSchedule;
    }
    
    public List<String> getStudentClasses() throws SQLException {
        Connection con = dbConnect.getConnection();
        List<String> studentClasses = new ArrayList<>();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select distinct class.name as class_name from class_schedule join class "
                                        + "on class_schedule.class_id = class.id where "
                                        + "class_schedule.student_id = (select id from student where login = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            studentClasses.add(result.getString("class_name"));
        }
        
        return studentClasses;
    }
    
    public String getTeacherNavTabSchedule() throws SQLException {
        String navTabSchedule = "<ul class='nav nav-pills nav-justified' role='tablist'>";
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select distinct class.name as class_name from class_schedule join class "
                                        + "on class_schedule.class_id = class.id where "
                                        + "class_schedule.teacher_id = (select id from teacher where login = ?);");
        
        preparedStatement.setString(1, Util.getTeacherLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            navTabSchedule += "<li id=\"" + result.getString("class_name").replaceAll("\\s","") + "Tab\" class=\"nav-item\">\n" +
                              "<a class=\"nav-link active\" data-toggle=\"tab\" href=\"#" + result.getString("class_name").replaceAll("\\s","") + "\" " +
                              "role=\"tab\">" + result.getString("class_name") + "</a></li>";
        }
        
        navTabSchedule += "</ul>";        
        
        return navTabSchedule;
    }
    
    public String toggleStudentScheduleTabsJS() throws SQLException {
        String scheduleTabsJS = "";
        List<String> studentClasses = getStudentClasses();
        
        for (int idx = 0; idx < studentClasses.size(); idx++) {
            scheduleTabsJS += "$('#" + studentClasses.get(idx).replaceAll("\\s","") + "Tab a').click(function (e) {\n" +
            "e.preventDefault();\n";
            for (int innerIdx = 0; innerIdx < studentClasses.size(); innerIdx++) {       
                if (innerIdx != idx) {
                    scheduleTabsJS += "$('#" + studentClasses.get(innerIdx).replaceAll("\\s","") + "').hide();\n";
                }
            }
            
            scheduleTabsJS += "$('#" + studentClasses.get(idx).replaceAll("\\s","") + "').show();\n" + "});";
        }
        
        
        return scheduleTabsJS;
    }
    
    public String toggleTeacherScheduleTabsJS() throws SQLException {
        String scheduleTabsJS = "";
        List<String> teacherClasses = getTeacherClasses();
        
        for (int idx = 0; idx < teacherClasses.size(); idx++) {
            scheduleTabsJS += "$('#" + teacherClasses.get(idx).replaceAll("\\s","") + "Tab a').click(function (e) {\n" +
            "e.preventDefault();\n";
            for (int innerIdx = 0; innerIdx < teacherClasses.size(); innerIdx++) {       
                if (innerIdx != idx) {
                    scheduleTabsJS += "$('#" + teacherClasses.get(innerIdx).replaceAll("\\s","") + "').hide();\n";
                }
            }
            
            scheduleTabsJS += "$('#" + teacherClasses.get(idx).replaceAll("\\s","") + "').show();\n" + "});";
        }
        
        
        return scheduleTabsJS;
    }
    
    public String addToSchedule() throws SQLException {                
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);        

        PreparedStatement preparedStatement = con.prepareStatement("insert into class_schedule(class_id, student_id, teacher_id) values(?, (select id from student where login = ?), (select id from teacher where login = ?));");
      
        for (String classId : this.classList) {            
            preparedStatement.setInt(1, Integer.parseInt(classId));
            preparedStatement.setString(2, this.student.getStudentLogin());
            preparedStatement.setString(3, this.teacher.getTeacherLogin());
            preparedStatement.executeUpdate();
        }
        
        con.commit();
        con.close();
        
        clear();
        
        return "createSchedule";
    }
    
    public boolean hasTimeConflict(String[] submittedClassList, String startTime, String endTime) throws SQLException {
        Connection con = dbConnect.getConnection();
        String timeCounter;
        List<String> timeRange = new ArrayList<>();
                
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select start_time, end_time from class where id = ?");
        
        ResultSet result;
        
        TreeMap<String, String> timeMap = new TreeMap<>();
        
        timeMap.put("07:10 AM", "07:10");
        timeMap.put("08:10 AM", "08:10");
        timeMap.put("09:10 AM", "09:10");
        timeMap.put("10:10 AM", "10:10");
        timeMap.put("11:10 AM", "11:10");
        timeMap.put("12:10 PM", "12:10");
        timeMap.put("01:10 PM", "13:10");
        timeMap.put("02:10 PM", "14:10");
        timeMap.put("03:10 PM", "15:10");
        timeMap.put("04:10 PM", "16:10");
        timeMap.put("05:10 PM", "17:10");
        timeMap.put("06:10 PM", "18:10");
        timeMap.put("07:10 PM", "19:10");
        timeMap.put("08:10 PM", "20:10");
        timeMap.put("09:10 PM", "21:10");
        timeMap.put("10:10 PM", "22:10");
        
        for (String classId : submittedClassList) {
            preparedStatement.setInt(1, Integer.parseInt(classId));
            result = preparedStatement.executeQuery();
            
            while (result.next()) {
                timeCounter = result.getString("start_time");
                
                for (String key : timeMap.keySet()) {
                    if (timeMap.get(timeCounter).compareTo(timeMap.get(key)) >= 0 &&
                            timeMap.get(result.getString("end_time")).compareTo(timeMap.get(key)) != 0) {
                        timeRange.add(timeMap.get(key));
                    }
                    else {
                        break;
                    }
                }                                                                
            }
            
            if (timeRange.contains(startTime)) {
                return true;
            }
        }                                
        
        
        return false;
    }
    
    public boolean hasClassConflict(String[] submittedClassList, String submittedStudentLogin, String submittedTeacherLogin) throws SQLException {                
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select class.id as class_id, class.name, class.start_time, class.end_time from class_schedule join class "
                                        + "on class_schedule.class_id = class.id where "
                                        + "class_schedule.student_id = (select id from student where login = ?)"
                                        + "and class_schedule.teacher_id = (select id from teacher where login = ?);");
        
        preparedStatement.setString(1, submittedStudentLogin);
        
        preparedStatement.setString(2, submittedTeacherLogin);
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            if (Arrays.asList(submittedClassList).contains("" + result.getInt("class_id"))) {
                return true;
            }
            else if (hasTimeConflict(submittedClassList, result.getString("start_time"), result.getString("end_time"))) {
                return true;
            }
        }
        
        return false;
    }
    
    public void validateSchedule(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {        
        
        String[] submittedClassList = (String[])classListUI.getLocalValue();
        String submittedStudentLogin = studentLoginUI.getLocalValue().toString();
        String submittedTeacherLogin = value.toString();

        if (hasClassConflict(submittedClassList, submittedStudentLogin, submittedTeacherLogin)) {
            FacesMessage errorMessage = new FacesMessage("These classes have a time conflict.");
            throw new ValidatorException(errorMessage);
        }
    }
    
    public void clear() {
        this.cl.setName(null);
    }
}
