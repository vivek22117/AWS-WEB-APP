package com.doubledigit.boot.restApiexample.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class TourRating {

	@EmbeddedId
	private TourRatingPK pk;
	
	@Column(nullable=false)
	private Integer score;
	
	@Column
	private String comment;

	public TourRating(TourRatingPK pk, Integer score, String comment) {
		this.pk = pk;
		this.score = score;
		this.comment = comment;
	}

	public TourRating() {
		super();
	}

	public TourRatingPK getPk() {
		return pk;
	}

	public Integer getScore() {
		return score;
	}

	public String getComment() {
		return comment;
	}

	@Override
	public String toString() {
		return "TourRating [pk=" + pk + ", score=" + score + ", comment=" + comment + "]";
	}
	
	
	
	
	
}
