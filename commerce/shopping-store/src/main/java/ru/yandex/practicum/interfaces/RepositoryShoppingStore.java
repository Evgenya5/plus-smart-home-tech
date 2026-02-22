package ru.yandex.practicum.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.enums.shoppingStore.ProductCategory;
import ru.yandex.practicum.model.Product;

import java.util.UUID;

public interface RepositoryShoppingStore extends JpaRepository<Product, UUID> {

    Page<Product> findAllByProductCategory(ProductCategory category, Pageable pageable);

}
