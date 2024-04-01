package vp.magisterski.model.shared;

import lombok.Getter;
import vp.magisterski.model.enumerations.AppRole;

public enum UserRole {
    STUDENT(false, true, AppRole.STUDENT),
    // professors
    PROFESSOR(true, false, AppRole.PROFESSOR),
    ACADEMIC_AFFAIR_VICE_DEAN(true, false, AppRole.ACADEMIC_AFFAIR_VICE_DEAN),
    SCIENCE_AND_COOPERATION_VICE_DEAN(true, false, AppRole.SCIENCE_AND_COOPERATION_VICE_DEAN),
    FINANCES_VICE_DEAN(true, false, AppRole.FINANCES_VICE_DEAN),
    DEAN(true, false, AppRole.DEAN),
    // staff
    STUDENT_ADMINISTRATION(false, false, AppRole.STUDENT_ADMINISTRATION),
    STUDENT_ADMINISTRATION_MANAGER(false, false, AppRole.STUDENT_ADMINISTRATION_MANAGER),
    FINANCE_ADMINISTRATION(false, false, AppRole.FINANCE_ADMINISTRATION),
    FINANCE_ADMINISTRATION_MANAGER(false, false, AppRole.FINANCE_ADMINISTRATION_MANAGER),
    LEGAL_ADMINISTRATION(false, false, AppRole.LEGAL_ADMINISTRATION),
    ARCHIVE_ADMINISTRATION(false, false, AppRole.ARCHIVE_ADMINISTRATION),
    ADMINISTRATION_MANAGER(false, false, AppRole.ADMINISTRATION_MANAGER),
    // external professors
    EXTERNAL(true, false, AppRole.EXTERNAL);

    private final Boolean professor;

    private final Boolean student;

    @Getter
    public AppRole applicationRole = AppRole.GUEST;

    UserRole(Boolean professor, Boolean student, AppRole role) {
        this.professor = professor;
        this.student = student;
        this.applicationRole = role;
    }

    UserRole(Boolean professor, Boolean student) {
        this.professor = professor;
        this.student = student;
    }

    public Boolean isProfessor() {
        return professor;
    }

    public Boolean isStudent() {
        return student;
    }

    public String roleName() {
        return "ROLE_" + this.name();
    }
}
