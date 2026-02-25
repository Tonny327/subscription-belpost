package by.belpost.subscription_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemRequest {

    @NotNull
    private Long publicationId;

    @NotBlank
    private String period;

    @NotNull
    @Min(1)
    private Integer quantity;

    private Long cartId;
    private String cartToken;
    private Long userId;
}

