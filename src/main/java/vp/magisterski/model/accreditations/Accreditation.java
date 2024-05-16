package vp.magisterski.model.accreditations;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Accreditation {

    @Id
    private String year;

    private LocalDate activeFrom;

    private LocalDate activeTo;

    private Boolean isActive; // Only one accreditation is active in DB

    @ElementCollection
    private List<String> studyProgramFields;



}
