package org.uengine.bpm.eventconsumer.kafka;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by uengine on 2019. 2. 21..
 */
public interface SubscriptionRepository extends PagingAndSortingRepository<Subscription, Long>{
}
