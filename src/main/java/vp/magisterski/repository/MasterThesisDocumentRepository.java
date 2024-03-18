package vp.magisterski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.magister.MasterThesisDocument;

public interface MasterThesisDocumentRepository  extends JpaRepository<MasterThesisDocument, Long> {
}
