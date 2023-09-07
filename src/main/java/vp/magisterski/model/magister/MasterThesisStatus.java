package vp.magisterski.model.magister;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MasterThesisStatus {
    //TODO: en_us i prashalnicite nekako da isceznat lang=mk mozda vo html neshto.
    PROFESSOR_THESIS_REGISTRATION(1, "Професорот ја регистрира тезата."),
    STUDENT_APPROVAL(2, "Студентот ја прифаќа тезата."),
    ADMINISTRATION_VALIDATION(3, "Валидација од страна на администрација."),
    VICE_DEAN_APPROVAL(4, "Продеканот ја прифаќа тезата."),
    PENDING_TEXT(5, "Во очекување на текст од страна на менторот."),
    MENTOR_TEXT_APPROVAL(6, "Менторот го прифаќа текстот."),
    MEMBERS_TEXT_APPROVAL(7, "Членовите на комисијата го прифаќаат текстот."),
    ADMINISTRATION_PRESENTATION_VALIDATION(8, "Адиминистрацијата ја валидира презентацијата."),
    MENTOR_PRESENTATION_SCHEDULING(9, "Менторот ја закажува презентацијата."),
    PENDING_GRADE(10, "Очекување на оцена од менторот."),
    MEMBERS_GRADE_APPROVAL(11, "Членовите на комисијата ја одобруваат оцената."),
    ARCHIVE(12, "Архива"),
    CANCELED(30, "Откажано");

    private final double order;
    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }
}
