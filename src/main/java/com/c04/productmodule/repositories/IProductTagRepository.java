package com.c04.productmodule.repositories;

import com.c04.productmodule.models.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductTagRepository extends JpaRepository<ProductTag, Long> {
}
