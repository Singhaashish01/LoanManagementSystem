package com.iispl.lms.dashboard.admin;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class Report {
	
	private int id;
    private String applicant;
    private double amount;
    private String type;
    private String status;
    private Date appliedDate;
    private int daysOverdue;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

    public Report(int id, String applicant, double amount, String type,
                        String status, Date appliedDate, int daysOverdue) {

        this.id = id;
        this.applicant = applicant;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.appliedDate = appliedDate;
        this.daysOverdue = daysOverdue;
    }

    public int getId() { return id; }
    public String getApplicant() { return applicant; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public Date getAppliedDate() { return appliedDate; }
    public int getDaysOverdue() { return daysOverdue; }
    public String getDateAsString() { return sdf.format(appliedDate); }

}
