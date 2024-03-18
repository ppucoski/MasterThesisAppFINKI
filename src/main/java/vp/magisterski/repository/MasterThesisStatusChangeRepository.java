package vp.magisterski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisStatusChange;

import java.util.List;

public interface MasterThesisStatusChangeRepository extends JpaRepository<MasterThesisStatusChange, Long> {
    List<MasterThesisStatusChange> findAllByThesis(MasterThesis thesis);
}
