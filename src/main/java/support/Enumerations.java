package support;

public class Enumerations {
    public enum SortOrder{
        ASC,
        DESC
    }

    public enum StatusReports{
        Daily,
        Transmission,
        Final,
        DDR,
        BASELINE,
        MDN,
        ARCHIVED
    }

    public enum SortValues{
        PATIENT_NAME,
        createdDate,
        eventTime,
        lastName,
        reportId,
        serialNumber,
        firstName,
        patientNumber,
        externalPatientId,
        enrollmentId,
        patientId,
        registrationDate
    }

    public enum DocumentExtension{
        jpeg,
        pdf,
        png
    }

    public enum StatusUsers{
        ACTIVE,
        INACTIVE,
        NOT_CONFIRMED
    }

    public enum Roles{
        ROLE_PRESCRIBER,
        ROLE_CLINICAL_CARE,
        ROLE_PHYSICIAN,
        ROLE_INTERPRETING_PHYSICIAN,
        ROLE_EHR_ADMIN,
        ROLE_ACCOUNT_ADMIN
    }

    /**
     * web service versions, for sorting test cases in the test report
     */
    public enum Versions{
        V1_0,
        V1_5,
        V1_6,
        V1_7,
        V1_8,
        V2_0,
        V2_1,
        V2_2,
        V2_3,
        NO_VERSION,
        ALL
    }
}
