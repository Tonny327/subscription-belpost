package by.belpost.subscription_service.init;

import by.belpost.subscription_service.entity.Category;
import by.belpost.subscription_service.entity.Publication;
import by.belpost.subscription_service.enums.PublicationType;
import by.belpost.subscription_service.repository.CategoryRepository;
import by.belpost.subscription_service.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final PublicationRepository publicationRepository;

    @Override
    public void run(String... args) {
        if (!categoryRepository.findAll().isEmpty() || !publicationRepository.findAll().isEmpty()) {
            return;
        }

        // Root categories
        Category adult = Category.builder().name("Взрослому").build();
        Category child = Category.builder().name("Ребенку").build();

        categoryRepository.saveAll(List.of(adult, child));

        // Adult subcategories
        Category hobby = buildChild("Хобби (вязание, бисероплетение, шитье)", adult);
        Category science = buildChild("Наука", adult);
        Category cooking = buildChild("Кулинария", adult);
        Category crosswords = buildChild("Сканворды", adult);
        Category world = buildChild("Планета и мир", adult);
        Category fishing = buildChild("Рыбалка/охота", adult);

        // Child subcategories
        Category logic = buildChild("Логика", child);
        Category counting = buildChild("Счёт", child);
        Category beauty = buildChild("Макияж/красота", child);
        Category coloring = buildChild("Раскраски+наклейки", child);

        categoryRepository.saveAll(List.of(
                hobby, science, cooking, crosswords, world, fishing,
                logic, counting, beauty, coloring
        ));

        // Publications
        Publication p1 = Publication.builder()
                .title("Мир науки")
                .description("Популярный научный журнал для взрослых.")
                .price(10.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(science))
                .build();

        Publication p2 = Publication.builder()
                .title("Кулинарные шедевры")
                .description("Рецепты на каждый день и праздники.")
                .price(8.5)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(cooking))
                .build();

        Publication p3 = Publication.builder()
                .title("Сканворды XL")
                .description("Большая подборка сканвордов и кроссвордов.")
                .price(5.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(crosswords))
                .build();

        Publication p4 = Publication.builder()
                .title("Планета Земля")
                .description("Журнал о природе и путешествиях.")
                .price(9.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(world))
                .build();

        Publication p5 = Publication.builder()
                .title("Рыболов-охотник")
                .description("Всё о рыбалке и охоте.")
                .price(7.5)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(fishing))
                .build();

        Publication p6 = Publication.builder()
                .title("Умная логика")
                .description("Логические задачи для детей.")
                .price(4.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(logic))
                .build();

        Publication p7 = Publication.builder()
                .title("Учимся считать")
                .description("Развивающий журнал по математике для детей.")
                .price(4.5)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(counting))
                .build();

        Publication p8 = Publication.builder()
                .title("Модный макияж")
                .description("Советы по макияжу и красоте.")
                .price(6.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(beauty))
                .build();

        Publication p9 = Publication.builder()
                .title("Раскраски и наклейки")
                .description("Журнал с раскрасками и наклейками для детей.")
                .price(3.5)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(coloring))
                .build();

        Publication p10 = Publication.builder()
                .title("Утренние новости")
                .description("Ежедневная газета с главными новостями.")
                .price(2.0)
                .period("1 месяц")
                .type(PublicationType.NEWSPAPER)
                .categories(Set.of(adult))
                .build();

        Publication p11 = Publication.builder()
                .title("Вечерний обзор")
                .description("Ежедневная аналитическая газета.")
                .price(2.5)
                .period("1 месяц")
                .type(PublicationType.NEWSPAPER)
                .categories(Set.of(adult))
                .build();

        // Дополнительные издания в стиле онлайн‑подписки Белпочты
        Publication p12 = Publication.builder()
                .title("Каталог Белпочты")
                .description("Подборка лучших периодических изданий для всей семьи.")
                .price(6.9)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(adult))
                .build();

        Publication p13 = Publication.builder()
                .title("Домашний очаг")
                .description("Советы по дому, даче и уютной жизни.")
                .price(7.2)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(cooking, hobby))
                .build();

        Publication p14 = Publication.builder()
                .title("Белорусская панорама")
                .description("Еженедельное издание о событиях в Беларуси и мире.")
                .price(3.4)
                .period("1 месяц")
                .type(PublicationType.NEWSPAPER)
                .categories(Set.of(world, adult))
                .build();

        Publication p15 = Publication.builder()
                .title("Сельская газета")
                .description("Новости регионов, советы садоводам и фермерские истории.")
                .price(2.8)
                .period("1 месяц")
                .type(PublicationType.NEWSPAPER)
                .categories(Set.of(world, hobby))
                .build();

        Publication p16 = Publication.builder()
                .title("Здоровье и жизнь")
                .description("Журнал о здоровье, спорте и активном отдыхе.")
                .price(6.1)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(adult))
                .build();

        Publication p17 = Publication.builder()
                .title("Легенды и сказки")
                .description("Сказочные истории и комиксы для детей.")
                .price(4.3)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(child, coloring))
                .build();

        Publication p18 = Publication.builder()
                .title("Юный исследователь")
                .description("Познаём мир через эксперименты и факты.")
                .price(4.9)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(child, science, logic))
                .build();

        Publication p19 = Publication.builder()
                .title("Телепрограмма недели")
                .description("Телепрограмма и анонсы лучших передач.")
                .price(2.1)
                .period("1 месяц")
                .type(PublicationType.NEWSPAPER)
                .categories(Set.of(adult))
                .build();

        Publication p20 = Publication.builder()
                .title("Финансовый советник")
                .description("Личные финансы, сбережения и инвестиции простым языком.")
                .price(7.7)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(adult))
                .build();

        Publication p21 = Publication.builder()
                .title("IT-мир")
                .description("Новости технологий, гаджетов и цифровых сервисов.")
                .price(8.9)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .categories(Set.of(science, logic))
                .build();

        publicationRepository.saveAll(List.of(
                p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11,
                p12, p13, p14, p15, p16, p17, p18, p19, p20, p21
        ));
    }

    private Category buildChild(String name, Category parent) {
        Category child = Category.builder()
                .name(name)
                .parent(parent)
                .build();
        parent.getChildren().add(child);
        return child;
    }
}

