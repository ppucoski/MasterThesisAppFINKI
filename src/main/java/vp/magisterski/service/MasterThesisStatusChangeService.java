package vp.magisterski.service;

import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.model.magister.MasterThesisStatusChange;
import vp.magisterski.model.shared.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MasterThesisStatusChangeService {
    void addStatus(MasterThesis thesis, MasterThesisStatus status);
    void addStatus(MasterThesis thesis, MasterThesisStatus status, LocalDateTime date, Boolean approved);
    void addEditStatus(MasterThesis thesis, String note, User user);
    void addStatus(MasterThesis thesis, MasterThesisStatus status, LocalDateTime date, String note, User user);
    void cancelStatus(MasterThesis thesis, LocalDateTime date, String note, User user);
    MasterThesisStatusChange addStatus(MasterThesis thesis, LocalDateTime statusChangeDate, MasterThesisStatus nextStatus, User statusChangedBy, String note);
    void updateStatus(MasterThesis thesis, LocalDateTime date, MasterThesisStatus status, User user, String note);
    Optional<MasterThesisStatusChange> getStatusChange(MasterThesis thesis);

    List<MasterThesisStatusChange> getAllByThesis(MasterThesis thesis);

    MasterThesisStatusChange updateStatus(Long statusId,MasterThesis thesis, String note, User user, Boolean approved);
    MasterThesisStatusChange updateLastStatus(Long statusId,MasterThesis thesis, String note, User user, Boolean approved);
    void updateAndCancelStatus(Long statusId,MasterThesis thesis,String note, User user, Boolean approved);
    Optional<MasterThesisStatusChange> findByThesis(MasterThesis thesis);

}
