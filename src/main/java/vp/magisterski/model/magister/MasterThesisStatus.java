package vp.magisterski.model.magister;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MasterThesisStatus {

    STUDENT_THESIS_REGISTRATION(1, "Студентот пополнува пријава и прикачува документи."),
    MENTOR_VALIDATION(2, "Менторот валидира."),
    ADMINISTRATION_VALIDATION(3, "Студентската служба проверува и валидира."),
    COMMISSION_VALIDATION(4, "Наставно научна комисија валидира."),
    SECRETARY_VALIDATION(5, "Секретарот валидира."),
    STUDENT_DRAFT(6, "Студентот подготвува и менторот го прикачува драфт документ."),
    MENTOR_COMMISSION_CHOICE(7, "Менторот избира членови на комисија и валидира."),
    SECOND_SECRETARY_VALIDATION(8, "Секретарот валидира по изборот на комисија."),
    COMMISSION_CHECK(9, "Проверка од ННК."),
    THIRD_SECRETARY_VALIDATION(10, "Секретарот валидира по проверката од ННК."),
    DRAFT_CHECK(11, "Комисија внесува забелешки, студентот корегира, менторот валидира."),
    REPORT_VALIDATION(12, "Членови на комисија проверуваат извештај и валидираат."),
    FOURTH_SECRETARY_VALIDATION(13, "Секретар додава архивски број и валидира."),
    ADMINISTRATION_ARCHIVING(14, "Студентска служба архивира и валидира."),
    PROCESS_FINISHED(15, "Процесот е завршен и се чека одбрана на трудот."),
    CANCELLED(30, "Откажано.");

    private final double order;
    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public MasterThesisStatus getNextStatusFromCurrent() {
        MasterThesisStatus[] statuses = MasterThesisStatus.values();
        for (int i = 0; i < statuses.length - 1; i++) {
            if (statuses[i].order == this.order) {
                return statuses[i + 1];
            }
        }
        return null;
    }
}
