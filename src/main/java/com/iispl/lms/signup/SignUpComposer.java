package com.iispl.lms.signup;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

public class SignUpComposer extends SelectorComposer<Window> {

	private static final long serialVersionUID = 1L;

    @Wire private Vlayout step1, step2, step3, step4;
    @Wire private Button nextBtn, prevBtn, submitBtn;
    @Wire private Div progressBar;

    // Step 1 fields
    @Wire private Textbox fullName, email, password, confirmPassword;
    @Wire private Longbox phone, income;
    @Wire private Label strengthLabel;

    // Step 2 fields
    @Wire private Datebox dob;
    @Wire private Combobox gender, nationality, idType, occupation;
    @Wire private Textbox idNumber;
    @Wire private Fileupload idUpload;
    @Wire private Label fileNameLabel;

    // Step 3 fields
    @Wire private Textbox address1, address2, country, city, postalCode;
    @Wire private Combobox state;

    // Step 4 fields
    @Wire private Combobox securityQuestion;
    @Wire private Textbox securityAnswer, otpBox;
    @Wire private Checkbox twoFA, terms, privacy;
    @Wire private Button sendOtpBtn, verifyOtpBtn;
    @Wire private Label otpTimer;

    // internal state
    private int currentStep = 1;
    private Timer otpTimerComp;          
    private int otpSecondsLeft = 0;
    private String generatedOtp = null;
    private boolean otpVerified = false;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        
        if (country != null) {
            country.setValue("India");
            country.setReadonly(true);
        }

        // initialize UI state
        updateStep();
        if (fileNameLabel != null) fileNameLabel.setValue("");
        if (strengthLabel != null) {
            strengthLabel.setValue("Strength: ");
            strengthLabel.setStyle("color:gray");
        }
        // hide OTP controls initially if present
        if (twoFA != null) {
            boolean en = twoFA.isChecked();
            toggleOtpUI(en);
        } else {
            toggleOtpUI(false);
        }

        // Password live strength check: use ON_CHANGING to get live text
        if (password != null) {
            password.addEventListener(Events.ON_CHANGING, (Event ev) -> {
                if (ev instanceof InputEvent) {
                    String val = ((InputEvent) ev).getValue();
                    checkPasswordStrength(val);
                } else {
                    checkPasswordStrength(password.getValue());
                }
            });
        }

        // Protect against nulls for buttons that may be missing
        if (sendOtpBtn != null) sendOtpBtn.setDisabled(false);
        if (verifyOtpBtn != null) verifyOtpBtn.setDisabled(true);
    }

    /* ---------------- Navigation ---------------- */

    @Listen("onClick = #nextBtn")
    public void onNextClick() {
        if (validateCurrentStep()) {
            if (currentStep < 4) currentStep++;
            updateStep();
        }
    }

    @Listen("onClick = #prevBtn")
    public void onPrevClick() {
        if (currentStep > 1) currentStep--;
        updateStep();
    }

    private void updateStep() {
        if (step1 != null) step1.setVisible(currentStep == 1);
        if (step2 != null) step2.setVisible(currentStep == 2);
        if (step3 != null) step3.setVisible(currentStep == 3);
        if (step4 != null) step4.setVisible(currentStep == 4);

        if (prevBtn != null) prevBtn.setDisabled(currentStep == 1);
        if (nextBtn != null) nextBtn.setVisible(currentStep < 4);
        if (submitBtn != null) submitBtn.setVisible(currentStep == 4);

        if (progressBar != null) {
            int progress = currentStep * 25;
            progressBar.setStyle("width:" + progress + "%; background-color:#1565c0; height:10px; border-radius:20px; transition:width 0.5s ease;");
        }
    }

    /* ---------------- Validations ---------------- */

    private boolean validateCurrentStep() {
        switch (currentStep) {
            case 1:
                if (isEmpty(fullName) || isEmpty(email) || phone == null || phone.getValue() == null ||
                        isEmpty(password) || isEmpty(confirmPassword)) {
                    showWarning("Please fill all fields in Step 1.");
                    return false;
                }
                // email basic check
                if (!isValidEmail(email.getValue().trim())) {
                    showWarning("Please enter a valid email address.");
                    return false;
                }
                // phone length 10
                String ph = String.valueOf(phone.getValue());
                if (ph.length() != 10) {
                    showWarning("Phone number must be exactly 10 digits.");
                    return false;
                }
                // password match & strength rule: at least 8 chars, uppercase, lowercase, digit, special
                if (!password.getValue().equals(confirmPassword.getValue())) {
                    showWarning("Passwords do not match!");
                    return false;
                }
                if (!password.getValue().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
                    showWarning("Password must be at least 8 characters and include uppercase, lowercase, number and special character.");
                    return false;
                }
                break;

            case 2:
                if (dob == null || dob.getValue() == null) {
                    showWarning("Please select your date of birth.");
                    return false;
                }
                if (!validateDob(dob.getValue())) return false;

                if (isEmpty(gender) || isEmpty(nationality) || isEmpty(idType) || isEmpty(idNumber) ||
                        isEmpty(occupation) || income == null || income.getValue() == null) {
                    showWarning("Please complete all personal details in Step 2.");
                    return false;
                }
                // ensure file uploaded (if you want mandatory)
                if (fileNameLabel == null || fileNameLabel.getValue() == null || fileNameLabel.getValue().trim().isEmpty()) {
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
                // If 2FA enabled -> ensure OTP verified
                if (twoFA != null && twoFA.isChecked()) {
                    if (!otpVerified) {
                        showWarning("Please complete OTP verification for 2FA.");
                        return false;
                    }
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
        if (m != null) {
            if (fileNameLabel != null) fileNameLabel.setValue(m.getName());
            // TODO: persist the uploaded file where needed
        }
    }

    /* ---------------- 2FA / OTP ---------------- */

    @Listen("onCheck = #twoFA")
    public void onTwoFACheck() {
        boolean enabled = twoFA.isChecked();
        toggleOtpUI(enabled);

        if (!enabled) {
            // reset OTP state if user disables 2FA
            generatedOtp = null;
            otpVerified = false;
            stopOtpTimer();
            if (otpBox != null) otpBox.setValue("");
            if (otpTimer != null) otpTimer.setValue("");
        }
    }

    private void toggleOtpUI(boolean visible) {
        if (sendOtpBtn != null) sendOtpBtn.setVisible(visible);
        if (otpBox != null) otpBox.setVisible(visible);
        if (verifyOtpBtn != null) verifyOtpBtn.setVisible(visible);
        if (otpTimer != null) otpTimer.setVisible(visible);
        if (sendOtpBtn != null) sendOtpBtn.setDisabled(!visible);
        if (verifyOtpBtn != null) verifyOtpBtn.setDisabled(true);
    }

    @Listen("onClick = #sendOtpBtn")
    public void onSendOtp() {
        // generate 6-digit OTP for demo
        generatedOtp = String.format("%06d", new Random().nextInt(1_000_000));
        otpVerified = false;

        // In production: send via SMS/email gateway. Here we show demo notification.
        Clients.showNotification("OTP (demo): " + generatedOtp, "info", sendOtpBtn, "after_center", 3000);

        // enable verify and start timer
        if (verifyOtpBtn != null) verifyOtpBtn.setDisabled(false);
        startOtpTimer(60);
    }

    @Listen("onClick = #verifyOtpBtn")
    public void onVerifyOtp() {
        if (generatedOtp == null) {
            showWarning("Please click Send OTP first.");
            return;
        }
        if (otpBox == null || otpBox.getValue() == null || otpBox.getValue().trim().isEmpty()) {
            showWarning("Please enter the OTP.");
            return;
        }
        if (generatedOtp.equals(otpBox.getValue().trim())) {
            otpVerified = true;
            Clients.showNotification("OTP verified successfully.", "info", verifyOtpBtn, "after_center", 2000);
            stopOtpTimer();
            if (otpTimer != null) otpTimer.setValue("OTP verified ✓");
            if (verifyOtpBtn != null) verifyOtpBtn.setDisabled(true);
        } else {
            showWarning("Incorrect OTP. Please try again.");
        }
    }

    private void startOtpTimer(int seconds) {
        stopOtpTimer();
        otpSecondsLeft = seconds;
        if (otpTimer != null) otpTimer.setValue("OTP expires in " + otpSecondsLeft + "s");

        otpTimerComp = new Timer();
        otpTimerComp.setDelay(1000);
        otpTimerComp.setRepeats(true);
        otpTimerComp.setParent(getSelf()); // attach to current window so it runs
        otpTimerComp.addEventListener(Events.ON_TIMER, (Event ev) -> {
            otpSecondsLeft--;
            if (otpTimer != null) otpTimer.setValue("OTP expires in " + otpSecondsLeft + "s");
            if (otpSecondsLeft <= 0) {
                stopOtpTimer();
                if (verifyOtpBtn != null) verifyOtpBtn.setDisabled(true);
                if (otpTimer != null) otpTimer.setValue("OTP expired. Please resend.");
                // optionally allow resend by re-enabling sendOtpBtn
                if (sendOtpBtn != null) sendOtpBtn.setDisabled(false);
            }
        });
        otpTimerComp.start();
    }

    private void stopOtpTimer() {
        if (otpTimerComp != null) {
            try {
                otpTimerComp.stop();
            } catch (Exception ignored) {}
            try {
                otpTimerComp.detach();
            } catch (Exception ignored) {}
            otpTimerComp = null;
        }
    }

    /* ---------------- Password Strength ---------------- */

    private void checkPasswordStrength(String pwd) {
        if (strengthLabel == null) return;
        String strength = "Weak";
        String color = "red";

        if (pwd != null && !pwd.trim().isEmpty()) {
            if (pwd.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
                strength = "Strong";
                color = "green";
            } else if (pwd.length() >= 6) {
                strength = "Medium";
                color = "orange";
            }
        } else {
            strength = "Weak";
            color = "gray";
        }
        strengthLabel.setValue("Strength: " + strength);
        strengthLabel.setStyle("color:" + color);
    }

    /* ---------------- Submit ---------------- */

    @Listen("onClick = #submitBtn")
    public void onSubmit() {
        // Validate all steps before final submit
        for (int i = 1; i <= 4; i++) {
            currentStep = i;
            if (!validateCurrentStep()) {
                updateStep();
                return;
            }
        }
        updateStep();
        // All validations passed
        Clients.showNotification("🎉 Registration completed successfully!", "info", submitBtn, "after_center", 2500);

        // TODO: Persist user to DB or call service here.
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
        if (em == null) return false;
        return em.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$") ||
               em.matches("(?i)^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$");
    }
}
