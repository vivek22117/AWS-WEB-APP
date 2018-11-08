package com.doubledigit.boot.restApiexample.web;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.doubledigit.boot.restApiexample.domain.Tour;
import com.doubledigit.boot.restApiexample.domain.TourRating;
import com.doubledigit.boot.restApiexample.domain.TourRatingPK;
import com.doubledigit.boot.restApiexample.repo.TourRatingRepo;
import com.doubledigit.boot.restApiexample.repo.TourRepoPagable;

@RestController
@RequestMapping(path = "/tours/{tourId}/ratings")
public class TourRatingController {

	private TourRepoPagable tourRepo;
	private TourRatingRepo tourRatingRepo;

	@Autowired
	public TourRatingController(TourRepoPagable tourRepo, TourRatingRepo tourRatingRepo) {
		this.tourRepo = tourRepo;
		this.tourRatingRepo = tourRatingRepo;
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public void createTourRating(@PathVariable(value="tourId") int tourId, @RequestBody @Validated RatingDTO ratingDto){
		Tour tour = verifyTour(tourId);
		tourRatingRepo.save(new TourRating(new TourRatingPK(tour, ratingDto.getCustomerId()), 
				ratingDto.getScore(), ratingDto.getComment()));
		
	}

	private Tour verifyTour(int tourId) throws NoSuchElementException {
		Optional<Tour> tour = tourRepo.findById(tourId);

		if (!tour.isPresent()) {
			throw new NoSuchElementException("Tour does not exists: " + tourId);
		}
		return tour.get();
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoSuchElementException.class)
	public String return400(NoSuchElementException ex){
		return ex.getMessage();
	}
}
