package vp.magisterski.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vp.magisterski.model.magister.MasterThesis;

import java.util.Optional;

public interface MasterThesisService {
    MasterThesis save(String studentIndex, String title, String area,
                      String description, String mentorId, String firstMemberId,
                      String secondMemberId);

    Optional<MasterThesis> findThesisById(Long id);

    Page<MasterThesis> findAll(Pageable pageable);
}
