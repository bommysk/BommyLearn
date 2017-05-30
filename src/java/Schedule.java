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
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

@Named(value = "schedule")
@SessionScoped
@ManagedBean
public class Schedule {
    private DBConnect dbConnect = new DBConnect();
    Class cl = new Class();
    Student student = new Student();
    Teacher teacher = new Teacher();

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
            cl.setStartTime(result.getTime("start_time"));
            cl.setEndTime(result.getTime("end_time"));
                        
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
            cl.setStartTime(result.getTime("start_time"));
            cl.setEndTime(result.getTime("end_time"));
            
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
    
    public List<String> getTeacherStudents() throws SQLException {
        Connection con = dbConnect.getConnection();
        List<String> teacherStudents = new ArrayList<>();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select distinct student.login as student_login from class_schedule join student "
                                        + "on class_schedule.student_id = student.id where "
                                        + "class_schedule.teacher_id = (select id from teacher where login = ?);");
        
        preparedStatement.setString(1, Util.getTeacherLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            teacherStudents.add(result.getString("student_login"));
        }
        
        return teacherStudents;
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
    
    public List<String> getStudentTeachers() throws SQLException {
        Connection con = dbConnect.getConnection();
        List<String> studentTeachers = new ArrayList<>();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select distinct teacher.login as teacher_login from class_schedule join teacher "
                                        + "on class_schedule.teacher_id = teacher.id where "
                                        + "class_schedule.student_id = (select id from student where login = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            studentTeachers.add(result.getString("teacher_login"));
        }
        
        return studentTeachers;
    }
    
    public List<String> getStudentClassMates() throws SQLException {
        Connection con = dbConnect.getConnection();
        List<String> studentClassMates = new ArrayList<>();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select student.login as student_login from class_schedule join student on "
                                        + "class_schedule.student_id = student.id where class_schedule.class_id in "
                                        + "(select distinct class.id from class_schedule join class "
                                        + "on class_schedule.class_id = class.id where "
                                        + "class_schedule.student_id = (select id from student where login = ?)) and "
                                        + "student.id != (select id from student where login = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        preparedStatement.setString(2, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            studentClassMates.add(result.getString("student_login"));
        }
        
        return studentClassMates;
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
}
