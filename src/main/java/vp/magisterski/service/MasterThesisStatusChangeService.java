package vp.magisterski.service;

import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisStatus;
import vp.magisterski.model.magister.MasterThesisStatusChange;
import vp.magisterski.model.shared.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MasterThesisStatusChangeService {
    void addStatus(MasterThesis thesis, MasterThesisStatus status);
    void addStatus(MasterThesis thesis, LocalDate statusChangeDate, MasterThesisStatus nextStatus, User statusChangedBy, String note);
    void updateStatus(MasterThesis thesis, LocalDate date, MasterThesisStatus status, User user, String note);
    Optional<MasterThesisStatusChange> getStatusChange(MasterThesis thesis);

    List<MasterThesisStatusChange> getAllByThesis(MasterThesis thesis);

    MasterThesisStatusChange updateStatus(Long thesisId,String note, User user);
    Optional<MasterThesisStatusChange> findByThesis(MasterThesis thesis);


}
