package ru.yandex.practicum.DTO.warehouse;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {

    @Min(value = 1, message = "Глубина должна быть минимум 1")
    private Double depth;

    @Min(value = 1, message = "Ширина должна быть минимум 1")
    private Double width;

    @Min(value = 1, message = "Высота должна быть минимум 1")
    private Double height;
}
