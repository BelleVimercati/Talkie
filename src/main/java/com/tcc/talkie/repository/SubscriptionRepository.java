package com.tcc.talkie.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.occurrence.Subscription;
import com.tcc.talkie.domain.user.User;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findBySubscriberId(Category category);

    boolean existsBySubscriberIdAndCategoryId(User subscriber, Category category);
}
