
import java.io.Serializable;

import java.util.*;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Named(value = "util")
@SessionScoped
@ManagedBean
public class Util implements Serializable {

    public static void invalidateSession() {
        HttpSession session = getSession();
        session.invalidate();
    }
    
    public static String validateStudentSession(String studentLogin) {
        HttpSession session = getSession();
        session.setAttribute("studentLogin", studentLogin);
        return "success";
    }
    
     public static String validateTeacherSession(String teacherLogin) {
        HttpSession session = getSession();
        session.setAttribute("teacherLogin", teacherLogin);
        return "success";
    }

    public void validateDate(FacesContext context, UIComponent component, Object value)
            throws Exception {

        try {
            Date d = (Date) value;
        } catch (Exception e) {
            FacesMessage errorMessage = new FacesMessage("Input is not a valid date");
            throw new ValidatorException(errorMessage);
        }
    }
    
    public static HttpSession getSession() {
        return (HttpSession) FacesContext.getCurrentInstance()
                            .getExternalContext().getSession(false);
    }

    public static HttpServletRequest getRequest() {
            return (HttpServletRequest) FacesContext.getCurrentInstance()
                            .getExternalContext().getRequest();
    }

    public static String getStudentLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                        .getExternalContext().getSession(false);
        
        System.out.println("STUDENT SESSION:");
        System.out.println(session.getAttribute("studentLogin").toString());
        
        return session.getAttribute("studentLogin").toString();
    }
    
    public static String getTeacherLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                        .getExternalContext().getSession(false);
        
        return session.getAttribute("teacherLogin").toString();
    }
    
    public String refreshAddClass() {
        return "addClass";
    }
}
