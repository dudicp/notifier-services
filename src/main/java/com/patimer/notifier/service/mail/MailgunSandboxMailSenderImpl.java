package com.patimer.notifier.service.mail;

import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class MailgunSandboxMailSenderImpl implements MailSender
{
    private final static Logger log = LogManager.getLogger(MailgunSandboxMailSenderImpl.class);

    @Value("${mail.mailgun.url}")
    private String url;

    @Value("${mail.mailgun.api.key}")
    private String apiKey;

    protected void setUrl(String url)
    {
        Validate.notEmpty(url);
        this.url = url;
    }

    protected void setApiKey(String apiKey)
    {
        Validate.notEmpty(apiKey);
        this.apiKey = apiKey;
    }

    @PostConstruct
    public void validate()
    {
        Validate.notEmpty(url);
        Validate.notEmpty(apiKey);
    }

    public void send(MailMessage mailMessage)
    {
        Validate.notNull(mailMessage);

        if(log.isDebugEnabled()){
            log.debug("Sending mail to: '" + mailMessage.getRecipient() +"' with subject: '" + mailMessage.getSubject() +"'.");
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        String base64Creds = createBase64Credentials("api", apiKey);
        headers.add("Authorization", "Basic " + base64Creds);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formDataMap = new LinkedMultiValueMap<>();
        formDataMap.add("from", mailMessage.getFrom());
        mailMessage.getRecipient().forEach(recipient -> formDataMap.add("to", recipient));
        formDataMap.add("recipient-variables", prepareRecipientsVariableString(mailMessage.getRecipient()));
        formDataMap.add("subject", mailMessage.getSubject());
        formDataMap.add("text", mailMessage.getText());
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formDataMap, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        if(log.isDebugEnabled()){
            log.debug("Mail send successfully to: '" + mailMessage.getRecipient() +"' with subject: '" + mailMessage.getSubject() + "'.");
            log.debug("Mailgum response: '" + response.getBody() + "'.");
        }
    }

    private String createBase64Credentials(String username, String password)
    {
        String plainCreds = username + ":" + password;
        return new String(Base64.encode(plainCreds.getBytes()));
    }

    private String prepareRecipientsVariableString(List<String> recipients)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        for(int i=0; i<recipients.size();i++)
        {
            String recipient = recipients.get(i);
            sb.append(quote(recipient));
            sb.append(":");
            sb.append("{");
            sb.append(quote("first"));
            sb.append(":");
            sb.append(quote(recipient));
            sb.append(",");
            sb.append(quote("id"));
            sb.append(":");
            sb.append((i+1));
            sb.append("}");
            if(i != recipients.size() -1){
                sb.append(",");
            }
        }

        sb.append("}");

        return sb.toString();
    }

    private String quote(String value)
    {
        return "\"" + value + "\"";
    }

}
