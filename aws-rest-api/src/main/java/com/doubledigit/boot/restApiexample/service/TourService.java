package com.doubledigit.boot.restApiexample.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doubledigit.boot.restApiexample.domain.Difficulty;
import com.doubledigit.boot.restApiexample.domain.Region;
import com.doubledigit.boot.restApiexample.domain.Tour;
import com.doubledigit.boot.restApiexample.domain.TourPackage;
import com.doubledigit.boot.restApiexample.repo.TourPackageRepository;
import com.doubledigit.boot.restApiexample.repo.TourRepoPagable;

@Service
public class TourService {

	private TourPackageRepository tpRepository;
	private TourRepoPagable tRepository;
	
	@Autowired
	public TourService(TourPackageRepository tpRepository, TourRepoPagable tRepository) {
		this.tpRepository = tpRepository;
		this.tRepository = tRepository;
	}

	public Tour createTour(String title, String description, String blurb, Integer price, String duration, String bullets,
			String keywords, String tourPackageName, Difficulty difficulty, Region region){
		
		Optional<TourPackage> tourPackage = tpRepository.findByName(tourPackageName);
		if(!tourPackage.isPresent()){
			throw new RuntimeException("Tour Package does not exist: " + tourPackageName);
		}
		return tRepository.save(new Tour(title, description, blurb, price, duration, bullets, keywords, 
				tourPackage.get(), difficulty, region));
	}
	
	public Iterable<Tour> lookUp(){
		return tRepository.findAll();
	}
	
	public long total(){
		return tRepository.count();
	}
	
}
