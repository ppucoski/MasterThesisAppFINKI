package vp.magisterski.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.magister.MasterThesis;


public interface MasterThesisRepository extends JpaRepository<MasterThesis, Long> {
}
