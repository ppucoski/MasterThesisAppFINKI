package vp.magisterski.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vp.magisterski.model.magister.MasterThesis;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MasterThesisService {
    MasterThesis save(String studentIndex, LocalDateTime dateTime, String title, String area,
                      String description, String mentorId, String firstMemberId,
                      String secondMemberId);

    Optional<MasterThesis> findThesisById(Long id);

    Page<MasterThesis> findAll(Pageable pageable);

    List<MasterThesis> filterMasterThesis(MasterThesis masterThesis);
}
