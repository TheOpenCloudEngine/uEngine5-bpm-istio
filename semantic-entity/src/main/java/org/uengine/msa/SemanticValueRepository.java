package org.uengine.msa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by uengine on 2018. 1. 11..
 */
public interface SemanticValueRepository extends PagingAndSortingRepository<SemanticValue, Long>{

    List<SemanticValue> findBySynonym(@Param("synonym") String synonym);
}
