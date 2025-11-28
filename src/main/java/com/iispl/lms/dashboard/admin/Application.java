package com.iispl.lms.dashboard.admin;

public class Application {
	
	private int id;
    private String applicant;
    private String type;
    private String amount;
    private String status;
    private String date;

    public Application(int id, String applicant, String type, String amount, String status, String date) {
        this.id = id;
        this.applicant = applicant;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.date = date;
    }

    public int getId() { 
    	return id; 
    	}
    public String getApplicant() { 
    	return applicant; 
    	}
    public String getType() { 
    	return type; 
    	}
    public String getAmount() { 
    	return amount; 
    	}
    public String getStatus() { 
    	return status; 
    	}
    public String getDate() { 
    	return date; 
    	}

}
