package com.patimer.notifier.service.notification;

public interface NotificationService
{
    void sendActivationCode(String email, String activationCode);
}
