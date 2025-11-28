package com.iispl.lms.forgot;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ForgotComposer extends SelectorComposer<Window> {

	@Wire
	private Textbox emailBox;

	@Wire
	private Button resetBtn;

	@Listen("onClick = #resetBtn")
	public void sendResetLink() {
		String email = emailBox.getValue();
		if (email == null || email.isEmpty()) {
			Messagebox.show("Please enter your registered email!", "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
		} else {
			// Simulate sending reset link
			Messagebox.show("Password reset link sent to " + email, "Success", Messagebox.OK, Messagebox.INFORMATION);
		}
	}
}
