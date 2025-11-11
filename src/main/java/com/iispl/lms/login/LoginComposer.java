package com.iispl.lms.login;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class LoginComposer extends SelectorComposer<Window>{

	@Wire
    private Div div1;
    @Wire
    private Div div2;

    @Wire
    private Textbox userid;

    @Wire
    private Textbox password;

    @Wire
    private Textbox phoneno;

    @Wire
    private Textbox otp;

    @Wire
    private Checkbox rememberMe;

    @Wire
    private Button loginBtn;

    @Wire
    private Button otpBtn;

    @Wire
    private Button backBtn;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
    }

    @Listen("onClick = #loginBtn")
    public void doLogin() {
        if (div1.isVisible()) {
            String user = userid.getValue().trim();
            String pass = password.getValue().trim();
            boolean remember = rememberMe.isChecked();

            if (user.isEmpty() || pass.isEmpty()) {
                Clients.showNotification("Please enter both Username and Password!", "warning", null, "middle_center",
                        2000);
                return;
            }

            if (user.equals("admin") && pass.equals("12345")) {
                alert("login successsfully");
                if (remember) {
                    Clients.showNotification("Welcome back, " + user + "! (Remember Me enabled)", "info", null,
                            "top_center", 2500);
                } else {
                    Clients.showNotification("Welcome, " + user + "!", "info", null, "top_center", 2500);
                }
               
            } else {
                Clients.showNotification("Invalid username or password!", "error", null, "middle_center", 2500);
            }
        } else {
            String phone = phoneno.getValue().trim();
            String otpstr = otp.getValue().trim();
            boolean remember = rememberMe.isChecked();

            if (phone.isEmpty()) {
                Clients.showNotification("Please enter Phone No.!", "warning", null, "middle_center", 2000);
                return;
            }
            if (otpstr.isEmpty()) {
                Clients.showNotification("Please enter OTP.!", "warning", null, "middle_center", 2000);
                return;
            }

            if (phone.equals("1234567890") && otpstr.equals("12345")) {
                alert("login successsfully");
                Executions.sendRedirect("dashboard.zul");
            } else {
                Clients.showNotification("Invalid Phone No. or password!", "error", null, "middle_center", 2500);
            }
        }
    }

    @Listen("onClick = #otpBtn")
    public void doLoginWithOtp() {
        div1.setVisible(false);
        div2.setVisible(true);
        otpBtn.setVisible(false);
        backBtn.setVisible(true);
    }

    @Listen("onClick = #backBtn")
    public void doBackLogin() {
        div1.setVisible(true);
        div2.setVisible(false);
        otpBtn.setVisible(true);
        backBtn.setVisible(false);
    }
}
