package vp.magisterski.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisStatus;

import java.util.List;

public interface MasterThesisRepository extends JpaRepository<MasterThesis, Long> {
    List<MasterThesis> findAll(Specification<MasterThesis> specification);

    Page<MasterThesis> findAll(Specification<MasterThesis> specification, Pageable pageable);
}
