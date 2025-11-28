package com.iispl.lms.homepage;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class HomePageComposer extends SelectorComposer<Window> {

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
	@Listen("onClick = #signBtn")
	public void onSignUpClick() {
		Executions.sendRedirect("SignUp.zul");
	}

	@Listen("onClick = #logBtn")
	public void onLoginClick() {
		Executions.sendRedirect("login.zul");
	}

	// HERO BUTTON
	@Listen("onClick = #knowMBtn")
	public void onKnowMoreClick() {
		Executions.sendRedirect("AboutUs.zul");
	}

	// OPTIONAL NAVBAR LINKS
	@Listen("onClick = #homeNav")
	public void onHomeClick() {
		Executions.sendRedirect("Homepage.zul");
	}

	@Listen("onClick = #aboutNav")
	public void onAboutClick() {
		Executions.sendRedirect("AboutUs.zul");
	}

	@Listen("onClick = #serviceNav")
	public void onServicesClick() {
		Executions.sendRedirect("services.zul");
	}

	@Listen("onClick = #contactNav")
	public void onContactClick() {
		Executions.sendRedirect("ContactUs.zul");
	}
}