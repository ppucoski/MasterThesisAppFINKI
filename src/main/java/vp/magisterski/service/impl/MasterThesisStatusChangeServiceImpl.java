package vp.magisterski.service.impl;

import org.springframework.stereotype.Service;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisStatus;
import vp.magisterski.model.magister.MasterThesisStatusChange;
import vp.magisterski.model.shared.User;
import vp.magisterski.repository.MasterThesisStatusChangeRepository;
import vp.magisterski.service.MasterThesisStatusChangeService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MasterThesisStatusChangeServiceImpl implements MasterThesisStatusChangeService {
    private final MasterThesisStatusChangeRepository masterThesisStatusChangeRepository;

    public MasterThesisStatusChangeServiceImpl(MasterThesisStatusChangeRepository masterThesisStatusChangeRepository) {
        this.masterThesisStatusChangeRepository = masterThesisStatusChangeRepository;
    }

    @Override
    public void addStatus(MasterThesis thesis, MasterThesisStatus status) {
        MasterThesisStatusChange masterThesisStatusChange = new MasterThesisStatusChange(thesis, status);
        masterThesisStatusChangeRepository.save(masterThesisStatusChange);
    }

    @Override
    public void updateStatus(MasterThesis thesis, LocalDate date, MasterThesisStatus status, User user, String note) {

    }

    @Override
    public Optional<MasterThesisStatusChange> getStatusChange(MasterThesis thesis) {
        Comparator<MasterThesisStatusChange> comparator = Comparator.comparingDouble(i -> i.getNextStatus().getOrder());
        return masterThesisStatusChangeRepository.findAllByThesis(thesis).stream().sorted(comparator.reversed()).findFirst();
    }

    @Override
    public List<MasterThesisStatusChange> getAllByThesis(MasterThesis thesis) {
        Comparator<MasterThesisStatusChange> comparator = Comparator.comparingDouble(i -> i.getNextStatus().getOrder());
        return masterThesisStatusChangeRepository.findAllByThesis(thesis).stream().sorted(comparator.reversed()).toList();
    }
}
