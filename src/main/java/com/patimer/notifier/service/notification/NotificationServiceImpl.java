package com.patimer.notifier.service.notification;

import com.patimer.notifier.service.mail.MailMessage;
import com.patimer.notifier.service.mail.MailSender;
import org.apache.commons.lang.Validate;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.util.Collections;

@Component
public class NotificationServiceImpl implements NotificationService
{
    private VelocityEngine velocityEngine;
    private MailSender mailSender;

    @Value("${mail.from.address}")
    private String fromAddress;

    @Value("${mail.subject.activation.code.mail}")
    private String activationCodeSubjectTemplate;

    @Autowired
    public NotificationServiceImpl(MailSender mailSender)
    {
        Validate.notNull(mailSender);
        this.mailSender = mailSender;

        this.velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();
    }

    @PostConstruct
    public void validate()
    {
        Validate.notEmpty(fromAddress);
        Validate.notEmpty(activationCodeSubjectTemplate);
    }

    protected void setFromAddress(String fromAddress)
    {
        Validate.notEmpty(fromAddress);
        this.fromAddress = fromAddress;
    }

    protected void setActivationCodeSubjectTemplate(String activationCodeSubjectTemplate)
    {
        Validate.notEmpty(activationCodeSubjectTemplate);
        this.activationCodeSubjectTemplate = activationCodeSubjectTemplate;
    }

    @Override
    public void sendActivationCode(String email, String activationCode)
    {
        Validate.notEmpty(email);
        Validate.notEmpty(activationCode);

        Template template = velocityEngine.getTemplate("templates/mail/activationCodeTemplate.vm");
        VelocityContext context = new VelocityContext();
        context.put("activationCode", activationCode);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        MailMessage mailMessage =
            new MailMessage(
                fromAddress,
                Collections.singletonList(email),
                activationCodeSubjectTemplate,
                writer.toString()
            );

        mailSender.send(mailMessage);
    }
}
