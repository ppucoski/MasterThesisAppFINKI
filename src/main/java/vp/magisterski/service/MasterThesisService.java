package vp.magisterski.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    Specification<MasterThesis> filterMasterThesis(MasterThesis masterThesis);
    //Page<MasterThesis> findAll(List<MasterThesis> masterThesisList, Pageable pageable);
    Page<MasterThesis> findAll(Specification<MasterThesis> specification, Pageable pageable);
    List<MasterThesis> findAll();
}
