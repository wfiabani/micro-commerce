package com.commerce.repository;

import com.commerce.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ImageRepository extends JpaRepository<Image, Long>, PagingAndSortingRepository<Image, Long> {

}
