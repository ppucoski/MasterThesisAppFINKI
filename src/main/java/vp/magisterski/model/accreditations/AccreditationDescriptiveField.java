package vp.magisterski.model.accreditations;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccreditationDescriptiveField {

    private String name;

    private String category;

    @Column(length = 5000)
    private String value;
}

