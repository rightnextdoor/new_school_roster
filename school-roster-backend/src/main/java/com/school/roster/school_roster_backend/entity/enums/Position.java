package com.school.roster.school_roster_backend.entity.enums;

import lombok.Getter;

@Getter
public enum Position {
    TEACHER_I(SalaryGrade.TEACHER_I),
    TEACHER_II(SalaryGrade.TEACHER_II),
    TEACHER_III(SalaryGrade.TEACHER_III),
    MASTER_TEACHER_I(SalaryGrade.MASTER_TEACHER_I),
    MASTER_TEACHER_II(SalaryGrade.MASTER_TEACHER_II),
    MASTER_TEACHER_III(SalaryGrade.MASTER_TEACHER_III),
    MASTER_TEACHER_IV(SalaryGrade.MASTER_TEACHER_IV),
    HEAD_TEACHER_I(SalaryGrade.HEAD_TEACHER_I),
    HEAD_TEACHER_II(SalaryGrade.HEAD_TEACHER_II),
    HEAD_TEACHER_III(SalaryGrade.HEAD_TEACHER_III),
    HEAD_TEACHER_IV(SalaryGrade.HEAD_TEACHER_IV),
    HEAD_TEACHER_V(SalaryGrade.HEAD_TEACHER_V),
    HEAD_TEACHER_VI(SalaryGrade.HEAD_TEACHER_VI),
    ASST_SCHOOL_PRINCIPAL_I(SalaryGrade.ASST_SCHOOL_PRINCIPAL_I),
    ASST_SCHOOL_PRINCIPAL_II(SalaryGrade.ASST_SCHOOL_PRINCIPAL_II),
    ASST_SCHOOL_PRINCIPAL_III(SalaryGrade.ASST_SCHOOL_PRINCIPAL_III),
    SCHOOL_PRINCIPAL_I(SalaryGrade.SCHOOL_PRINCIPAL_I),
    SCHOOL_PRINCIPAL_II(SalaryGrade.SCHOOL_PRINCIPAL_II),
    SCHOOL_PRINCIPAL_III(SalaryGrade.SCHOOL_PRINCIPAL_III),
    SCHOOL_PRINCIPAL_IV(SalaryGrade.SCHOOL_PRINCIPAL_IV);

    private final SalaryGrade salaryGrade;

    Position(SalaryGrade salaryGrade) {
        this.salaryGrade = salaryGrade;
    }
}
