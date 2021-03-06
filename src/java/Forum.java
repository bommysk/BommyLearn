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
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
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
        String forumHTML = "";
        Connection con = dbConnect.getConnection();
        int counter;

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("(select teacher_comment.teacher_response_id, teacher_comment.student_response_id, teacher.login, teacher_comment.comment, teacher_comment.post_date\n" +
                                        "from forum join teacher_comment on forum.teacher_comment_id = teacher_comment.id\n" +
                                        "join teacher on teacher_comment.teacher_id = teacher.id\n" +
                                        "where forum.class_id = ?)\n" +
                                        "UNION\n" +
                                        "(select student_comment.teacher_response_id, student_comment.student_response_id, student.login, student_comment.comment, student_comment.post_date\n" +
                                        "from forum join student_comment on forum.student_comment_id = student_comment.id\n" +
                                        "join student on student_comment.student_id = student.id\n" +
                                        "where forum.class_id = ?)\n" +
                                        "order by post_date;");
        
        ResultSet resultSet;
        
        List<Schedule> teacherSchedule = (new Schedule()).getTeacherSchedule();
        
        for (Schedule sched : teacherSchedule) {
            preparedStatement.setInt(1, this.teacher.getClassId(sched.getCl().getName()));
            preparedStatement.setInt(2, this.teacher.getClassId(sched.getCl().getName()));
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
                
            counter = 0;
                
            while (resultSet.next()) {
                if (counter % 2 != 0) {
                    forumHTML += "<li class=\"timeline-inverted\">\n";
                }
                else {
                    forumHTML += "<li>\n";
                }
                
                forumHTML += "<div class=\"timeline-badge\"><i class=\"fa fa-check\"></i>\n" +
                        "</div>\n" +
                        "<div class=\"timeline-panel\">\n" +
                            "<div class=\"timeline-heading\">\n";
                
                
                forumHTML += "<p>" + resultSet.getString("comment") + "</p>\n";

                forumHTML += "<p><small class=\"text-muted\"><i class=\"fa fa-clock-o\"></i> " + resultSet.getString("login") + " " + resultSet.getTimestamp("post_date") + "</small></p>\n";
                
                if (resultSet.getInt("teacher_response_id") != 0) {
                    forumHTML += "<p><small class=\"text-muted\"><i class=\"fa fa-at\"></i> " + (new Teacher()).getTeacherLoginFromId(resultSet.getInt("teacher_response_id")) + "</small></p>\n";
                }
                
                if (resultSet.getInt("student_response_id") != 0) {
                    forumHTML += "<p><small class=\"text-muted\"><i class=\"fa fa-at\"></i> " + (new Student()).getStudentLoginFromId(resultSet.getInt("student_response_id")) + "</small></p>\n";
                }
                
                forumHTML += "</div><div class=\"timeline-body\">\n";
                
                forumHTML += "</div>\n" +
                        "</div>\n" +
                    "</li>";
                
                counter++;
            }
            
                    
            forumHTML += "</ul>" +
                         "</div></div></div></div></div>";
            
        }
        
        return forumHTML;
    }
    
    public void addTeacherComment() throws SQLException, Exception {
        Connection con = dbConnect.getConnection();
        String className = cl.getName();
        String studentLogin = student.getStudentLogin();
        int teacherCommentId;

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        if (studentLogin.equals("Select Student")) {
            studentLogin = null;
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("INSERT INTO teacher_comment(comment, teacher_id, class_id, teacher_response_id, student_response_id, post_date) "
                        + "VALUES(?, (select id from teacher where login = ?), ?, "
                        + "null, (select id from student where login = ?), now());", PreparedStatement.RETURN_GENERATED_KEYS);             
        
        preparedStatement.setString(1, comment);
        preparedStatement.setString(2, Util.getTeacherLogin());
        preparedStatement.setInt(3, this.teacher.getClassId(className));
        preparedStatement.setString(4, studentLogin);
        
        preparedStatement.executeUpdate();
        
        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        if ( resultSet.next() ) {
            // Retrieve the auto generated key(s).
            teacherCommentId = resultSet.getInt(1);
        }
        else {
            throw new Exception("Student comment id not set.");
        }
        
        preparedStatement
                = con.prepareStatement("INSERT INTO forum(class_id, teacher_comment_id, student_comment_id) "
                        + "VALUES(?, ?, null);");
        
        preparedStatement.setInt(1, this.teacher.getClassId(className));
        preparedStatement.setInt(2, teacherCommentId);
        
        preparedStatement.executeUpdate();
        
        clearTeacherComment();
    }
    
    public void clearTeacherComment() {        
        this.cl.setName("Select Class");
        this.student.setStudentLogin("Select Student");
        setComment(null);
    }
   
    public String createStudentForumHTML() throws SQLException {
        String forumHTML = "";
        Connection con = dbConnect.getConnection();
        int counter;

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("(select teacher_comment.teacher_response_id, teacher_comment.student_response_id, teacher.login, teacher_comment.comment, teacher_comment.post_date\n" +
                                        "from forum join teacher_comment on forum.teacher_comment_id = teacher_comment.id\n" +
                                        "join teacher on teacher_comment.teacher_id = teacher.id\n" +
                                        "where forum.class_id = ?)\n" +
                                        "UNION\n" +
                                        "(select student_comment.teacher_response_id, student_comment.student_response_id, student.login, student_comment.comment, student_comment.post_date\n" +
                                        "from forum join student_comment on forum.student_comment_id = student_comment.id\n" +
                                        "join student on student_comment.student_id = student.id\n" +
                                        "where forum.class_id = ?)\n" +
                                        "order by post_date;");

        ResultSet resultSet;
        
        List<Schedule> studentSchedule = (new Schedule()).getStudentSchedule();
        
        for (Schedule sched : studentSchedule) {
            preparedStatement.setInt(1, this.student.getClassId(sched.getCl().getName()));
            preparedStatement.setInt(2, this.student.getClassId(sched.getCl().getName()));
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
            
            counter = 0;
                
            while (resultSet.next()) {
                if (counter % 2 != 0) {
                    forumHTML += "<li class=\"timeline-inverted\">\n";
                }
                else {
                    forumHTML += "<li>\n";
                }
                
                forumHTML += "<div class=\"timeline-badge\"><i class=\"fa fa-check\"></i>\n" +
                        "</div>\n" +
                        "<div class=\"timeline-panel\">\n" +
                            "<div class=\"timeline-heading\">\n";
                
                forumHTML += "<p>" + resultSet.getString("comment") + "</p>\n";

                forumHTML += "<p><small class=\"text-muted\"><i class=\"fa fa-clock-o\"></i> " + resultSet.getString("login") + " " + resultSet.getTimestamp("post_date") + "</small></p>\n";
                
                if (resultSet.getInt("teacher_response_id") != 0) {
                    forumHTML += "<p><small class=\"text-muted\"><i class=\"fa fa-at\"></i> " + (new Teacher()).getTeacherLoginFromId(resultSet.getInt("teacher_response_id")) + "</small></p>\n";
                }
                
                if (resultSet.getInt("student_response_id") != 0) {
                    forumHTML += "<p><small class=\"text-muted\"><i class=\"fa fa-at\"></i> " + (new Student()).getStudentLoginFromId(resultSet.getInt("student_response_id")) + "</small></p>\n";
                }
                
                forumHTML += "</div><div class=\"timeline-body\">\n";
                
                forumHTML += "</div>\n" +
                        "</div>\n" +
                    "</li>";
                
                counter++;
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
                = con.prepareStatement("INSERT INTO student_comment(comment, class_id, student_id, teacher_response_id, student_response_id, post_date) "
                        + "VALUES(?, ?, (select id from student where login = ?), "
                        + "(select id from teacher where login = ?), (select id from student where login = ?), now());", PreparedStatement.RETURN_GENERATED_KEYS);
        
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
        
        clearStudentComment();
    }
    
    public void onload() {
        this.cl = new Class();
        this.teacher = new Teacher();
        this.student = new Student();
    }
    
    public void clearStudentComment() {        
        this.cl.setName("Select Class");
        this.teacher.setTeacherLogin("Select Teacher");
        this.student.setStudentLogin("Select Student");
        setComment(null);
    }
    
    public Integer getTeacherCommentCount() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select ((select count(*) from teacher_comment where teacher_response_id = "
                        + "(select id from teacher where login = ?)) + (select count(*) from student_comment where teacher_response_id = "
                        + "(select id from teacher where login = ?))) as comment_count");

        preparedStatement.setString(1, Util.getTeacherLogin());
        
        preparedStatement.setString(2, Util.getTeacherLogin());
        
        ResultSet resultSet = preparedStatement.executeQuery();
        
        resultSet.next();
        
        return resultSet.getInt("comment_count");
    }
    
    public Integer getStudentCommentCount() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select ((select count(*) from teacher_comment where student_response_id = "
                        + "(select id from student where login = ?)) + (select count(*) from student_comment where student_response_id = "
                        + "(select id from student where login = ?))) as comment_count");

        preparedStatement.setString(1, Util.getStudentLogin());
        
        preparedStatement.setString(2, Util.getStudentLogin());
        
        ResultSet resultSet = preparedStatement.executeQuery();
        
        resultSet.next();
        
        return resultSet.getInt("comment_count");
    }
    
    public List<String> getTeacherStudents() throws SQLException {
        List<String> teacherStudents = new ArrayList<>();            
        
        if (this.cl.getName() == null || this.cl.getName().equals("Select Class")) {                        
            return teacherStudents;
        }
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select distinct student.login as student_login from class_schedule join student "
                                        + "on class_schedule.student_id = student.id join class on class_schedule.class_id = class.id where "
                                        + "class_schedule.teacher_id = (select id from teacher where login = ?) and class.name = ?;");
        
        preparedStatement.setString(1, Util.getTeacherLogin());
        preparedStatement.setString(2, this.cl.getName());
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            teacherStudents.add(result.getString("student_login"));
        }
        
        return teacherStudents;
    }
    
    public List<String> getStudentTeachers() throws SQLException {
        List<String> studentTeachers = new ArrayList<>();
        
        if (this.cl.getName() == null || this.cl.getName().equals("Select Class")) {                        
            return studentTeachers;
        }
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select distinct teacher.login as teacher_login from class_schedule join teacher "
                                        + "on class_schedule.teacher_id = teacher.id join class on class_schedule.class_id = class.id where "
                                        + "class_schedule.student_id = (select id from student where login = ?) and class.name = ?;");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        preparedStatement.setString(2, this.cl.getName());
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            studentTeachers.add(result.getString("teacher_login"));
        }
        
        return studentTeachers;
    }
    
    public List<String> getStudentClassMates() throws SQLException {
        List<String> studentClassMates = new ArrayList<>();       
        
        if (this.cl.getName() == null || this.cl.getName().equals("Select Class")) {                        
            return studentClassMates;
        }
        
        Connection con = dbConnect.getConnection();

        PreparedStatement preparedStatement
                = con.prepareStatement("select student.login as student_login from class_schedule join student on "
                                        + "class_schedule.student_id = student.id where class_schedule.class_id in "
                                        + "(select distinct class.id from class_schedule join class "
                                        + "on class_schedule.class_id = class.id where "
                                        + "class_schedule.student_id = (select id from student where login = ?) and class.name = ?) and "
                                        + "student.id != (select id from student where login = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        preparedStatement.setString(2, this.cl.getName());
        preparedStatement.setString(3, Util.getStudentLogin());      
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            studentClassMates.add(result.getString("student_login"));
        }
        
        return studentClassMates;
    }
    
    public void validateClass(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {        
        String submittedClass = value.toString();

        if (submittedClass.equals("Select Class")) {
            FacesMessage errorMessage = new FacesMessage("Wrong login/password");
            throw new ValidatorException(errorMessage);
        }
    }
    
}
