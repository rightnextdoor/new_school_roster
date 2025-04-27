package com.school.roster.school_roster_backend.entity.enums;

import lombok.Getter;

@Getter
public enum SalaryGrade {
    TEACHER_I(11, 30024),
    TEACHER_II(12, 32245),
    TEACHER_III(13, 34421),
    MASTER_TEACHER_I(18, 51304),
    MASTER_TEACHER_II(19, 56390),
    MASTER_TEACHER_III(20, 62967),
    MASTER_TEACHER_IV(21, 70013),
    HEAD_TEACHER_I(14, 37024),
    HEAD_TEACHER_II(15, 40208),
    HEAD_TEACHER_III(16, 43560),
    HEAD_TEACHER_IV(17, 47247),
    HEAD_TEACHER_V(18, 51304),
    HEAD_TEACHER_VI(19, 56390),
    ASST_SCHOOL_PRINCIPAL_I(18, 51304),
    ASST_SCHOOL_PRINCIPAL_II(19, 56390),
    ASST_SCHOOL_PRINCIPAL_III(20, 62967),
    SCHOOL_PRINCIPAL_I(19, 56390),
    SCHOOL_PRINCIPAL_II(20, 62967),
    SCHOOL_PRINCIPAL_III(21, 70013),
    SCHOOL_PRINCIPAL_IV(22, 78162);

    private final int salaryGrade;
    private final int salaryAmount;

    SalaryGrade(int salaryGrade, int salaryAmount) {
        this.salaryGrade = salaryGrade;
        this.salaryAmount = salaryAmount;
    }
}
