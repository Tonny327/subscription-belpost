package by.belpost.subscription_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private Long id;
    private PublicationDto publication;
    private String period;
    private Integer quantity;
    private Double totalPrice;
}

