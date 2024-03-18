package vp.magisterski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisDocument;

import java.util.List;
import java.util.Optional;

public interface MasterThesisDocumentRepository  extends JpaRepository<MasterThesisDocument, Long> {
    Optional<MasterThesisDocument> findById(Long id);
    List<MasterThesisDocument> findAllByThesis(MasterThesis masterThesis);
}
