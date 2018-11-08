package com.doubledigit.boot.restApiexample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doubledigit.boot.restApiexample.domain.TourPackage;
import com.doubledigit.boot.restApiexample.repo.TourPackageRepository;

@Service
public class TourPackageService {

	private TourPackageRepository tpRepository;

	@Autowired
	public TourPackageService(TourPackageRepository tpRepository) {
		this.tpRepository = tpRepository;
	}
	
	public TourPackage createTourPackage(String code, String name){
		if(!tpRepository.existsById(code)){
			tpRepository.save(new TourPackage(code, name));
		}
		return null;
	}
	
	public Iterable<TourPackage> lookUp(){
		return tpRepository.findAll();
	}
	
	public long total(){
		return tpRepository.count();
	}
	
}
