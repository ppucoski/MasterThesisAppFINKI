package vp.magisterski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vp.magisterski.model.magister.MasterThesis;

public interface MasterThesisRepository extends JpaRepository<MasterThesis, Long> {
    //TODO: func
}
