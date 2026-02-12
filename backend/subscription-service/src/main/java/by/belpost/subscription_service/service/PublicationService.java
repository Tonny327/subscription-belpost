package by.belpost.subscription_service.service;

import by.belpost.subscription_service.dto.PublicationDto;
import by.belpost.subscription_service.entity.Category;
import by.belpost.subscription_service.entity.Publication;
import by.belpost.subscription_service.enums.PublicationType;
import by.belpost.subscription_service.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;

    public List<PublicationDto> getAllPublications(String type, Long categoryId) {
        List<Publication> publications;

        boolean hasType = type != null && !type.isBlank();
        boolean hasCategory = categoryId != null;

        if (!hasType && !hasCategory) {
            publications = publicationRepository.findAll();
        } else if (hasType && !hasCategory) {
            PublicationType publicationType = parseType(type);
            publications = publicationRepository.findByType(publicationType);
        } else if (!hasType) {
            publications = publicationRepository.findByCategoriesId(categoryId);
        } else {
            PublicationType publicationType = parseType(type);
            publications = publicationRepository.findByTypeAndCategoriesId(publicationType, categoryId);
        }

        return publications.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PublicationType parseType(String type) {
        try {
            return PublicationType.valueOf(type.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid publication type: " + type);
        }
    }

    public PublicationDto toDto(Publication publication) {
        Set<String> categoryNames = publication.getCategories()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toSet());

        return PublicationDto.builder()
                .id(publication.getId())
                .title(publication.getTitle())
                .description(publication.getDescription())
                .price(publication.getPrice())
                .period(publication.getPeriod())
                .type(publication.getType() != null ? publication.getType().name() : null)
                .imageUrl(publication.getImageUrl())
                .categoryNames(categoryNames)
                .build();
    }
}

