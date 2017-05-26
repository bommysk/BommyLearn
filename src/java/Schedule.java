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
}
