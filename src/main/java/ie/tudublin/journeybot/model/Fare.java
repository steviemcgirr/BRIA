package ie.tudublin.journeybot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import ie.tudublin.journeybot.model.JourneyFare.JourneyFareBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fare")
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Fare {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "adult_single")
	private Float adultSingle;
	
	@Column(name = "adult_day_return")
	private Float adultDayReturn;
	
	@Column(name = "adult_open_return")
	private Float adultOpenReturn;
	
	@Column(name = "student_single")
	private Float studentSingle;
	
	@Column(name = "student_return")
	private Float studenReturn;
	

}
