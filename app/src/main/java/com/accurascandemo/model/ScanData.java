package com.accurascandemo.model;

/**
 * Created by richa on 1/5/17.
 */

public class ScanData {

    private int id, mrz;
    private String lastName, firstName, passportNo, country, gender, dateOfBirth, dateOfExpiry, documentType,address, status,
            auth, glassesDecision, glassesScore, livenessAuthFacemap, livenessEnrollFacemap, livenessScore, livenessAuthResultFacemap,
            livenessEnrollResultFacemap, matchScore, livenessResult, retryFeedbackSuggestion;
    private byte[] userPicture;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfExpiry() {
        return dateOfExpiry;
    }

    public void setDateOfExpiry(String dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    public byte[] getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(byte[] userPicture) {
        this.userPicture = userPicture;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public int getMrz() {
        return mrz;
    }

    public void setMrz(int mrz) {
        this.mrz = mrz;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getGlassesDecision() {
        return glassesDecision;
    }

    public void setGlassesDecision(String glassesDecision) {
        this.glassesDecision = glassesDecision;
    }

    public String getGlassesScore() {
        return glassesScore;
    }

    public void setGlassesScore(String glassesScore) {
        this.glassesScore = glassesScore;
    }

    public String getLivenessAuthFacemap() {
        return livenessAuthFacemap;
    }

    public void setLivenessAuthFacemap(String livenessAuthFacemap) {
        this.livenessAuthFacemap = livenessAuthFacemap;
    }

    public String getLivenessEnrollFacemap() {
        return livenessEnrollFacemap;
    }

    public void setLivenessEnrollFacemap(String livenessEnrollFacemap) {
        this.livenessEnrollFacemap = livenessEnrollFacemap;
    }

    public String getLivenessScore() {
        return livenessScore;
    }

    public void setLivenessScore(String livenessScore) {
        this.livenessScore = livenessScore;
    }

    public String getLivenessAuthResultFacemap() {
        return livenessAuthResultFacemap;
    }

    public void setLivenessAuthResultFacemap(String livenessAuthResultFacemap) {
        this.livenessAuthResultFacemap = livenessAuthResultFacemap;
    }

    public String getLivenessEnrollResultFacemap() {
        return livenessEnrollResultFacemap;
    }

    public void setLivenessEnrollResultFacemap(String livenessEnrollResultFacemap) {
        this.livenessEnrollResultFacemap = livenessEnrollResultFacemap;
    }

    public String getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(String matchScore) {
        this.matchScore = matchScore;
    }

    public String getLivenessResult() {
        return livenessResult;
    }

    public void setLivenessResult(String livenessResult) {
        this.livenessResult = livenessResult;
    }

    public String getRetryFeedbackSuggestion() {
        return retryFeedbackSuggestion;
    }

    public void setRetryFeedbackSuggestion(String retryFeedbackSuggestion) {
        this.retryFeedbackSuggestion = retryFeedbackSuggestion;
    }
}
