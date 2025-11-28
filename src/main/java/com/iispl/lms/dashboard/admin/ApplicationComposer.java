package com.iispl.lms.dashboard.admin;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Vlayout;

public class ApplicationComposer extends SelectorComposer<Vlayout>{

	
	private static final long serialVersionUID = 1L;

    @Wire private Label lblTotalApps;
    @Wire private Label lblPending;
    @Wire private Label lblApproved;
    @Wire private Label lblRejected;

    @Wire private Listbox appsList;

    private List<Application> applications = new ArrayList<>();

    @Override
    public void doAfterCompose(Vlayout comp) throws Exception {
        super.doAfterCompose(comp);

        loadApplications();
        showStats();
        appsList.setModel(new ListModelList<>(applications));
    }

    private void loadApplications() {
        applications.add(new Application(1, "Aashish", "Personal Loan", "50,000", "Pending", "18 Nov"));
        applications.add(new Application(2, "Sneha", "Home Loan", "2,00,000", "Approved", "20 Nov"));
        applications.add(new Application(3, "Rohan", "Business Loan", "1,50,000", "Rejected", "21 Nov"));
    }

    private void showStats() {
        lblTotalApps.setValue(applications.size() + "");

        long pending = applications.stream().filter(a -> a.getStatus().equals("Pending")).count();
        long approved = applications.stream().filter(a -> a.getStatus().equals("Approved")).count();
        long rejected = applications.stream().filter(a -> a.getStatus().equals("Rejected")).count();

        lblPending.setValue(pending + "");
        lblApproved.setValue(approved + "");
        lblRejected.setValue(rejected + "");
    }

    @Listen("onClick=#loanList .view-btn")
    public void viewApp(int id) {
        Application selected = applications.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);

        if (selected != null) {
            System.out.println("Viewing application: " + selected.getApplicant());
            // You can open modal here
        }
    }
}
