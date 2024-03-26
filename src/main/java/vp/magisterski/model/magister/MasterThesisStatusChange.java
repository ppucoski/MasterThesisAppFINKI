package vp.magisterski.model.magister;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.model.shared.User;
import java.time.LocalDateTime;
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
    private MasterThesis thesis;

    private LocalDateTime statusChangeDate;

    @Enumerated(EnumType.STRING)
    private MasterThesisStatus nextStatus;

    @ManyToOne
    private User statusChangedBy;

    @Column(length = 5_000)
    private String note;

    private Boolean approved;

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

    public MasterThesisStatusChange(MasterThesis thesis, LocalDateTime statusChangeDate, MasterThesisStatus nextStatus, User statusChangedBy, String note) {
        this.thesis = thesis;
        this.statusChangeDate = statusChangeDate;
        this.nextStatus = nextStatus;
        this.statusChangedBy = statusChangedBy;
        this.note = note;
    }

    public MasterThesisStatusChange(MasterThesis thesis, LocalDateTime statusChangeDate, MasterThesisStatus nextStatus) {
        this.thesis = thesis;
        this.statusChangeDate = statusChangeDate;
        this.nextStatus = nextStatus;
    }

    public MasterThesisStatusChange(MasterThesis thesis, MasterThesisStatus nextStatus) {
        this.thesis = thesis;
        this.nextStatus = nextStatus;
    }
}
