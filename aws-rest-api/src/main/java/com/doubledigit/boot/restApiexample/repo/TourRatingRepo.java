package com.doubledigit.boot.restApiexample.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.doubledigit.boot.restApiexample.domain.TourRating;
import com.doubledigit.boot.restApiexample.domain.TourRatingPK;

@RepositoryRestResource(exported=false)
public interface TourRatingRepo extends CrudRepository<TourRating, TourRatingPK> {
	
	List<TourRating> findByPkTourId(Integer tourId);
	
	TourRating findByPkTourIdAndPkCustomerId(Integer tourId, Integer CustomerId);
}
