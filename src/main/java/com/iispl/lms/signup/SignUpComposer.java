package com.iispl.lms.signup;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.util.media.Media;

import java.time.*;
import java.util.*;

public class SignUpComposer extends SelectorComposer<Window> {

	private static final long serialVersionUID = 1L;

	@Wire
	private Vlayout step1, step2, step3, step4;
	@Wire
	private Button nextBtn, prevBtn, submitBtn;
	@Wire
	private Div progressBar;

	// Step 1 fields
	@Wire
	private Textbox fullName, email, password, confirmPassword;
	@Wire
	private Longbox phone, income;
	@Wire
	private Button togglePwdBtn, toggleConfirmPwdBtn;

	// Step 2 fields
	@Wire
	private Datebox dob;
	@Wire
	private Combobox gender, nationality, idType, occupation;
	@Wire
	private Textbox idNumber;
	@Wire
	private Fileupload idUpload;
	@Wire
	private Label fileNameLabel;

	// Step 3 fields
	@Wire
	private Textbox address1, address2, country, city, postalCode;
	@Wire
	private Combobox state;

	// Step 4 fields
	@Wire
	private Combobox securityQuestion;
	@Wire
	private Textbox securityAnswer, otpBox;
	@Wire
	private Checkbox twoFA, terms, privacy;
	@Wire
	private Button sendOtpBtn, verifyOtpBtn;

	// internal state
	private int currentStep = 1;
	private String generatedOtp = null;
	private boolean otpVerified = false;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		if (country != null) {
			country.setValue("India");
			country.setReadonly(true);
		}

		updateStep();
		if (fileNameLabel != null)
			fileNameLabel.setValue("");
	}

	// password-eye
	@Listen("onClick = #togglePwdBtn")
	public void onTogglePasswordVisibility() {
		togglePasswordVisibility(password, togglePwdBtn);
	}

	@Listen("onClick = #toggleConfirmPwdBtn")
	public void onToggleConfirmPasswordVisibility() {
		togglePasswordVisibility(confirmPassword, toggleConfirmPwdBtn);
	}

	private void togglePasswordVisibility(Textbox textbox, Button toggleBtn) {
		if (textbox == null || toggleBtn == null)
			return;
		boolean isPassword = "password".equalsIgnoreCase(textbox.getType());
		textbox.setType(isPassword ? "text" : "password");
		toggleBtn.setIconSclass(isPassword ? "z-icon-eye-slash" : "z-icon-eye");
	}

	// Navigation
	@Listen("onClick = #nextBtn")
	public void onNextClick() {
		if (validateCurrentStep()) {
			if (currentStep < 4)
				currentStep++;
			updateStep();
		}
	}

	@Listen("onClick = #prevBtn")
	public void onPrevClick() {
		if (currentStep > 1)
			currentStep--;
		updateStep();
	}

	private void updateStep() {
		if (step1 != null)
			step1.setVisible(currentStep == 1);
		if (step2 != null)
			step2.setVisible(currentStep == 2);
		if (step3 != null)
			step3.setVisible(currentStep == 3);
		if (step4 != null)
			step4.setVisible(currentStep == 4);

		if (prevBtn != null)
			prevBtn.setDisabled(currentStep == 1);
		if (nextBtn != null)
			nextBtn.setVisible(currentStep < 4);
		if (submitBtn != null)
			submitBtn.setVisible(currentStep == 4);

		if (progressBar != null) {
			int progress = currentStep * 25;
			progressBar.setStyle("width:" + progress
					+ "%; background-color:#1565c0; height:10px; border-radius:20px; transition:width 0.5s ease;");
		}
	}

	/* ---------------- Validations ---------------- */
	private boolean validateCurrentStep() {
		switch (currentStep) {
		case 1:
			if (isEmpty(fullName) || isEmpty(email) || phone == null || phone.getValue() == null || isEmpty(password)
					|| isEmpty(confirmPassword)) {
				showWarning("Please fill all fields in Step 1.");
				return false;
			}
			if (!isValidEmail(email.getValue().trim())) {
				showWarning("Please enter a valid email address.");
				return false;
			}
			String ph = String.valueOf(phone.getValue());
			if (ph.length() != 10) {
				showWarning("Phone number must be exactly 10 digits.");
				return false;
			}
			if (!password.getValue().equals(confirmPassword.getValue())) {
				showWarning("Passwords do not match!");
				return false;
			}
			if (!password.getValue().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
				showWarning(
						"Password must be at least 8 characters and include uppercase, lowercase, number and special character.");
				return false;
			}
			break;
		case 2:
			if (dob == null || dob.getValue() == null) {
				showWarning("Please select your date of birth.");
				return false;
			}
			if (!validateDob(dob.getValue()))
				return false;
			if (isEmpty(gender) || isEmpty(nationality) || isEmpty(idType) || isEmpty(idNumber) || isEmpty(occupation)
					|| income == null || income.getValue() == null) {
				showWarning("Please complete all personal details in Step 2.");
				return false;
			}
			if (fileNameLabel == null || fileNameLabel.getValue() == null
					|| fileNameLabel.getValue().trim().isEmpty()) {
				showWarning("Please upload an ID proof file.");
				return false;
			}
			break;
		case 3:
			if (isEmpty(address1) || isEmpty(country) || isEmpty(state) || isEmpty(city) || isEmpty(postalCode)) {
				showWarning("Please complete address details in Step 3.");
				return false;
			}
			break;
		case 4:
			if (isEmpty(securityQuestion) || isEmpty(securityAnswer)) {
				showWarning("Please select and answer a security question.");
				return false;
			}
			if (!terms.isChecked() || !privacy.isChecked()) {
				showWarning("You must agree to Terms & Conditions and Privacy Policy to continue.");
				return false;
			}
			if (twoFA != null && twoFA.isChecked() && !otpVerified) {
				showWarning("Please complete OTP verification for 2FA.");
				return false;
			}
			break;
		}
		return true;
	}

	private boolean validateDob(Date d) {
		LocalDate dobLocal = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate today = LocalDate.now();
		if (dobLocal.isAfter(today)) {
			showWarning("Date of birth cannot be a future date.");
			return false;
		}
		Period p = Period.between(dobLocal, today);
		if (p.getYears() < 21) {
			showWarning("You must be at least 21 years old to register.");
			return false;
		}
		return true;
	}

	/* ---------------- File Upload ---------------- */
	@Listen("onUpload = #idUpload")
	public void onIdUpload(UploadEvent evt) {
		Media m = evt.getMedia();
		if (m != null && fileNameLabel != null) {
			fileNameLabel.setValue(m.getName());
		}
	}

	/* ---------------- 2FA / OTP ---------------- */
	@Listen("onCheck = #twoFA")
	public void onTwoFACheck() {
		boolean enabled = twoFA.isChecked();
		sendOtpBtn.setVisible(enabled);
		otpBox.setVisible(false);
		verifyOtpBtn.setVisible(false);
		generatedOtp = null;
		otpVerified = false;
		if (!enabled && otpBox != null)
			otpBox.setValue("");
	}

	@Listen("onClick = #sendOtpBtn")
	public void onSendOtp() {
		generatedOtp = String.format("%06d", new Random().nextInt(1_000_000));
		otpVerified = false;
		Clients.showNotification("OTP (demo): " + generatedOtp, "info", sendOtpBtn, "after_center", 3000);
		otpBox.setVisible(true);
		verifyOtpBtn.setVisible(true);
	}

	@Listen("onClick = #verifyOtpBtn")
	public void onVerifyOtp() {
		if (generatedOtp == null) {
			showWarning("Please click Send OTP first.");
			return;
		}
		if (otpBox == null || otpBox.getValue().trim().isEmpty()) {
			showWarning("Please enter the OTP.");
			return;
		}
		if (generatedOtp.equals(otpBox.getValue().trim())) {
			otpVerified = true;
			Clients.showNotification("OTP verified successfully.", "info", verifyOtpBtn, "after_center", 2000);
		} else {
			showWarning("Incorrect OTP. Please try again.");
		}
	}

	/* ---------------- Submit ---------------- */
	@Listen("onClick = #submitBtn")
	public void onSubmit() {
		for (int i = 1; i <= 4; i++) {
			currentStep = i;
			if (!validateCurrentStep()) {
				updateStep();
				return;
			}
		}
		updateStep();
		Clients.showNotification("🎉 Registration completed successfully!", "info", submitBtn, "after_center", 2500);
		// TODO: Persist user
	}

	/* ---------------- Helpers ---------------- */
	private void showWarning(String msg) {
		Messagebox.show(msg, "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
	}

	private boolean isEmpty(Textbox t) {
		return t == null || t.getValue() == null || t.getValue().trim().isEmpty();
	}

	private boolean isEmpty(Combobox c) {
		return c == null || (c.getSelectedItem() == null && (c.getValue() == null || c.getValue().trim().isEmpty()));
	}

	private boolean isValidEmail(String em) {
		if (em == null)
			return false;
		return em.matches("(?i)^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$");
	}
}