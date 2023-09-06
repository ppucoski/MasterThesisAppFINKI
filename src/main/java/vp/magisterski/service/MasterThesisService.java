package vp.magisterski.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vp.magisterski.model.magister.MasterThesis;

import java.util.Optional;

public interface MasterThesisService {
    MasterThesis save(String student, String title, String area,
                      String description, String mentor, String firstMember,
                      String secondMember);

    Optional<MasterThesis> findThesisById(Long id);

    Page<MasterThesis> findAll(Pageable pageable);
}
