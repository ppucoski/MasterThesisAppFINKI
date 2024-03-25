package vp.magisterski.service.impl;

import org.springframework.stereotype.Service;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisStatus;
import vp.magisterski.model.magister.MasterThesisStatusChange;
import vp.magisterski.model.shared.User;
import vp.magisterski.repository.MasterThesisStatusChangeRepository;
import vp.magisterski.service.MasterThesisService;
import vp.magisterski.service.MasterThesisStatusChangeService;
import vp.magisterski.service.UserService;

import java.time.LocalDate;
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
    public void addStatus(MasterThesis thesis, MasterThesisStatus status, LocalDate date) {
        MasterThesisStatusChange masterThesisStatusChange = new MasterThesisStatusChange(thesis, date, status);
        masterThesisStatusChangeRepository.save(masterThesisStatusChange);
    }

    @Override
    public void addStatus(MasterThesis thesis, LocalDate statusChangeDate, MasterThesisStatus nextStatus, User statusChangedBy, String note) {
        this.masterThesisStatusChangeRepository.save(new MasterThesisStatusChange(thesis, statusChangeDate, nextStatus, statusChangedBy, note));
    }

    @Override
    public void updateStatus(MasterThesis thesis, LocalDate date, MasterThesisStatus status, User user, String note) {

    }

    @Override
    public Optional<MasterThesisStatusChange> getStatusChange(MasterThesis thesis) {
        Comparator<MasterThesisStatusChange> comparator = Comparator.comparingDouble(i -> i.getNextStatus().getOrder());
        return masterThesisStatusChangeRepository.findAllByThesis(thesis).stream().max(comparator);
    }

    @Override
    public List<MasterThesisStatusChange> getAllByThesis(MasterThesis thesis) {
        Comparator<MasterThesisStatusChange> comparator = Comparator.comparingDouble(i -> i.getNextStatus().getOrder());
        return masterThesisStatusChangeRepository.findAllByThesis(thesis).stream().sorted(comparator.reversed()).toList();
    }


    @Override
    public MasterThesisStatusChange updateStatus(Long statusId, MasterThesis thesis, String note, User user) {
        MasterThesisStatusChange masterThesisStatusChange = this.masterThesisStatusChangeRepository.findById(statusId).orElse(null);
        if (masterThesisStatusChange != null) {
            masterThesisStatusChange.setNote(note);
            masterThesisStatusChange.setStatusChangeDate(LocalDate.now());
            masterThesisStatusChange.setStatusChangedBy(user);
            masterThesisStatusChangeRepository.save(masterThesisStatusChange);
            MasterThesisStatus status = masterThesisStatusChange.getNextStatus().getNextStatusFromCurrent();
            this.addStatus(thesis, status);
        }
        return masterThesisStatusChange;
    }


    @Override
    public Optional<MasterThesisStatusChange> findByThesis(MasterThesis thesis) {
        return masterThesisStatusChangeRepository.findByThesis(thesis);
    }

}
