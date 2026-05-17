package com.lin101.store.common;

public enum AdminRole {
    BRAND_ADMIN("brand_admin"),
    STORE_MANAGER("store_manager");

    private final String code;

    AdminRole(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public boolean matches(String value) {
        return code.equalsIgnoreCase(value);
    }
}
