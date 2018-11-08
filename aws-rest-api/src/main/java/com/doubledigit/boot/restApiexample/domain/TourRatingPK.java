package com.doubledigit.boot.restApiexample.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class TourRatingPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne
	private Tour tour;
	
	@Column(insertable=false,  updatable=false, nullable=false)
	private Integer customerId;

	public TourRatingPK() {}

	public TourRatingPK(Tour tour, Integer customerId) {
		this.tour = tour;
		this.customerId = customerId;
	}

	public Tour getTour() {
		return tour;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	@Override
	public String toString() {
		return "TourRatingPK [tour=" + tour + ", customerId=" + customerId + "]";
	}

}