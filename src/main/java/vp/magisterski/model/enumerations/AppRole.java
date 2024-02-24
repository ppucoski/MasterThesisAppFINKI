package vp.magisterski.model.enumerations;

public enum AppRole {
    PROFESSOR, ADMIN, GUEST;


    public String roleName() {
        return "ROLE_" + this.name();
    }
}
