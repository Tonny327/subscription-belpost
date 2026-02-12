package by.belpost.subscription_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationDto {

    private Long id;
    private String title;
    private String description;
    private Double price;
    private String period;
    private String type;
    private String imageUrl;
    private Set<String> categoryNames;
}

