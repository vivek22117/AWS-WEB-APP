package com.doubledigit.boot.restApiexample;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.doubledigit.boot.restApiexample.domain.Difficulty;
import com.doubledigit.boot.restApiexample.domain.Region;
import com.doubledigit.boot.restApiexample.service.TourPackageService;
import com.doubledigit.boot.restApiexample.service.TourService;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class RestApiExampleApplication implements CommandLineRunner{
	
	@Autowired
	private TourPackageService tpService;
	
	@Autowired 
	private TourService tourService;

	public static void main(String[] args) {
		SpringApplication.run(RestApiExampleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		tpService.createTourPackage("BC", "Panchmani Cal");
		tpService.createTourPackage("BYC", "Cycle California");
		tpService.createTourPackage("SC", "Snowboard Cali");
		tpService.createTourPackage("KC", "Kids California");
		tpService.createTourPackage("TC", "Taste of California");
		tpService.createTourPackage("CC", "California Calm");
		tpService.createTourPackage("DC", "From Desert to Sea");
		tpService.createTourPackage("DN", "Nature Watch");
		
		tpService.lookUp().forEach(System.out::println);
		TourFromFile.importTours().forEach(System.out::println);
		TourFromFile.importTours().forEach(t -> tourService.createTour(t.title, t.description, t.blurb, Integer.parseInt(t.price), t.length, 
				t.bullets, t.keywords, t.packageType, Difficulty.valueOf(t.difficulty), Region.findByLabel(t.region)));

		System.out.println(tourService.total());
	}
	
	
	static class TourFromFile{
		private String packageType, title, blurb, description, bullets, difficulty, length, price, region, keywords;
		
		static List<TourFromFile> importTours() throws JsonParseException, JsonMappingException, IOException{
			return new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
					.readValue(TourFromFile.class.getResourceAsStream("/static_tours_data.json"), new TypeReference<List<TourFromFile>>(){});
		}
	}
}
