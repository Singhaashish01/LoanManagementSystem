package com.iispl.lms.homepage;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class HomePageComposer extends SelectorComposer<Window>{


	    @Wire
	    private Label welcomeMsg;

	    @Override
	    public void doAfterCompose(Window comp) throws Exception {
	        super.doAfterCompose(comp);
	        // Set dynamic welcome message (optional)
	        if (welcomeMsg != null) {
	            welcomeMsg.setValue("Welcome to Loan Management System - Your Financial Partner");
	        }
	    }

	    // NAVBAR BUTTONS 
	    @Listen("onClick = button[label='Sign Up']")
	    public void onSignUpClick() {
	        Executions.sendRedirect("SignUp.zul");
	    }

	    @Listen("onClick = button[label='Log In']")
	    public void onLoginClick() {
	        Executions.sendRedirect("login.zul");
	    }

	    // HERO BUTTON 
	    @Listen("onClick = button.know-btn")
	    public void onKnowMoreClick() {
	        Executions.sendRedirect("aboutus.zul");
	    }

	    // OPTIONAL NAVBAR LINKS 
	    @Listen("onClick = label[value='Home']")
	    public void onHomeClick() {
	        Executions.sendRedirect("homepage.zul");
	    }

	    @Listen("onClick = label[value='About Us']")
	    public void onAboutClick() {
	        Executions.sendRedirect("aboutus.zul");
	    }

	    @Listen("onClick = label[value='Services']")
	    public void onServicesClick() {
	        Executions.sendRedirect("services.zul");
	    }

	    @Listen("onClick = label[value='Contact']")
	    public void onContactClick() {
	        Executions.sendRedirect("contact.zul");
	    }
	}