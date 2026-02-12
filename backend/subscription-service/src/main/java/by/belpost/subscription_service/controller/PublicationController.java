package by.belpost.subscription_service.controller;

import by.belpost.subscription_service.dto.PublicationDto;
import by.belpost.subscription_service.service.PublicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;

    @GetMapping
    public List<PublicationDto> getPublications(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId
    ) {
        return publicationService.getAllPublications(type, categoryId);
    }
}

