package co.com.btgpactual.sns;

import co.com.btgpactual.exception.technical.TechnicalException;
import co.com.btgpactual.model.notification.Notification;
import co.com.btgpactual.model.notification.gateways.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;

import java.util.Objects;

import static co.com.btgpactual.exception.technical.TechnicalErrorType.SNS_CONNECTION_ERROR;
@Slf4j
@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {
    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String snsTopicArn;

    @Override
    public void sendSmsNotification(String clientPhoneNumber, Notification notification) {
        if(Objects.nonNull(clientPhoneNumber)){
            try {
                PublishRequest request = PublishRequest.builder()
                        .message(notification.getMessage())
                        .phoneNumber(clientPhoneNumber)
                        .build();

                var response = snsClient.publish(request);
                log.info("Sms notification sent with message id: {}", response.messageId());

            } catch (SnsException e) {
                throw new TechnicalException(e, SNS_CONNECTION_ERROR);
            }
        }
    }

    @Override
    public void sendEmailNotification(String clientEmail, Notification notification) {
        PublishRequest publishRequest = PublishRequest.builder()
                .message(notification.getMessage())
                .subject(notification.getSubject())
                .topicArn(snsTopicArn)
                .build();

        snsClient.publish(publishRequest);
    }
}
