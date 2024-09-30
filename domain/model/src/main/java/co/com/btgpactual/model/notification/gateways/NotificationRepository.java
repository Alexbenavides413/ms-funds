package co.com.btgpactual.model.notification.gateways;

import co.com.btgpactual.model.notification.Notification;

public interface NotificationRepository {
    void sendSmsNotification(String clientPhoneNumber, Notification notification);
    void sendEmailNotification(String clientEmail, Notification notification);
}
