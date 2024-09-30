package co.com.btgpactual.model.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Notification {
    private String message;
    private String subject;
}
