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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MasterThesisStatusChangeServiceImpl implements MasterThesisStatusChangeService {
    private final MasterThesisStatusChangeRepository masterThesisStatusChangeRepository;
    private final MasterThesisService masterThesisService;
    private final UserService userService;


    public MasterThesisStatusChangeServiceImpl(MasterThesisStatusChangeRepository masterThesisStatusChangeRepository, MasterThesisService masterThesisService, UserService userService) {
        this.masterThesisStatusChangeRepository = masterThesisStatusChangeRepository;

        this.masterThesisService = masterThesisService;
        this.userService = userService;
    }

    @Override
    public void addStatus(MasterThesis thesis, MasterThesisStatus status) {
        MasterThesisStatusChange masterThesisStatusChange = new MasterThesisStatusChange(thesis, status);
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
        return masterThesisStatusChangeRepository.findAllByThesis(thesis).stream().sorted(comparator.reversed()).findFirst();
    }

    @Override
    public List<MasterThesisStatusChange> getAllByThesis(MasterThesis thesis) {
        Comparator<MasterThesisStatusChange> comparator = Comparator.comparingDouble(i -> i.getNextStatus().getOrder());
        return masterThesisStatusChangeRepository.findAllByThesis(thesis).stream().sorted(comparator.reversed()).toList();
    }


    @Override
    public MasterThesisStatusChange updateStatus(Long thesisId, String note, User user) {
        MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();
        MasterThesisStatusChange masterThesisStatusChange = this.masterThesisStatusChangeRepository.findByThesis(masterThesis).get();
        masterThesisStatusChange.setNote(note);
        masterThesisStatusChange.setStatusChangeDate(LocalDate.now());
        masterThesisStatusChange.setStatusChangedBy(user);
        masterThesis.setStatus(masterThesisStatusChange.getNextStatus());


        if(masterThesisStatusChange.getNextStatus().getOrder() <= 15){
            double order = masterThesisStatusChange.getNextStatus().getOrder();
            MasterThesisStatus nextStatus = Arrays.stream(MasterThesisStatus.values())
                    .filter(status -> status.getOrder() == order + 1)
                    .findFirst()
                    .orElse(masterThesisStatusChange.getNextStatus());

            masterThesisStatusChange.setNextStatus(nextStatus);
        }

        return masterThesisStatusChangeRepository.save(masterThesisStatusChange);
    }


    @Override
    public Optional<MasterThesisStatusChange> findByThesis(MasterThesis thesis) {
        return masterThesisStatusChangeRepository.findByThesis(thesis);
    }




}
