package vp.magisterski.service.impl;

import org.springframework.stereotype.Service;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.model.magister.MasterThesisStatusChange;
import vp.magisterski.model.shared.User;
import vp.magisterski.repository.MasterThesisStatusChangeRepository;
import vp.magisterski.service.MasterThesisService;
import vp.magisterski.service.MasterThesisStatusChangeService;
import vp.magisterski.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MasterThesisStatusChangeServiceImpl implements MasterThesisStatusChangeService {
    private final MasterThesisStatusChangeRepository masterThesisStatusChangeRepository;

    public MasterThesisStatusChangeServiceImpl(MasterThesisStatusChangeRepository masterThesisStatusChangeRepository, MasterThesisService masterThesisService, UserService userService) {
        this.masterThesisStatusChangeRepository = masterThesisStatusChangeRepository;
    }

    @Override
    public void addStatus(MasterThesis thesis, MasterThesisStatus status) {
        MasterThesisStatusChange masterThesisStatusChange = new MasterThesisStatusChange(thesis, status);
        masterThesisStatusChangeRepository.save(masterThesisStatusChange);
    }

    @Override
    public void addStatus(MasterThesis thesis, MasterThesisStatus status, LocalDateTime date, Boolean approved) {
        MasterThesisStatusChange masterThesisStatusChange = new MasterThesisStatusChange(thesis, date, status, approved);
        masterThesisStatusChangeRepository.save(masterThesisStatusChange);
    }

    @Override
    public void addStatus(MasterThesis thesis, LocalDateTime statusChangeDate, MasterThesisStatus nextStatus, User statusChangedBy, String note) {
        this.masterThesisStatusChangeRepository.save(new MasterThesisStatusChange(thesis, statusChangeDate, nextStatus, statusChangedBy, note));
    }

    @Override
    public void updateStatus(MasterThesis thesis, LocalDateTime date, MasterThesisStatus status, User user, String note) {

    }

    @Override
    public Optional<MasterThesisStatusChange> getStatusChange(MasterThesis thesis) {
        Comparator<MasterThesisStatusChange> comparator = Comparator.comparing(MasterThesisStatusChange::getStatusChangeDate,
                Comparator.nullsLast(Comparator.naturalOrder()));
        return masterThesisStatusChangeRepository.findAllByThesis(thesis).stream().max(comparator);
    }

    @Override
    public List<MasterThesisStatusChange> getAllByThesis(MasterThesis thesis) {
        Comparator<MasterThesisStatusChange> comparator = Comparator.comparing(MasterThesisStatusChange::getStatusChangeDate,
                Comparator.nullsLast(Comparator.naturalOrder()));
        return masterThesisStatusChangeRepository.findAllByThesis(thesis).stream().sorted(comparator.reversed()).toList();
    }


    @Override
    public MasterThesisStatusChange updateStatus(Long statusId, MasterThesis thesis, String note, User user, Boolean approved) {
        MasterThesisStatusChange masterThesisStatusChange = this.masterThesisStatusChangeRepository.findById(statusId).orElse(null);
        if (masterThesisStatusChange != null) {
            masterThesisStatusChange.setNote(note);
            masterThesisStatusChange.setStatusChangeDate(LocalDateTime.now());
            masterThesisStatusChange.setStatusChangedBy(user);
            masterThesisStatusChange.setApproved(approved);
            masterThesisStatusChangeRepository.save(masterThesisStatusChange);
            if (approved)
            {
                MasterThesisStatus status = masterThesisStatusChange.getNextStatus().getNextStatusFromCurrent();
                this.addStatus(thesis, status);
            }
            if (!approved)
            {
                MasterThesisStatus status = masterThesisStatusChange.getNextStatus();
                this.addStatus(thesis, status);
            }

        }
        return masterThesisStatusChange;
    }


    @Override
    public Optional<MasterThesisStatusChange> findByThesis(MasterThesis thesis) {
        return masterThesisStatusChangeRepository.findByThesis(thesis);
    }

}
