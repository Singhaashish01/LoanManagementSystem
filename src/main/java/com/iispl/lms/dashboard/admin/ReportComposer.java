package com.iispl.lms.dashboard.admin;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

public class ReportComposer extends SelectorComposer<Vlayout>{

	private static final long serialVersionUID = 1L;


    @Wire private Textbox txtSearch;
    @Wire private Combobox cmbStatus, cmbType;
    @Wire private Datebox dtFrom, dtTo;

    @Wire private Label lblTotal, lblApproved, lblPending, lblOverdue;

    @Wire private Listbox reportList;

    private List<Report> allData = new ArrayList<>();

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

    @Override
    public void doAfterCompose(Vlayout comp) throws Exception {
        super.doAfterCompose(comp);
        loadDummyData();
        refresh();
    }

    private void loadDummyData() {
        allData.add(new Report(1, "Aashish", 50000, "Personal", "Pending", new Date(0), 0));
        allData.add(new Report(2, "Sneha", 120000, "Home", "Approved", new Date(0), 0));
        allData.add(new Report(3, "Rohan", 150000, "Business", "Rejected", new Date(0), 3));
    }

    /** Filter + refresh listbox + summary */
    private void refresh() {
        List<Report> filtered = allData.stream()
                .filter(this::filterSearch)
                .filter(this::filterStatus)
                .filter(this::filterType)
                .filter(this::filterDate)
                .collect(Collectors.toList());

        reportList.setModel(new ListModelList<>(filtered));

        lblTotal.setValue(filtered.size() + "");
        lblApproved.setValue(filtered.stream().filter(r -> r.getStatus().equals("Approved")).count() + "");
        lblPending.setValue(filtered.stream().filter(r -> r.getStatus().equals("Pending")).count() + "");
        lblOverdue.setValue(filtered.stream().filter(r -> r.getDaysOverdue() > 0).count() + "");
    }

    private boolean filterSearch(Report r) {
        String q = txtSearch.getValue().toLowerCase();
        return q.isEmpty() ||
                r.getApplicant().toLowerCase().contains(q) ||
                (r.getId() + "").contains(q);
    }

    private boolean filterStatus(Report r) {
        String st = cmbStatus.getValue();
        return st.equals("") || st.equals("All") || st.equals(r.getStatus());
    }

    private boolean filterType(Report r) {
        String t = cmbType.getValue();
        return t.equals("") || t.equals("All") || t.equals(r.getType());
    }

    private boolean filterDate(Report r) {
        Date from = (Date) dtFrom.getValue();
        Date to   = (Date) dtTo.getValue();
        if (from == null && to == null) return true;
        if (from != null && r.getAppliedDate().before(from)) return false;
        if (to != null && r.getAppliedDate().after(to)) return false;
        return true;
    }

    // ------------------------
    //    LISTEN HANDLERS
    // ------------------------

    @Listen("onClick = #btnSearch")
    public void searchClick() {
        refresh();
    }

    @Listen("onClick = #btnClear")
    public void clearClick() {
        txtSearch.setValue("");
        cmbStatus.setValue("");
        cmbType.setValue("");
        dtFrom.setValue(null);
        dtTo.setValue(null);
        refresh();
    }

    @Listen("onClick = #btnExport")
    public void exportClick() {
        Clients.showNotification("CSV Export Coming Soon!", "info", null, "top_center", 1500);
    }

    /** VIEW button inside listbox row */
    @Listen("onClick = #reportList .view-btn")
    public void onViewRecord(Event e) {

        Listitem item = (Listitem) e.getTarget().getParent().getParent();
        Report record = item.getValue();

        Clients.showNotification(
            "Viewing: " + record.getApplicant() + " (ID " + record.getId() + ")",
            "info", null, "middle_center", 2200
        );
    }
}
