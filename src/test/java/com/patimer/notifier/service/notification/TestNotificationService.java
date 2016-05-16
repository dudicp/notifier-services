package com.patimer.notifier.service.notification;

import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.AccountEntityBuilder;
import com.patimer.notifier.service.mail.MailMessage;
import com.patimer.notifier.service.mail.MailSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class TestNotificationService
{
    private static final String TEST_FROM_ADDRESS = "noreply@test.com";
    private static final String TEST_ACTIVATION_CODE_SUBJECT = "Activate your account";

    private NotificationServiceImpl notificationService;

    @Mock
    private MailSender mailSenderMock;

    @Before
    public void setUp()
    {
        this.notificationService = new NotificationServiceImpl(mailSenderMock);
        this.notificationService.setFromAddress(TEST_FROM_ADDRESS);
        this.notificationService.setActivationCodeSubjectTemplate(TEST_ACTIVATION_CODE_SUBJECT);
    }

    @Test
    public void testValidate()
    {
        // given

        // when
        notificationService.validate();

        // then
    }

    @Test
    public void testSendActivationCode()
    {
        // given
        String email = "user@test.com";
        AccountEntity accountEntity = new AccountEntityBuilder().withMail(email).build();
        String activationCode = UUID.randomUUID().toString();
        ArgumentCaptor<MailMessage> mailMessageArgumentCapture = ArgumentCaptor.forClass(MailMessage.class);

        // when
        notificationService.sendActivationCode(accountEntity, activationCode);
        Mockito.verify(mailSenderMock).send(mailMessageArgumentCapture.capture());

        // then
        MailMessage mailMessage = mailMessageArgumentCapture.getValue();
        Assert.notNull(mailMessage.getText());
        Assert.isTrue(TEST_FROM_ADDRESS.equals(mailMessage.getFrom()));
        Assert.isTrue(TEST_ACTIVATION_CODE_SUBJECT.equals(mailMessage.getSubject()));
        Assert.isTrue(mailMessage.getRecipient().contains(email));
        Assert.isTrue(mailMessage.getText().contains(activationCode));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendActivationCodeWithNullEmail()
    {
        // given
        String activationCode = UUID.randomUUID().toString();

        // when
        notificationService.sendActivationCode(null, activationCode);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendActivationCodeWithNullActivationCode()
    {
        // given
        String email = "user@test.com";
        AccountEntity accountEntity = new AccountEntityBuilder().withMail(email).build();

        // when
        notificationService.sendActivationCode(accountEntity, null);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendActivationCodeWithEmptyActivationCode()
    {
        // given
        String email = "user@test.com";
        AccountEntity accountEntity = new AccountEntityBuilder().withMail(email).build();

        // when
        notificationService.sendActivationCode(accountEntity, "");

        // then - expected exception
    }


}
