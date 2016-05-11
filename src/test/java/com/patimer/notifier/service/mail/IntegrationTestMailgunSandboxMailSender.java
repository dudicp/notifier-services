package com.patimer.notifier.service.mail;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class IntegrationTestMailgunSandboxMailSender
{
    @Test
    public void testSendToOneRecipient()
    {
        // given
        String recipient = "dudicp@gmail.com";

        // when
        sendTestMail(Collections.singletonList(recipient));

        // then - no exception
    }

    @Test
    public void testSendToMultipleRecipients()
    {
        // given
        List<String> recipients = Arrays.asList("dudicp@gmail.com", "patimerdudi@gmail.com");

        // when
        sendTestMail(recipients);

        // then - no exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendToEmptyRecipients()
    {
        // given
        // when
        sendTestMail(Collections.emptyList());
        // then - except exception
    }

    private void sendTestMail(List<String> recipients)
    {
        String from = "Mailgun Sandbox <postmaster@sandboxca73482de7734d1aa0d83d5d9cedcc96.mailgun.org>";
        String subject = "Hello David";
        String text =
            "Congratulations David, you just sent an email with Mailgun!  You are truly awesome!  " +
            "You can see a record of this email in your logs: https://mailgun.com/cp/log . " +
            "You can send up to 300 emails/day from this sandbox server. " +
            "Next, you should add your own domain so you can send 10,000 emails/month for free.";

        String url = "https://api.mailgun.net/v3/sandboxca73482de7734d1aa0d83d5d9cedcc96.mailgun.org/messages";
        String apiKey = "<PLACE YOUR KEY HERE>";

        MailgunSandboxMailSenderImpl mailgunSandboxMailSender = new MailgunSandboxMailSenderImpl();
        mailgunSandboxMailSender.setUrl(url);
        mailgunSandboxMailSender.setApiKey(apiKey);
        MailMessage mailMessage = new MailMessage(from, recipients, subject, text);
        mailgunSandboxMailSender.send(mailMessage);
    }
}
