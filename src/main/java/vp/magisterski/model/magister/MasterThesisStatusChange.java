package vp.magisterski.model.magister;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import vp.magisterski.model.shared.User;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MasterThesisStatusChange {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private MasterThesis diplomaThesis;

    private LocalDate statusChangeDate;

    @Enumerated(EnumType.STRING)
    private MasterThesisStatus nextStatus;

    @ManyToOne
    private User statusChangedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MasterThesisStatusChange that = (MasterThesisStatusChange) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
