package vp.magisterski.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vp.magisterski.model.magister.MasterThesis;

import java.util.List;

public interface MasterThesisRepository extends JpaRepository<MasterThesis, Long> {
    List<MasterThesis> findAll(Specification<MasterThesis> specification);
}
