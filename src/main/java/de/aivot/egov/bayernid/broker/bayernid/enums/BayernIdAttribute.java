package de.aivot.egov.bayernid.broker.bayernid.enums;

public enum BayernIdAttribute {
    GIVEN_NAME("urn:oid:2.5.4.42", "givenName"),
    SURNAME("urn:oid:2.5.4.4", "surname"),
    EMAIL("urn:oid:0.9.2342.19200300.100.1.3", "email"),
    POSTAL_ADDRESS("urn:oid:2.5.4.16", "postalAddress"),
    POSTAL_CODE("urn:oid:2.5.4.17", "postalCode"),
    LOCALITY_NAME("urn:oid:2.5.4.7", "localityName"),
    COUNTRY("urn:oid:1.2.40.0.10.2.1.1.225599", "Country"),
    PERSONAL_TITLE("urn:oid:0.9.2342.19200300.100.1.40", "personalTitle"),
    GENDER("urn:oid:1.3.6.1.4.1.33592.1.3.5", "gender"),
    BIRTHDATE("urn:oid:1.2.40.0.10.2.1.1.55", "birthdate"),
    PLACE_OF_BIRTH("urn:oid:1.3.6.1.5.5.7.9.2", "placeOfBirth"),
    BIRTH_NAME("urn:oid:1.2.40.0.10.2.1.1.225566", "birthName"),
    NATIONALITY("urn:oid:1.2.40.0.10.2.1.1.225577", "nationality"),
    DOKUMENT_TYPE("urn:oid:1.2.40.0.10.2.1.1.552255", "documentType"),
    DE_MAIL("urn:oid:1.3.6.1.4.1.55605.70737875.1.1.1.7.1", "DeMail"),
    TELEPHONE_NUMBER("urn:oid:2.5.4.20", "telephoneNumber"),
    EIDAS_ISSUING_COUNTRY("urn:oid:1.3.6.1.4.1.25484.494450.10.1", "eIDASIssuingCountry"),
    BPK2("urn:oid:1.3.6.1.4.1.25484.494450.3", "bPK2"),
    TRUST_LEVEL("urn:oid:1.2.40.0.10.2.1.1.261.94", "EID-CITIZEN-QAA-LEVEL"),
    VERSION("urn:oid:1.3.6.1.4.1.25484.494450.1", "Version"),
    ASSERTION_PROVED_BY("urn:oid:1.3.6.1.4.1.25484.494450.1", "AssertionProvedBy"),
    ;
    private final String name;
    private final String friendlyName;

    BayernIdAttribute(String name, String friendlyName) {
        this.name = name;
        this.friendlyName = friendlyName;
    }

    public String getName() {
        return name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
