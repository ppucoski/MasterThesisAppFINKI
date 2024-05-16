package vp.magisterski.model.accreditations;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.StudyProgram;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StudyProgramDetails {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "id", insertable = false, updatable = false)
    private StudyProgram studyProgram;

    private String nameEn;

    private Float order;

    private Short durationYears;

    private Short durationSemesters;

    @Column(length = 8000)
    private String generalInformation;

    @Column(length = 3000)
    private String graduationTitle;

    @Column(length = 3000)
    private String graduationTitleEn;

    @Column(length = 8000)
    private String subjectRestrictions;

    private Boolean onEnglish;

    @Enumerated(EnumType.STRING)
    private StudyCycle studyCycle;

    @ManyToOne
    private Accreditation accreditation;

    private Boolean bilingual;

    @ElementCollection
    private List<AccreditationDescriptiveField> fields;

    @ManyToOne
    private Professor coordinator;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudyProgramDetails professor = (StudyProgramDetails) o;
        return getId() != null && Objects.equals(getId(), professor.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
