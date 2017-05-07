
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author skahal
 */
@Named(value = "login")
@SessionScoped
@ManagedBean
public class Login implements Serializable {

    private String teacherLogin;
    private String teacherPassword;
    private String studentLogin;
    private String studentPassword;
    private final DBConnect dbConnect = new DBConnect();
    private UIInput loginUI;

    public UIInput getLoginUI() {
        return loginUI;
    }

    public void setLoginUI(UIInput loginUI) {
        this.loginUI = loginUI;
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
    
    public boolean checkStudentLogin(String login, String password) throws SQLException {
        Connection con = dbConnect.getConnection();
        String loginDB, passwordDB;

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select login, password from student where"
                                + " login = ? and password = ?");
        
        ps.setString(1, login);
        ps.setString(2, password);
        
        ResultSet result = ps.executeQuery();
        
        if (! result.next()) {
            return false;
        }

        loginDB = result.getString("login");
        passwordDB = result.getString("password");
        
        System.out.println(loginDB);
        System.out.println(passwordDB);
        
        result.close();
        con.close();
        
        return (login.equals(loginDB) && password.equals(passwordDB));
    }
    
    public boolean checkTeacherLogin(String login, String password) throws SQLException {
        Connection con = dbConnect.getConnection();
        String loginDB, passwordDB;

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select login, password from teacher where"
                                + " login = ? and password = ?");
        
        ps.setString(1, login);
        ps.setString(2, password);
        
        ResultSet result = ps.executeQuery();
        
        if (! result.next()) {
            return false;
        }

        loginDB = result.getString("login");
        passwordDB = result.getString("password");
        
        System.out.println(loginDB);
        System.out.println(passwordDB);
        
        result.close();
        con.close();
        
        return (login.equals(loginDB) && password.equals(passwordDB));
    }

    public void validateStudent(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        String submittedLogin = loginUI.getLocalValue().toString();
        String submittedPassword = value.toString();

        if (! checkStudentLogin(submittedLogin, submittedPassword)) {
            FacesMessage errorMessage = new FacesMessage("Wrong login/password");
            throw new ValidatorException(errorMessage);
        }
    }
    
    public void validateTeacher(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        String submittedLogin = loginUI.getLocalValue().toString();
        String submittedPassword = value.toString();

        if (! checkStudentLogin(submittedLogin, submittedPassword)) {
            FacesMessage errorMessage = new FacesMessage("Wrong login/password");
            throw new ValidatorException(errorMessage);
        }
    }

    public String studentGo() {
        Util.validateStudentSession(studentLogin);
        
        return "success";
    }
    
    public String teacherGo() {       
        Util.validateTeacherSession(teacherLogin);
        
        return "success";
    }
    
    //logout event, invalidate session
    public String teacherLogout() {
        Util.invalidateSession();

        return "logout";
    }
    
    //logout event, invalidate session
    public String studentLogout() {
        Util.invalidateSession();

        return "logout";
    }
}
