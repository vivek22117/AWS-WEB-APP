package com.doubledigit.boot.restApiexample.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.doubledigit.boot.restApiexample.domain.Tour;

public interface TourRepoPagable extends PagingAndSortingRepository<Tour, Integer>{

	Page<Tour> findByTourPackageCode(@Param("code") String code, Pageable pagable);

	@Override
	@RestResource(exported=false)
	default <S extends Tour> Iterable<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@RestResource(exported=false)
	default void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RestResource(exported=false)
	default void delete(Tour entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RestResource(exported=false)
	default void deleteAll(Iterable<? extends Tour> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RestResource(exported=false)
	default void deleteAll() {
		// TODO Auto-generated method stub
		
	}
}
