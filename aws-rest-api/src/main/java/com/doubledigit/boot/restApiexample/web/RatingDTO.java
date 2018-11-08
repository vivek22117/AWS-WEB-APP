package com.doubledigit.boot.restApiexample.web;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RatingDTO {

	@Min(0)
	@Max(5)
	private Integer score;
	
	@Size(max=255)
	private String comment;
	
	@NotNull
	private Integer customerId;

	public RatingDTO(Integer score, String comment, Integer customerId) {
		super();
		this.score = score;
		this.comment = comment;
		this.customerId = customerId;
	}

	public RatingDTO() {
		super();
	}

	public Integer getScore() {
		return score;
	}

	public String getComment() {
		return comment;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	@Override
	public String toString() {
		return "RatingDTO [score=" + score + ", comment=" + comment + ", customerId=" + customerId + "]";
	}
	
}
