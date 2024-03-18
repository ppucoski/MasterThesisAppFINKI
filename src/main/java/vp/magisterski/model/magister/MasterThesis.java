package vp.magisterski.model.magister;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MasterThesis {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private MasterThesisStatus status;

    @ManyToOne
    private Student student;

    private String title;

    private String area; //Oblast

    @Column(length = 5000)
    private String description;

    // isProfessor == true and not elected
    @ManyToOne
    private Professor mentor;

    // isProfessor == true
    @ManyToOne
    private Professor firstMember;

    // any professor
    @ManyToOne
    private Professor secondMember;

    @Lob
    private byte[] thesisText;

    @Embedded
    private MasterThesisPresentation presentation;

    // 5 - 10
    private Integer grade;

    public MasterThesis(MasterThesisStatus status, MasterThesisPresentation presentation, Student student, String title, String area, String description, Professor mentor, Professor firstMember, Professor secondMember) {
        this.status = status;
        this.presentation = presentation;
        this.student = student;
        this.title = title;
        this.area = area;
        this.description = description;
        this.mentor = mentor;
        this.firstMember = firstMember;
        this.secondMember = secondMember;
    }

    public MasterThesis(MasterThesisStatus status, Student student, String title, Professor mentor) {
        this.status = status;
        this.student = student;
        this.title = title;
        this.mentor = mentor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MasterThesis that = (MasterThesis) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
