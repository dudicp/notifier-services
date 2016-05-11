package com.patimer.notifier.service.mail;

import org.apache.commons.lang.Validate;

import java.util.List;

public class MailMessage
{
    private String from;
    private List<String> recipient;
    private String subject;
    private String text;

    public MailMessage(String from, List<String> recipient, String subject, String text)
    {
        Validate.notEmpty(from);
        Validate.notEmpty(recipient);
        Validate.notNull(subject);
        Validate.notNull(text);

        this.from = from;
        this.recipient = recipient;
        this.subject = subject;
        this.text = text;
    }

    public String getFrom()
    {
        return from;
    }

    public List<String> getRecipient()
    {
        return recipient;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getText()
    {
        return text;
    }
}
