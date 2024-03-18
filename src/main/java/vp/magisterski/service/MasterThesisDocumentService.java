package vp.magisterski.service;

import org.springframework.web.multipart.MultipartFile;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisDocumentType;

import java.io.IOException;

public interface MasterThesisDocumentService {
    void saveFile(MasterThesis thesis, MasterThesisDocumentType type, MultipartFile file) throws IOException;
}
