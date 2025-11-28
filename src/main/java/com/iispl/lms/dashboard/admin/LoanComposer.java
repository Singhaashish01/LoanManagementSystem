package com.iispl.lms.dashboard.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

public class LoanComposer extends SelectorComposer<Vlayout>{
	
	private static final long serialVersionUID = 1L;

    @Wire private Textbox txtSearch;
    @Wire private Combobox cmbStatus;
    @Wire private Listbox loanList;

    private List<Loan> allLoans = new ArrayList<>();

    @Override
    public void doAfterCompose(Vlayout comp) throws Exception {
        super.doAfterCompose(comp);

        loadLoans();
        displayLoans(allLoans);

        // Search button handler
        Button btnSearch = (Button) comp.getFellow("btnSearch");
        btnSearch.addEventListener(Events.ON_CLICK, e -> filterLoans());
    }

    // Load sample data (replace with DB later)
    private void loadLoans() {
        allLoans.add(new Loan(1, "Aashish", "50,000", "Personal", "Pending", "18 Nov"));
        allLoans.add(new Loan(2, "Sneha", "1,50,000", "Home", "Approved", "21 Nov"));
        allLoans.add(new Loan(3, "Rohan", "75,000", "Business", "Rejected", "20 Nov"));
        allLoans.add(new Loan(4, "Imran", "90,000", "Personal", "Approved", "17 Nov"));
    }

    private void filterLoans() {
        String name = txtSearch.getValue().toLowerCase();
        String status = cmbStatus.getValue();

        List<Loan> filtered = allLoans.stream()
            .filter(l -> l.getApplicant().toLowerCase().contains(name))
            .filter(l -> status.equals("") || status.equals("All") || l.getStatus().equalsIgnoreCase(status))
            .collect(Collectors.toList());

        displayLoans(filtered);
    }

    private void displayLoans(List<Loan> list) {
        loanList.setModel(new ListModelList<>(list));
    }

    @Listen("onClick=#loanList .view-btn")
    public void viewLoan(int id) {
        Loan selected = allLoans.stream()
            .filter(l -> l.getId() == id)
            .findFirst().orElse(null);

        if (selected != null) {
            System.out.println("Viewing loan: " + selected.getApplicant());
            // You can redirect or open modal here
        }
    }

}
