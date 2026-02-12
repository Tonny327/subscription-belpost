package by.belpost.subscription_service.dto;

import by.belpost.subscription_service.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponseDto {

    private Long id;
    private PublicationDto publication;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
    private String period;
    private Double totalPrice;
}

