package by.belpost.subscription_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWithSubscriptionsDto {

    private UserDto user;
    private List<SubscriptionResponseDto> subscriptions;
}

