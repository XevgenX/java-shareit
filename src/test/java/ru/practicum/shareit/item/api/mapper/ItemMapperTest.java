package ru.practicum.shareit.item.api.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.practicum.shareit.item.api.dto.ItemDto;
import ru.practicum.shareit.item.domain.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.domain.model.User;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapper();
    }

    @Test
    @DisplayName("toModel преобразует ItemDto в Item со всеми полями")
    void toModel_ConvertsItemDtoToItem_WithAllFields() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Аккумуляторная дрель мощная")
                .available(true)
                .build();

        Item item = itemMapper.toModel(dto);

        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("Дрель", item.getName());
        assertEquals("Аккумуляторная дрель мощная", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    @DisplayName("toModel возвращает null при null ItemDto")
    void toModel_ReturnsNull_WhenItemDtoIsNull() {
        Item item = itemMapper.toModel(null);

        assertNull(item);
    }

    @Test
    @DisplayName("toModel корректно обрабатывает ItemDto с null полями")
    void toModel_HandlesItemDto_WithNullFields() {
        ItemDto dto = ItemDto.builder()
                .id(null)
                .name(null)
                .description(null)
                .available(null)
                .build();

        Item item = itemMapper.toModel(dto);

        assertNotNull(item);
        assertNull(item.getId());
        assertNull(item.getName());
        assertNull(item.getDescription());
        assertNull(item.getAvailable());
    }

    @Test
    @DisplayName("toModel преобразует ItemDto с false available")
    void toModel_ConvertsItemDto_WithFalseAvailable() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Описание")
                .available(false)
                .build();

        Item item = itemMapper.toModel(dto);

        assertNotNull(item);
        assertFalse(item.getAvailable());
    }

    @Test
    @DisplayName("toDto преобразует Item в ItemDto со всеми полями")
    void toDto_ConvertsItemToItemDto_WithAllFields() {
        Item item = Item.builder()
                .id(1L)
                .name("Перфоратор")
                .description("Перфоратор для бетона")
                .available(true)
                .build();

        ItemDto dto = itemMapper.toDto(item);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Перфоратор", dto.name());
        assertEquals("Перфоратор для бетона", dto.description());
        assertTrue(dto.available());
    }

    @Test
    @DisplayName("toDto возвращает null при null Item")
    void toDto_ReturnsNull_WhenItemIsNull() {
        ItemDto dto = itemMapper.toDto(null);

        assertNull(dto);
    }

    @Test
    @DisplayName("toDto корректно обрабатывает Item с null полями")
    void toDto_HandlesItem_WithNullFields() {
        Item item = Item.builder()
                .id(null)
                .name(null)
                .description(null)
                .available(null)
                .build();

        ItemDto dto = itemMapper.toDto(item);

        assertNotNull(dto);
        assertNull(dto.id());
        assertNull(dto.name());
        assertNull(dto.description());
        assertNull(dto.available());
    }

    @Test
    @DisplayName("toDto преобразует Item с owner и request (они игнорируются)")
    void toDto_ConvertsItem_IgnoresOwnerAndRequest() {
        User owner = User.builder()
                .id(1L)
                .name("Владелец")
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Запрос")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Описание")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        ItemDto dto = itemMapper.toDto(item);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Дрель", dto.name());
        assertEquals("Описание", dto.description());
        assertTrue(dto.available());
        // Поля owner и request не копируются в DTO
    }

    @Test
    @DisplayName("toModels преобразует список ItemDto в список Item")
    void toModels_ConvertsListOfItemDtos_ToListOfItems() {
        ItemDto dto1 = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Дрель 1")
                .available(true)
                .build();

        ItemDto dto2 = ItemDto.builder()
                .id(2L)
                .name("Перфоратор")
                .description("Перфоратор 2")
                .available(false)
                .build();

        List<ItemDto> dtos = Arrays.asList(dto1, dto2);

        List<Item> items = itemMapper.toModels(dtos);

        assertNotNull(items);
        assertEquals(2, items.size());

        Item item1 = items.get(0);
        assertEquals(1L, item1.getId());
        assertEquals("Дрель", item1.getName());
        assertEquals("Дрель 1", item1.getDescription());
        assertTrue(item1.getAvailable());

        Item item2 = items.get(1);
        assertEquals(2L, item2.getId());
        assertEquals("Перфоратор", item2.getName());
        assertEquals("Перфоратор 2", item2.getDescription());
        assertFalse(item2.getAvailable());
    }

    @Test
    @DisplayName("toModels возвращает пустой список при пустом списке")
    void toModels_ReturnsEmptyList_WhenListIsEmpty() {
        List<ItemDto> emptyList = Collections.emptyList();

        List<Item> items = itemMapper.toModels(emptyList);

        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("toModels корректно обрабатывает список с null элементами")
    void toModels_HandlesList_WithNullElements() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .build();

        List<ItemDto> dtos = Arrays.asList(dto, null);

        List<Item> items = itemMapper.toModels(dtos);

        assertNotNull(items);
        assertEquals(2, items.size());
        assertNotNull(items.get(0));
        assertNull(items.get(1));
    }

    @Test
    @DisplayName("toDtos преобразует список Item в список ItemDto")
    void toDtos_ConvertsListOfItems_ToListOfItemDtos() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Дрель 1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Перфоратор")
                .description("Перфоратор 2")
                .available(false)
                .build();

        List<Item> items = Arrays.asList(item1, item2);

        List<ItemDto> dtos = itemMapper.toDtos(items);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());

        ItemDto dto1 = dtos.get(0);
        assertEquals(1L, dto1.id());
        assertEquals("Дрель", dto1.name());
        assertEquals("Дрель 1", dto1.description());
        assertTrue(dto1.available());

        ItemDto dto2 = dtos.get(1);
        assertEquals(2L, dto2.id());
        assertEquals("Перфоратор", dto2.name());
        assertEquals("Перфоратор 2", dto2.description());
        assertFalse(dto2.available());
    }

    @Test
    @DisplayName("toDtos возвращает пустой список при пустом списке")
    void toDtos_ReturnsEmptyList_WhenListIsEmpty() {
        List<Item> emptyList = Collections.emptyList();

        List<ItemDto> dtos = itemMapper.toDtos(emptyList);

        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("toDtos корректно обрабатывает список с null элементами")
    void toDtos_HandlesList_WithNullElements() {
        Item item = Item.builder()
                .id(1L)
                .name("Дрель")
                .build();

        List<Item> items = Arrays.asList(item, null);

        List<ItemDto> dtos = itemMapper.toDtos(items);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertNotNull(dtos.get(0));
        assertNull(dtos.get(1));
    }

    @Test
    @DisplayName("Двустороннее преобразование сохраняет данные")
    void bidirectionalConversion_PreservesData() {
        // Item -> DTO -> Item
        Item originalItem = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .build();

        ItemDto dto = itemMapper.toDto(originalItem);
        Item convertedItem = itemMapper.toModel(dto);

        assertNotNull(convertedItem);
        assertEquals(originalItem.getId(), convertedItem.getId());
        assertEquals(originalItem.getName(), convertedItem.getName());
        assertEquals(originalItem.getDescription(), convertedItem.getDescription());
        assertEquals(originalItem.getAvailable(), convertedItem.getAvailable());
    }

    @Test
    @DisplayName("Двустороннее преобразование DTO -> Item -> DTO сохраняет данные")
    void bidirectionalConversionDtoToItemToDto_PreservesData() {
        // DTO -> Item -> DTO
        ItemDto originalDto = ItemDto.builder()
                .id(1L)
                .name("Перфоратор")
                .description("Перфоратор для бетона")
                .available(false)
                .build();

        Item item = itemMapper.toModel(originalDto);
        ItemDto convertedDto = itemMapper.toDto(item);

        assertNotNull(convertedDto);
        assertEquals(originalDto.id(), convertedDto.id());
        assertEquals(originalDto.name(), convertedDto.name());
        assertEquals(originalDto.description(), convertedDto.description());
        assertEquals(originalDto.available(), convertedDto.available());
    }

    @Test
    @DisplayName("toModels сохраняет порядок элементов")
    void toModels_PreservesOrderOfElements() {
        ItemDto dto1 = ItemDto.builder().id(1L).name("Первый").build();
        ItemDto dto2 = ItemDto.builder().id(2L).name("Второй").build();
        ItemDto dto3 = ItemDto.builder().id(3L).name("Третий").build();

        List<ItemDto> dtos = Arrays.asList(dto1, dto2, dto3);

        List<Item> items = itemMapper.toModels(dtos);

        assertEquals(3, items.size());
        assertEquals(1L, items.get(0).getId());
        assertEquals(2L, items.get(1).getId());
        assertEquals(3L, items.get(2).getId());
    }

    @Test
    @DisplayName("toDtos сохраняет порядок элементов")
    void toDtos_PreservesOrderOfElements() {
        Item item1 = Item.builder().id(1L).name("Первый").build();
        Item item2 = Item.builder().id(2L).name("Второй").build();
        Item item3 = Item.builder().id(3L).name("Третий").build();

        List<Item> items = Arrays.asList(item1, item2, item3);

        List<ItemDto> dtos = itemMapper.toDtos(items);

        assertEquals(3, dtos.size());
        assertEquals(1L, dtos.get(0).id());
        assertEquals(2L, dtos.get(1).id());
        assertEquals(3L, dtos.get(2).id());
    }

    @Test
    @DisplayName("toModels с большим списком")
    void toModels_WithLargeList() {
        List<ItemDto> dtos = Arrays.asList(
                ItemDto.builder().id(1L).name("Item1").available(true).build(),
                ItemDto.builder().id(2L).name("Item2").available(false).build(),
                ItemDto.builder().id(3L).name("Item3").available(true).build(),
                ItemDto.builder().id(4L).name("Item4").available(false).build(),
                ItemDto.builder().id(5L).name("Item5").available(true).build()
        );

        List<Item> items = itemMapper.toModels(dtos);

        assertEquals(5, items.size());
        for (int i = 0; i < 5; i++) {
            assertEquals((long) i + 1, items.get(i).getId());
            assertEquals("Item" + (i + 1), items.get(i).getName());
            assertEquals(i % 2 == 0, items.get(i).getAvailable());
        }
    }

    @Test
    @DisplayName("toModels и toDtos работают согласованно")
    void toModelsAndToDtos_WorkConsistently() {
        ItemDto dto1 = ItemDto.builder().id(1L).name("A").description("Desc A").available(true).build();
        ItemDto dto2 = ItemDto.builder().id(2L).name("B").description("Desc B").available(false).build();

        List<ItemDto> originalDtos = Arrays.asList(dto1, dto2);

        List<Item> items = itemMapper.toModels(originalDtos);
        List<ItemDto> convertedDtos = itemMapper.toDtos(items);

        assertThat(convertedDtos)
                .usingRecursiveComparison()
                .isEqualTo(originalDtos);
    }

    @Test
    @DisplayName("Преобразование Item с минимальными данными")
    void toDto_WithMinimalItem() {
        Item item = Item.builder()
                .id(1L)
                .name("Минимальный")
                // description не установлен
                // available не установлен
                .build();

        ItemDto dto = itemMapper.toDto(item);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Минимальный", dto.name());
        assertNull(dto.description());
        assertNull(dto.available());
    }

    @Test
    @DisplayName("Преобразование ItemDto с минимальными данными")
    void toModel_WithMinimalItemDto() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Минимальный")
                // description не установлен
                // available не установлен
                .build();

        Item item = itemMapper.toModel(dto);

        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("Минимальный", item.getName());
        assertNull(item.getDescription());
        assertNull(item.getAvailable());
    }

    @Test
    @DisplayName("Item с длинными значениями полей корректно преобразуется")
    void toDto_WithLongFieldValues() {
        String longName = "Очень длинное название инструмента, которое может быть очень длинным";
        String longDescription = "Очень длинное описание инструмента, которое содержит много деталей "
                + "и характеристик, важных для пользователя. Это описание может быть очень "
                + "подробным и занимать несколько строк текста.";

        Item item = Item.builder()
                .id(1L)
                .name(longName)
                .description(longDescription)
                .available(true)
                .build();

        ItemDto dto = itemMapper.toDto(item);

        assertNotNull(dto);
        assertEquals(longName, dto.name());
        assertEquals(longDescription, dto.description());
    }

    @Test
    @DisplayName("Специальные символы в названии и описании сохраняются")
    void toDto_PreservesSpecialCharacters() {
        String nameWithSpecialChars = "Дрель-перфоратор №123 (профессиональная)";
        String descriptionWithSpecialChars = "Инструмент с: спец. символами, кавычками \"и другими\"";

        Item item = Item.builder()
                .id(1L)
                .name(nameWithSpecialChars)
                .description(descriptionWithSpecialChars)
                .available(true)
                .build();

        ItemDto dto = itemMapper.toDto(item);

        assertNotNull(dto);
        assertEquals(nameWithSpecialChars, dto.name());
        assertEquals(descriptionWithSpecialChars, dto.description());
    }

    @Test
    @DisplayName("null значения полей не ломают преобразование")
    void conversion_HandlesNullFieldValues_Gracefully() {
        Item item = Item.builder()
                .id(null)
                .name(null)
                .description(null)
                .available(null)
                .build();

        ItemDto dto = itemMapper.toDto(item);
        Item convertedItem = itemMapper.toModel(dto);

        assertNotNull(convertedItem);
        assertNull(convertedItem.getId());
        assertNull(convertedItem.getName());
        assertNull(convertedItem.getDescription());
        assertNull(convertedItem.getAvailable());
    }
}
