package com.diabolicallabs.htm;

public enum HumanTaskStateEnum {

    READY("READY"),
    SKIPPED("SKIPPED"),
    ASSIGNED("ASSIGNED"),
    STARTED("STARTED"),
    SUSPENDED("SUSPENDED"),
    ESCALATED("ESCALATED"),
    REASSIGNED("REASSIGNED"),
    RELEASED("RELEASED"),
    TERMINATED("TERMINATED"),
    EXPIRED("EXPIRED"),
    FAILED("FAILED"),
    RESUMED("RESUMED"),
    FINISHED("FINISHED");


    private String name;

    HumanTaskStateEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}