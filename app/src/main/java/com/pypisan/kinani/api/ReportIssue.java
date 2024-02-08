package com.pypisan.kinani.api;

public class ReportIssue {
    final String subject;
    final String title;
    final String message;

    public ReportIssue(String subject, String title, String message) {
        this.subject = subject;
        this.title = title;
        this.message = message;
    }
}
