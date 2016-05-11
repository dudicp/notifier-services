package com.patimer.notifier.dto;

import com.patimer.notifier.model.NotificationType;
import org.apache.commons.lang.Validate;

public enum NotificationTypeDto
{
    Email,
    SMS;

    public static NotificationTypeDto fromNotificationType(NotificationType notificationType)
    {
        Validate.notNull(notificationType);

        switch (notificationType)
        {
            case Email: return Email;
            case SMS: return SMS;
            default: throw new IllegalArgumentException("Unknown notification type: '" + notificationType + "'");
        }
    }

    public NotificationType toNotificationType()
    {
        switch (this)
        {
            case Email: return NotificationType.Email;
            case SMS: return NotificationType.SMS;
            default: throw new IllegalArgumentException("Unsupported notification type: '" + this + "'");
        }
    }
}
