package org.uengine.bpm.eventconsumer.kafka;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by uengine on 2019. 2. 25..
 */
public interface EventToBeProcessedRepository extends PagingAndSortingRepository<EventToBeProcessed, Long>{
}
