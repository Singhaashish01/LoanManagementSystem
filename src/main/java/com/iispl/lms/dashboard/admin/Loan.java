package com.iispl.lms.dashboard.admin;

public class Loan {
	
	private int id;
    private String applicant;
    private String amount;
    private String type;
    private String status;
    private String date;

    public Loan(int id, String applicant, String amount, String type, String status, String date) {
        this.id = id;
        this.applicant = applicant;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.date = date;
    }

    public int getId() { 
    	return id; 
    	}
    public String getApplicant() { 
    	return applicant; 
    	}
    public String getAmount() { 
    	return amount; 
    	}
    public String getType() { 
    	return type; 
    	}
    public String getStatus() { 
    	return status; 
    	}
    public String getDate() { 
    	return date; 
    	}

}
