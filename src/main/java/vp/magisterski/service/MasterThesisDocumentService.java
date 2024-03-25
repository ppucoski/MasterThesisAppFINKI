package vp.magisterski.service;

import org.springframework.web.multipart.MultipartFile;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisDocument;
import vp.magisterski.model.enumerations.MasterThesisDocumentType;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MasterThesisDocumentService {
    void saveFile(MasterThesis thesis, MasterThesisDocumentType type, MultipartFile file) throws IOException;
    Optional<MasterThesisDocument> findById(Long id);
    List<MasterThesisDocument> findAllByThesis(MasterThesis masterThesis);
}
