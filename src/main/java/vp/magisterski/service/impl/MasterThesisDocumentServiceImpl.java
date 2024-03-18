package vp.magisterski.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisDocument;
import vp.magisterski.model.magister.MasterThesisDocumentType;
import vp.magisterski.repository.MasterThesisDocumentRepository;
import vp.magisterski.service.MasterThesisDocumentService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MasterThesisDocumentServiceImpl implements MasterThesisDocumentService {
    private final MasterThesisDocumentRepository masterThesisDocumentRepository;

    public MasterThesisDocumentServiceImpl(MasterThesisDocumentRepository masterThesisDocumentRepository) {
        this.masterThesisDocumentRepository = masterThesisDocumentRepository;
    }

    @Override
    @Transactional
    public void saveFile(MasterThesis thesis, MasterThesisDocumentType type, MultipartFile file) throws IOException {
        MasterThesisDocument masterThesisDocument = new MasterThesisDocument(thesis, type, file.getBytes(), LocalDate.now());
        this.masterThesisDocumentRepository.save(masterThesisDocument);
    }

    @Override
    @Transactional
    public Optional<MasterThesisDocument> findById(Long id) {
        return this.masterThesisDocumentRepository.findById(id);
    }

    @Override
    @Transactional
    public List<MasterThesisDocument> findAllByThesis(MasterThesis masterThesis) {
        return this.masterThesisDocumentRepository.findAllByThesis(masterThesis);
    }
}
