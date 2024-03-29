package vp.magisterski.model.enumerations;

public enum AppRole {
    PROFESSOR, ADMIN, GUEST, STUDENT;

    public String roleName() {
        return "ROLE_" + this.name();
    }
}
