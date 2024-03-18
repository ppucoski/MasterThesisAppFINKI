package vp.magisterski.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisDocument;
import vp.magisterski.model.magister.MasterThesisDocumentType;
import vp.magisterski.model.magister.MasterThesisStatus;
import vp.magisterski.repository.MasterThesisDocumentRepository;
import vp.magisterski.service.MasterThesisDocumentService;

import java.io.IOException;
import java.time.LocalDate;
@Service
public class MasterThesisDocumentServiceImpl implements MasterThesisDocumentService {
    private final MasterThesisDocumentRepository masterThesisDocumentRepository;

    public MasterThesisDocumentServiceImpl(MasterThesisDocumentRepository masterThesisDocumentRepository) {
        this.masterThesisDocumentRepository = masterThesisDocumentRepository;
    }

    @Override
    public void saveFile(MasterThesis thesis, MasterThesisDocumentType type, MultipartFile file) throws IOException {
        MasterThesisDocument masterThesisDocument = new MasterThesisDocument(thesis, type, file.getBytes(), LocalDate.now());
        this.masterThesisDocumentRepository.save(masterThesisDocument);
    }
}
