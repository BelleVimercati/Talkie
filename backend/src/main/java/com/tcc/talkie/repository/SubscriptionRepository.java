package com.tcc.talkie.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.occurrence.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findBySubscriberId(UUID subscriberId);

    List<Subscription> findByCategoryId(Long categoryId);

    boolean existsBySubscriberIdAndCategoryId(UUID subscriberId, Long categoryId);

    Optional<Subscription> findBySubscriberIdAndCategoryId(UUID subscriberId, Long categoryId);

    @Modifying
    @Query("delete from Subscription s where s.subscriber.id = :userId")
    int deleteBySubscriberId(@Param("userId") UUID userId);
}
