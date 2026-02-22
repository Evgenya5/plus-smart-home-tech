package ru.yandex.practicum.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dimension {
    private Double width;
    private Double height;
    private Double depth;

    public Double getVolume() {
        return width * height * depth;
    }
}
