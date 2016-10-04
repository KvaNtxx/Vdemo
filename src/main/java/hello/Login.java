package hello;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by KvaNt on 05.10.2016.
 */
@SpringUI(path = "/login")
@Theme("valo")
public class Login extends UI {
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        LoginForm loginForm = new LoginForm();
        VerticalLayout loginLayout = new VerticalLayout(loginForm);
        loginLayout.setHeight(100,Unit.PERCENTAGE);
        loginLayout.setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
        setContent(loginLayout);
    }
}
