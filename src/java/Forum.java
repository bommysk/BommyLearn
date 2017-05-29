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

@Named(value = "forum")
@SessionScoped
@ManagedBean
public class Forum {
    private DBConnect dbConnect = new DBConnect();
    private String comment;
    private Teacher teacher;
    private Student student;
    private Class cl;

    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Class getCl() {
        return cl;
    }

    public void setCl(Class cl) {
        this.cl = cl;
    }
    
    public String createTeacherForumHTML() throws SQLException {
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select class.name as class_name, class.description as class_description, " +
                                        "class.day_schedule as day_schedule, class.start_time as start_time, " +
                                        "class.end_time as end_time, teacher.first_name as teacher_first_name, " +
                                        "teacher.last_name as teacher_last_name from class_schedule join class " +
                                        "on class_schedule.class_id = class.id join teacher on class_schedule.teacher_id = " +
                                        "teacher.id where class_schedule.student_id = (select id from student where login = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        
        String tempForumHTML = 
        "<div class=\"panel-group\" id=\"accordion\">\n" +
          "<div class=\"panel panel-primary\">\n" +
          "<div class=\"panel-heading\">\n" +
            "<h4 class=\"panel-title\">\n" +
                "<a data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse1\">Collapsible Group 1</a>\n" +
            "</h4>\n" +
           "</div>\n" +
           "<div id=\"collapse1\" class=\"panel-collapse collapse in\">\n" +
           "<div class=\"panel-body\">Lorem ipsum dolor sit amet, consectetur adipisicing elit,\n" +
            "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,\n" +
            "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</div>\n" +
          "</div>\n" +
        "</div>";
    
        tempForumHTML += "</div>";
        
        return tempForumHTML;
    }
    
    public String createStudentForumHTML() throws SQLException {
        String forumHTML = "";
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select teacher.login as teacher_login, student.login as student_login, student_comment.comment \n" +
                                        "from forum left join teacher_comment on forum.teacher_comment_id = teacher_comment.id \n" +
                                        "join student_comment on forum.student_comment_id = student_comment.id left join teacher on \n" +
                                        "teacher_comment.teacher_id = teacher.id join student on student_comment.student_id = student.id\n" +
                                        "where student_comment.class_id = ?;");
        
        ResultSet resultSet;
        
        List<Schedule> studentSchedule = (new Schedule()).getStudentSchedule();
        
        for (Schedule sched : studentSchedule) {
            preparedStatement.setInt(1, this.student.getClassId(sched.getCl().getName()));
            resultSet = preparedStatement.executeQuery();
            
            forumHTML += 
              "<div class=\"panel-group\" id=\"accordion\">\n" +
              "<div class=\"panel panel-primary\">\n" +
              "<div class=\"panel-heading\">\n" +
                "<h4 class=\"panel-title\">\n" +
                    "<a data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse1\">" + sched.getCl().getName() + "</a>\n" +
                "</h4>\n" +
                "</div>\n" +
                "<div id=\"collapse1\" class=\"panel-collapse collapse in\">\n" +
                "<div class=\"panel panel-default\">\n" +
                "<div class=\"panel-heading\">\n" +
                    "<i class=\"fa fa-clock-o fa-fw\"></i> Forum\n" +
                "</div>\n" +
                "<!-- /.panel-heading -->\n" +
                "<div class=\"panel-body\">\n" +
                "<ul class=\"timeline\" id=\"" + sched.getCl().getName() + "_forum\"" + ">";
                    // loop and generate comments for each li
                
            while (resultSet.next()) {
                forumHTML += "<li>\n" +
                        "<div class=\"timeline-badge\"><i class=\"fa fa-check\"></i>\n" +
                        "</div>\n" +
                        "<div class=\"timeline-panel\">\n" +
                            "<div class=\"timeline-heading\">\n" +
                                "<p><small class=\"text-muted\"><i class=\"fa fa-clock-o\"></i> 11 hours ago via Twitter</small></p>\n" +
                            "</div>\n" +
                            "<div class=\"timeline-body\">\n" +
                                "<p>" + resultSet.getString("comment") + "</p>\n" +
                            "</div>\n" +
                        "</div>\n" +
                    "</li>";
            }
            
                    
            forumHTML += "</ul>" +
                         "</div></div></div></div></div>";
            
        }
        
        return forumHTML;
    }
    
    public void addStudentComment() throws SQLException, Exception {
        Connection con = dbConnect.getConnection();
        String className = cl.getName();
        String teacherLogin = teacher.getTeacherLogin();
        String studentLogin = student.getStudentLogin();
        int studentCommentId;

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        if (teacherLogin.equals("Select Teacher")) {
            teacherLogin = null;
        }
        
        if (studentLogin.equals("Select Student")) {
            studentLogin = null;
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("INSERT INTO student_comment(comment, class_id, student_id, teacher_response_id, student_response_id) "
                        + "VALUES(?, ?, (select id from student where login = ?), "
                        + "(select id from teacher where login = ?), (select id from student where login = ?));", PreparedStatement.RETURN_GENERATED_KEYS);
        
        preparedStatement.setString(1, comment);
        preparedStatement.setInt(2, this.student.getClassId(className));
        preparedStatement.setString(3, Util.getStudentLogin());
        preparedStatement.setString(4, teacherLogin);
        preparedStatement.setString(5, studentLogin);
        
        preparedStatement.executeUpdate();
        
        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        if ( resultSet.next() ) {
            // Retrieve the auto generated key(s).
            studentCommentId = resultSet.getInt(1);
        }
        else {
            throw new Exception("Student comment id not set.");
        }
        
        preparedStatement
                = con.prepareStatement("INSERT INTO forum(class_id, teacher_comment_id, student_comment_id) "
                        + "VALUES(?, null, ?);");
        
        preparedStatement.setInt(1, this.student.getClassId(className));
        preparedStatement.setInt(2, studentCommentId);
        
        preparedStatement.executeUpdate();
        
        clear();
    }
    
    public void onload() {
        this.cl = new Class();
        this.teacher = new Teacher();
        this.student = new Student();
    }
    
    public void clear() {        
        this.cl.setName("Select Class");
        this.teacher.setTeacherLogin("Select Teacher");
        this.student.setStudentLogin("Select Student");
        setComment(null);
    }
}
