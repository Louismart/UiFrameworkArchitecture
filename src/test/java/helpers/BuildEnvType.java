package helpers;

public enum BuildEnvType {

    DEV,
    TST,
    PRD;

    public String getType() {
        return this.toString().toLowerCase();
    }
}
