package com.youngchanteam.viewEats.repository;

import com.youngchanteam.viewEats.domain.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("""
            SELECT r FROM Restaurant r
            WHERE r.latitude BETWEEN :swLat AND :neLat
            AND r.longitude BETWEEN :swLng AND :neLng
            """)
    List<Restaurant> findByBounds(@Param("swLat") Double swLat, @Param("neLat") Double neLat,
                                   @Param("swLng") Double swLng, @Param("neLng") Double neLng);

    List<Restaurant> findByCategory_Id(Long categoryId);

    Optional<Restaurant> findByNameAndAddress(String name, String address);

    @Query("""
            SELECT r FROM Restaurant r
            WHERE r.name LIKE %:keyword%
               OR r.category.name LIKE %:keyword%
            """)
    List<Restaurant> searchByKeyword(@Param("keyword") String keyword);
}