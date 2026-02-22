package ru.yandex.practicum.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.ProductOfWarehouse;

import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<ProductOfWarehouse, UUID> {

}
