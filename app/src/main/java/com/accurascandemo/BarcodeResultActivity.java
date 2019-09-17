package com.accurascandemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accurascandemo.model.BarcodeData;

/*This class used to display data of Driving license with PDF 417*/

public class BarcodeResultActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvMiddleName, tvMiddleName1, tvLastName, tvLastName1, tvLastName2, tvFirstName, tvFirstName1, tvFirstName2, tvCity, tvZipCode, tvBirthDate, tvBirthDate1, tvSex, tvJurisdictionCode, tvLicenseClassification, tvLicenseRestriction,
            tvLicenseEndorsement, tvAddress1, tvAddress2, tvResidenceAddress1, tvResidenceAddress2, tvIssueDate, tvOrganDonor, tvHeightInFt, tvLicenseNumber, tvLicenseExpiryDate, tvFullName, tvFullName1,
            tvHeightInCm, tvWeightInLbs, tvWeightInKg, tvHairColor, tvEyeColor, tvIssueTime, tvNumberOfDuplicate, tvUniqueCustomerId, tvSocialSecurityNumber, tvSocialSecurityNumber1,
            tvUnder18, tvUnder19, tvUnder21, tvPermitClassification, tvVeteranIndicator, tvPermitIssue, tvPermitExpire, tvPermitRestriction, tvPermitEndorsement,
            tvCourtRestriction, tvInventoryControlNo, tvRaceEthnicity, tvStandardVehicleClass, tvDocumentDiscriminator, tvNameSuffix, tvNamePrefix, tvSuffix, tvSuffix1, tvPrefix, tvWholeData,
            tvResidencePostalCode, tvResidenceCity, tvResidenceJurisdictionCode, tvMedicalIndicatorCodes, tvNonResidentIndicator, tvVirginiaSpecificClass, tvVirginiaSpecificRestrictions, tvVirginiaSpecificEndorsements,
            tvPhysicalDescriptionWeight, tvCountryTerritoryOfIssuance, tvFederalCommercialVehicleCodes, tvPlaceOfBirth, tvAuditInformation, tvStandardEndorsementCode,
            tvStandardRestrictionCode, tvJuriSpeciVehiClassiDes, tvJurisdictionSpecific, tvJuriSpeciRestriCodeDescri, tvComplianceType, tvCardRevisionDate, tvHazMatEndorsementExpiryDate,
            tvLimitedDurationDocumentIndicator, tvFamilyNameTruncation, tvFirstNamesTruncation, tvMiddleNamesTruncation, tvOrganDonorIndicator, tvPermitIdentifier;
    private LinearLayout llFirstName1, llFirstName2, llMiddleName, llMiddleName1, llCity, llLastName, llLastName1, llLastName2, llZipcode, llBirthDate, llBirthDate1, llSex, llAddress1, llAddress2, llResidenceAddress1, llResidenceAddress2, llJurisdictionCode,
            llLicenseClassification, llLicenseRestriction, llLicenseEndorsement, llIssueDate, llOrganDonor, llHeightInFt, llLicenseNumber,
            llLicenseExpiryDate, llHeightInCm, llWeightInLbs, llWeightInKg, llHairColor, llIssueTime, llNumberOfDuplicate,
            llUniqueCustomerId, llSocialSecurityNumber, llSocialSecurityNumber1, llUnder18, llUnder19, llUnder21, llVeteranIndicator, llPermitIssue, llPermitExpire,
            llPermitRestriction, llPermitEndorsement, llCourtRestriction, llInventoryControlNo, llRaceEthnicity, llStandardVehicleClass,
            llDocumentDiscriminator, llFullName, llFullName1, llSuffix, llSuffix1, llPreffix, llNameSuffix, llNamePreffix, llEyeColor, llPermitClassification, llFormatting,
            llResidencePostalCode, llResidenceCity, llResidenceJurisdictionCode, llMedicalIndicatorCodes, llNonResidentIndicator, llVirginiaSpecificClass, llVirginiaSpecificRestrictions, llVirginiaSpecificEndorsements,
            llPhysicalDescriptionWeight, llCountryTerritoryOfIssuance, llFederalCommercialVehicleCodes, llPlaceOfBirth, llAuditInformation, llStandardEndorsementCode,
            llStandardRestrictionCode, llJuriSpeciVehiClassiDescri, llJurisdictionSpecific, llJuriSpeciRestriCodeDescri, llComplianceType, llCardRevisionDate, llHazMatEndorsementExpiryDate,
            llLimitedDurationDocumentIndicator, llFamilyNameTruncation, llFirstNamesTruncation, llMiddleNamesTruncation, llOrganDonorIndicator, llPermitIdentifier;
    private BarcodeData barcodeData;

    public static void startActivity(Activity activity, BarcodeData barcodeData) {
        Intent intent = new Intent(activity, BarcodeResultActivity.class);
        intent.putExtra("barcodeData", barcodeData);
        activity.startActivityForResult(intent, 109);
        activity.overridePendingTransition(0, 0);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_result);

        getDataFromIntent(getIntent());
        initUI();
    }

    private void getDataFromIntent(Intent intent) {
        barcodeData = intent.getParcelableExtra("barcodeData");
    }

    private void initUI() {
        findViewById(R.id.tvUSADLResult).setOnClickListener(this);
        findViewById(R.id.tvPDF417Barcode).setOnClickListener(this);
        tvWholeData = findViewById(R.id.tvWholeData);
        tvMiddleName = findViewById(R.id.tvMiddleName);
        tvMiddleName1 = findViewById(R.id.tvMiddleName1);
        tvLastName = findViewById(R.id.tvLastName);
        tvLastName1 = findViewById(R.id.tvLastName1);
        tvLastName2 = findViewById(R.id.tvLastName2);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvFirstName1 = findViewById(R.id.tvFirstName1);
        tvFirstName2 = findViewById(R.id.tvFirstName2);
        tvResidencePostalCode = findViewById(R.id.tvResidencePostalCode);
        tvCity = findViewById(R.id.tvCity);
        tvZipCode = findViewById(R.id.tvZipCode);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        tvBirthDate1 = findViewById(R.id.tvBirthDate1);
        tvSex = findViewById(R.id.tvSex);
        tvNameSuffix = findViewById(R.id.tvNameSuffix);
        tvNamePrefix = findViewById(R.id.tvNamePrefix);
        tvSuffix = findViewById(R.id.tvSuffix);
        tvSuffix1 = findViewById(R.id.tvSuffix1);
        tvPrefix = findViewById(R.id.tvPrefix);
        tvJurisdictionCode = findViewById(R.id.tvJurisdictionCode);
        tvLicenseClassification = findViewById(R.id.tvLicenseClassification);
        tvLicenseRestriction = findViewById(R.id.tvLicenseRestriction);
        tvLicenseEndorsement = findViewById(R.id.tvLicenseEndorsement);
        tvIssueDate = findViewById(R.id.tvIssueDate);
        tvOrganDonor = findViewById(R.id.tvOrganDonor);
        tvHeightInFt = findViewById(R.id.tvHeightInFt);
        tvLicenseNumber = findViewById(R.id.tvLicenseNumber);
        tvLicenseExpiryDate = findViewById(R.id.tvLicenseExpiryDate);
        tvFullName = findViewById(R.id.tvFullName);
        tvFullName1 = findViewById(R.id.tvFullName1);
        tvHeightInCm = findViewById(R.id.tvHeightInCm);
        tvWeightInLbs = findViewById(R.id.tvWeightInLbs);
        tvWeightInKg = findViewById(R.id.tvWeightInKg);
        tvHairColor = findViewById(R.id.tvHairColor);
        tvEyeColor = findViewById(R.id.tvEyeColor);
        tvIssueTime = findViewById(R.id.tvIssueTime);
        tvNumberOfDuplicate = findViewById(R.id.tvNumberOfDuplicate);
        tvUniqueCustomerId = findViewById(R.id.tvUniqueCustomerId);
        tvSocialSecurityNumber = findViewById(R.id.tvSocialSecurityNumber);
        tvSocialSecurityNumber1 = findViewById(R.id.tvSocialSecurityNumber1);
        tvUnder18 = findViewById(R.id.tvUnder18);
        tvUnder19 = findViewById(R.id.tvUnder19);
        tvUnder21 = findViewById(R.id.tvUnder21);
        tvPermitClassification = findViewById(R.id.tvPermitClassification);
        tvVeteranIndicator = findViewById(R.id.tvVeteranIndicator);
        tvPermitIssue = findViewById(R.id.tvPermitIssue);
        tvPermitExpire = findViewById(R.id.tvPermitExpire);
        tvPermitRestriction = findViewById(R.id.tvPermitRestriction);
        tvPermitEndorsement = findViewById(R.id.tvPermitEndorsement);
        tvCourtRestriction = findViewById(R.id.tvCourtRestriction);
        tvInventoryControlNo = findViewById(R.id.tvInventoryControlNo);
        tvRaceEthnicity = findViewById(R.id.tvRaceEthnicity);
        tvStandardVehicleClass = findViewById(R.id.tvStandardVehicleClass);
        tvDocumentDiscriminator = findViewById(R.id.tvDocumentDiscriminator);
        tvAddress1 = findViewById(R.id.tvAddressLine1);
        tvAddress2 = findViewById(R.id.tvAddressLine2);
        tvResidenceAddress1 = findViewById(R.id.tvResidenceAddress1);
        tvResidenceAddress2 = findViewById(R.id.tvResidenceAddress2);
        tvDocumentDiscriminator = findViewById(R.id.tvDocumentDiscriminator);
        tvResidenceCity = findViewById(R.id.tvResidenceCity);
        tvResidenceJurisdictionCode = findViewById(R.id.tvResidenceJurisdictionCode);
        tvMedicalIndicatorCodes = findViewById(R.id.tvMedicalIndicatorCodes);
        tvNonResidentIndicator = findViewById(R.id.tvNonResidentIndicator);
        tvVirginiaSpecificClass = findViewById(R.id.tvVirginiaSpecificClass);
        tvVirginiaSpecificRestrictions = findViewById(R.id.tvVirginiaSpecificRestrictions);
        tvVirginiaSpecificEndorsements = findViewById(R.id.tvVirginiaSpecificEndorsements);
        tvPhysicalDescriptionWeight = findViewById(R.id.tvPhysicalDescriptionWeight);
        tvCountryTerritoryOfIssuance = findViewById(R.id.tvCountryTerritoryOfIssuance);
        tvFederalCommercialVehicleCodes = findViewById(R.id.tvFederalCommercialVehicleCodes);
        tvPlaceOfBirth = findViewById(R.id.tvPlaceOfBirth);
        tvAuditInformation = findViewById(R.id.tvAuditInformation);
        tvStandardEndorsementCode = findViewById(R.id.tvStandardEndorsementCode);
        tvStandardRestrictionCode = findViewById(R.id.tvStandardRestrictionCode);
        tvJuriSpeciVehiClassiDes = findViewById(R.id.tvJuriSpeciVehiClassiDescri);
        tvJurisdictionSpecific = findViewById(R.id.tvJurisdictionSpecific);
        tvJuriSpeciRestriCodeDescri = findViewById(R.id.tvJuriSpeciRestriCodeDescri);
        tvComplianceType = findViewById(R.id.tvComplianceType);
        tvCardRevisionDate = findViewById(R.id.tvCardRevisionDate);
        tvHazMatEndorsementExpiryDate = findViewById(R.id.tvHazMatEndorsementExpiryDate);
        tvLimitedDurationDocumentIndicator = findViewById(R.id.tvLimitedDurationDocumentIndicator);
        tvFamilyNameTruncation = findViewById(R.id.tvFamilyNameTruncation);
        tvFirstNamesTruncation = findViewById(R.id.tvFirstNamesTruncation);
        tvMiddleNamesTruncation = findViewById(R.id.tvMiddleNamesTruncation);
        tvOrganDonorIndicator = findViewById(R.id.tvOrganDonorIndicator);
        tvPermitIdentifier = findViewById(R.id.tvPermitIdentifier);

        llVirginiaSpecificEndorsements = findViewById(R.id.llVirginiaSpecificEndorsements);
        llResidencePostalCode = findViewById(R.id.llResidencePostalCode);
        llPermitIdentifier = findViewById(R.id.llPermitIdentifier);
        llOrganDonorIndicator = findViewById(R.id.llOrganDonorIndicator);
        llMiddleNamesTruncation = findViewById(R.id.llMiddleNamesTruncation);
        llFirstNamesTruncation = findViewById(R.id.llFirstNamesTruncation);
        llFamilyNameTruncation = findViewById(R.id.llFamilyNameTruncation);
        llLimitedDurationDocumentIndicator = findViewById(R.id.llLimitedDurationDocumentIndicator);
        llHazMatEndorsementExpiryDate = findViewById(R.id.llHazMatEndorsementExpiryDate);
        llCardRevisionDate = findViewById(R.id.llCardRevisionDate);
        llJuriSpeciRestriCodeDescri = findViewById(R.id.llJuriSpeciRestriCodeDescri);
        llComplianceType = findViewById(R.id.llComplianceType);
        llJurisdictionSpecific = findViewById(R.id.llJurisdictionSpecific);
        llJuriSpeciVehiClassiDescri = findViewById(R.id.llJuriSpeciVehiClassiDescri);
        llStandardRestrictionCode = findViewById(R.id.llStandardRestrictionCode);
        llStandardEndorsementCode = findViewById(R.id.llStandardEndorsementCode);
        llAuditInformation = findViewById(R.id.llAuditInformation);
        llPlaceOfBirth = findViewById(R.id.llPlaceOfBirth);
        llFederalCommercialVehicleCodes = findViewById(R.id.llFederalCommercialVehicleCodes);
        llCountryTerritoryOfIssuance = findViewById(R.id.llCountryTerritoryOfIssuance);
        llPhysicalDescriptionWeight = findViewById(R.id.llPhysicalDescriptionWeight);
        llVirginiaSpecificRestrictions = findViewById(R.id.llVirginiaSpecificRestrictions);
        llVirginiaSpecificClass = findViewById(R.id.llVirginiaSpecificClass);
        llNonResidentIndicator = findViewById(R.id.llNonResidentIndicator);
        llMedicalIndicatorCodes = findViewById(R.id.llMedicalIndicatorCodes);
        llResidenceJurisdictionCode = findViewById(R.id.llResidenceJurisdictionCode);
        llResidenceCity = findViewById(R.id.llResidenceCity);
        llDocumentDiscriminator = findViewById(R.id.llDocumentDiscriminator);
        llFormatting = findViewById(R.id.llFormatting);
        llFirstName1 = findViewById(R.id.llFirstName1);
        llFirstName2 = findViewById(R.id.llFirstName2);
        llMiddleName = findViewById(R.id.llMiddleName);
        llMiddleName1 = findViewById(R.id.llMiddleName1);
        llCity = findViewById(R.id.llCity);
        llLastName = findViewById(R.id.llLastName);
        llLastName1 = findViewById(R.id.llLastName1);
        llLastName2 = findViewById(R.id.llLastName2);
        llZipcode = findViewById(R.id.llZipcode);
        llBirthDate = findViewById(R.id.llBirthDate);
        llBirthDate1 = findViewById(R.id.llBirthDate1);
        llSex = findViewById(R.id.llSex);
        llAddress1 = findViewById(R.id.llAddressLine1);
        llAddress2 = findViewById(R.id.llAddressLine2);
        llResidenceAddress1 = findViewById(R.id.llResidenceAddress1);
        llResidenceAddress2 = findViewById(R.id.llResidenceAddress2);
        llFullName = findViewById(R.id.llFullName);
        llFullName1 = findViewById(R.id.llFullName1);
        llNamePreffix = findViewById(R.id.llNamePrefix);
        llNameSuffix = findViewById(R.id.llNameSuffix);
        llPreffix = findViewById(R.id.llPrefix);
        llSuffix = findViewById(R.id.llSuffix);
        llSuffix1 = findViewById(R.id.llSuffix1);
        llJurisdictionCode = findViewById(R.id.llJurisdictionCode);
        llLicenseClassification = findViewById(R.id.llLicenseClassification);
        llLicenseRestriction = findViewById(R.id.llLicenseRestriction);
        llLicenseEndorsement = findViewById(R.id.llLicenseEndorsement);
        llIssueDate = findViewById(R.id.llIssueDate);
        llOrganDonor = findViewById(R.id.llOrganDonor);
        llHeightInFt = findViewById(R.id.llHeightInFt);
        llLicenseNumber = findViewById(R.id.llLicenseNumber);
        llLicenseExpiryDate = findViewById(R.id.llLicenseExpiryDate);
        llHeightInCm = findViewById(R.id.llHeightInCm);
        llWeightInLbs = findViewById(R.id.llWeightInLbs);
        llWeightInKg = findViewById(R.id.llWeightInKg);
        llHairColor = findViewById(R.id.llHairColor);
        llIssueTime = findViewById(R.id.llIssueTime);
        llNumberOfDuplicate = findViewById(R.id.llNumberOfDuplicate);
        llUniqueCustomerId = findViewById(R.id.llUniqueCustomerId);
        llSocialSecurityNumber = findViewById(R.id.llSocialSecurityNumber);
        llSocialSecurityNumber1 = findViewById(R.id.llSocialSecurityNumber1);
        llUnder18 = findViewById(R.id.llUnder18);
        llUnder19 = findViewById(R.id.llUnder19);
        llUnder21 = findViewById(R.id.llUnder21);
        llVeteranIndicator = findViewById(R.id.llVeteranIndicator);
        llPermitIssue = findViewById(R.id.llPermitIssue);
        llPermitExpire = findViewById(R.id.llPermitExpire);
        llPermitClassification = findViewById(R.id.llPermitClassification);
        llPermitRestriction = findViewById(R.id.llPermitRestriction);
        llPermitEndorsement = findViewById(R.id.llPermitEndorsement);
        llCourtRestriction = findViewById(R.id.llCourtRestriction);
        llInventoryControlNo = findViewById(R.id.llInventoryControlNo);
        llRaceEthnicity = findViewById(R.id.llRaceEthnicity);
        llStandardVehicleClass = findViewById(R.id.llStandardVehicleClass);
        llDocumentDiscriminator = findViewById(R.id.llDocumentDiscriminator);
        llHeightInCm = findViewById(R.id.llHeightInCm);
        llEyeColor = findViewById(R.id.llEyeColor);

        findViewById(R.id.tvSave).setOnClickListener(this);

        setData();
    }

    private void setData() {

        /*Set result on textview and hide row if there is no value*/

        tvWholeData.setText(barcodeData.wholeDataString);
        tvWholeData.setVisibility(View.GONE);
        tvFirstName.setText(barcodeData.fname);
        llFirstName1.setVisibility(!TextUtils.isEmpty(barcodeData.firstName) ? View.VISIBLE : View.GONE);
        tvFirstName1.setText(barcodeData.firstName);
        llFirstName2.setVisibility(!TextUtils.isEmpty(barcodeData.firstName1) ? View.VISIBLE : View.GONE);
        tvFirstName2.setText(barcodeData.firstName1);
        llLastName.setVisibility(!TextUtils.isEmpty(barcodeData.lname) ? View.VISIBLE : View.GONE);
        tvLastName.setText(barcodeData.lname);
        llLastName1.setVisibility(!TextUtils.isEmpty(barcodeData.lastName) ? View.VISIBLE : View.GONE);
        tvLastName1.setText(barcodeData.lastName);
        llLastName2.setVisibility(!TextUtils.isEmpty(barcodeData.lastName1) ? View.VISIBLE : View.GONE);
        tvLastName2.setText(barcodeData.lastName1);
        llMiddleName.setVisibility(!TextUtils.isEmpty(barcodeData.mname) ? View.VISIBLE : View.GONE);
        tvMiddleName.setText(barcodeData.mname);
        llMiddleName1.setVisibility(!TextUtils.isEmpty(barcodeData.middleName) ? View.VISIBLE : View.GONE);
        tvMiddleName1.setText(barcodeData.middleName);
        llAddress1.setVisibility(!TextUtils.isEmpty(barcodeData.address1) ? View.VISIBLE : View.GONE);
        tvAddress1.setText(barcodeData.address1);
        llAddress2.setVisibility(!TextUtils.isEmpty(barcodeData.address2) ? View.VISIBLE : View.GONE);
        tvAddress2.setText(barcodeData.address2);
        llResidenceAddress1.setVisibility(!TextUtils.isEmpty(barcodeData.ResidenceAddress1) ? View.VISIBLE : View.GONE);
        tvResidenceAddress1.setText(barcodeData.ResidenceAddress1);
        llResidenceAddress2.setVisibility(!TextUtils.isEmpty(barcodeData.ResidenceAddress2) ? View.VISIBLE : View.GONE);
        tvResidenceAddress2.setText(barcodeData.ResidenceAddress2);
        llCity.setVisibility(!TextUtils.isEmpty(barcodeData.city) ? View.VISIBLE : View.GONE);
        tvCity.setText(barcodeData.city);
        llZipcode.setVisibility(!TextUtils.isEmpty(barcodeData.zipcode) ? View.VISIBLE : View.GONE);
        tvZipCode.setText(barcodeData.zipcode);
        llBirthDate.setVisibility(!TextUtils.isEmpty(barcodeData.birthday) ? View.VISIBLE : View.GONE);
        tvBirthDate.setText(barcodeData.birthday);
        llBirthDate1.setVisibility(!TextUtils.isEmpty(barcodeData.birthday1) ? View.VISIBLE : View.GONE);
        tvBirthDate1.setText(barcodeData.birthday1);
        llLicenseNumber.setVisibility(!TextUtils.isEmpty(barcodeData.licence_number) ? View.VISIBLE : View.GONE);
        tvLicenseNumber.setText(barcodeData.licence_number);
        llLicenseExpiryDate.setVisibility(!TextUtils.isEmpty(barcodeData.licence_expire_date) ? View.VISIBLE : View.GONE);
        tvLicenseExpiryDate.setText(barcodeData.licence_expire_date);
        llSex.setVisibility(!TextUtils.isEmpty(barcodeData.sex) ? View.VISIBLE : View.GONE);
        tvSex.setText(barcodeData.sex);
        llJurisdictionCode.setVisibility(!TextUtils.isEmpty(barcodeData.jurisdiction) ? View.VISIBLE : View.GONE);
        tvJurisdictionCode.setText(barcodeData.jurisdiction);
        llLicenseClassification.setVisibility(!TextUtils.isEmpty(barcodeData.licenseClassification) ? View.VISIBLE : View.GONE);
        tvLicenseClassification.setText(barcodeData.licenseClassification);
        llLicenseRestriction.setVisibility(!TextUtils.isEmpty(barcodeData.licenseRestriction) ? View.VISIBLE : View.GONE);
        tvLicenseRestriction.setText(barcodeData.licenseRestriction);
        llLicenseEndorsement.setVisibility(!TextUtils.isEmpty(barcodeData.licenseEndorsement) ? View.VISIBLE : View.GONE);
        tvLicenseEndorsement.setText(barcodeData.licenseEndorsement);
        llIssueDate.setVisibility(!TextUtils.isEmpty(barcodeData.issueDate) ? View.VISIBLE : View.GONE);
        tvIssueDate.setText(barcodeData.issueDate);
        llOrganDonor.setVisibility(!TextUtils.isEmpty(barcodeData.organDonor) ? View.VISIBLE : View.GONE);
        tvOrganDonor.setText(barcodeData.organDonor);
        llHeightInFt.setVisibility(!TextUtils.isEmpty(barcodeData.heightinFT) ? View.VISIBLE : View.GONE);
        tvHeightInFt.setText(barcodeData.heightinFT);
        llFullName.setVisibility(!TextUtils.isEmpty(barcodeData.fullName) ? View.VISIBLE : View.GONE);
        tvFullName.setText(barcodeData.fullName);
        llFullName1.setVisibility(!TextUtils.isEmpty(barcodeData.fullName1) ? View.VISIBLE : View.GONE);
        tvFullName1.setText(barcodeData.fullName1);
        llHeightInCm.setVisibility(!TextUtils.isEmpty(barcodeData.heightCM) ? View.VISIBLE : View.GONE);
        tvHeightInCm.setText(barcodeData.heightCM);
        llWeightInLbs.setVisibility(!TextUtils.isEmpty(barcodeData.weightLBS) ? View.VISIBLE : View.GONE);
        tvWeightInLbs.setText(barcodeData.weightLBS);
        llWeightInKg.setVisibility(!TextUtils.isEmpty(barcodeData.weightKG) ? View.VISIBLE : View.GONE);
        tvWeightInKg.setText(barcodeData.weightKG);
        llNamePreffix.setVisibility(!TextUtils.isEmpty(barcodeData.namePrefix) ? View.VISIBLE : View.GONE);
        tvNamePrefix.setText(barcodeData.namePrefix);
        llNameSuffix.setVisibility(!TextUtils.isEmpty(barcodeData.nameSuffix) ? View.VISIBLE : View.GONE);
        tvNameSuffix.setText(barcodeData.nameSuffix);
        llPreffix.setVisibility(!TextUtils.isEmpty(barcodeData.Prefix) ? View.VISIBLE : View.GONE);
        tvPrefix.setText(barcodeData.Prefix);
        llSuffix.setVisibility(!TextUtils.isEmpty(barcodeData.Suffix) ? View.VISIBLE : View.GONE);
        tvSuffix.setText(barcodeData.Suffix);
        llSuffix1.setVisibility(!TextUtils.isEmpty(barcodeData.Suffix1) ? View.VISIBLE : View.GONE);
        tvSuffix1.setText(barcodeData.Suffix1);
        llEyeColor.setVisibility(!TextUtils.isEmpty(barcodeData.eyeColor) ? View.VISIBLE : View.GONE);
        tvEyeColor.setText(barcodeData.eyeColor);
        llHairColor.setVisibility(!TextUtils.isEmpty(barcodeData.hairColor) ? View.VISIBLE : View.GONE);
        tvHairColor.setText(barcodeData.hairColor);
        llIssueTime.setVisibility(!TextUtils.isEmpty(barcodeData.issueTime) ? View.VISIBLE : View.GONE);
        tvIssueTime.setText(barcodeData.issueTime);
        llNumberOfDuplicate.setVisibility(!TextUtils.isEmpty(barcodeData.numberDuplicate) ? View.VISIBLE : View.GONE);
        tvNumberOfDuplicate.setText(barcodeData.numberDuplicate);
        llUniqueCustomerId.setVisibility(!TextUtils.isEmpty(barcodeData.uniqueCustomerId) ? View.VISIBLE : View.GONE);
        tvUniqueCustomerId.setText(barcodeData.uniqueCustomerId);
        llSocialSecurityNumber.setVisibility(!TextUtils.isEmpty(barcodeData.socialSecurityNo) ? View.VISIBLE : View.GONE);
        tvSocialSecurityNumber.setText(barcodeData.socialSecurityNo);
        llSocialSecurityNumber1.setVisibility(!TextUtils.isEmpty(barcodeData.socialSecurityNo1) ? View.VISIBLE : View.GONE);
        tvSocialSecurityNumber1.setText(barcodeData.socialSecurityNo1);
        llUnder18.setVisibility(!TextUtils.isEmpty(barcodeData.under18) ? View.VISIBLE : View.GONE);
        tvUnder18.setText(barcodeData.under18);
        llUnder19.setVisibility(!TextUtils.isEmpty(barcodeData.under19) ? View.VISIBLE : View.GONE);
        tvUnder19.setText(barcodeData.under19);
        llUnder21.setVisibility(!TextUtils.isEmpty(barcodeData.under21) ? View.VISIBLE : View.GONE);
        tvUnder21.setText(barcodeData.under21);
        llPermitClassification.setVisibility(!TextUtils.isEmpty(barcodeData.permitClassification) ? View.VISIBLE : View.GONE);
        tvPermitClassification.setText(barcodeData.permitClassification);
        llVeteranIndicator.setVisibility(!TextUtils.isEmpty(barcodeData.veteranIndicator) ? View.VISIBLE : View.GONE);
        tvVeteranIndicator.setText(barcodeData.veteranIndicator);
        llPermitIssue.setVisibility(!TextUtils.isEmpty(barcodeData.permitIssue) ? View.VISIBLE : View.GONE);
        tvPermitIssue.setText(barcodeData.permitIssue);
        llPermitExpire.setVisibility(!TextUtils.isEmpty(barcodeData.permitExpire) ? View.VISIBLE : View.GONE);
        tvPermitExpire.setText(barcodeData.permitExpire);
        llPermitRestriction.setVisibility(!TextUtils.isEmpty(barcodeData.permitRestriction) ? View.VISIBLE : View.GONE);
        tvPermitRestriction.setText(barcodeData.permitRestriction);
        llPermitEndorsement.setVisibility(!TextUtils.isEmpty(barcodeData.permitEndorsement) ? View.VISIBLE : View.GONE);
        tvPermitEndorsement.setText(barcodeData.permitEndorsement);
        llCourtRestriction.setVisibility(!TextUtils.isEmpty(barcodeData.courtRestriction) ? View.VISIBLE : View.GONE);
        tvCourtRestriction.setText(barcodeData.courtRestriction);
        llInventoryControlNo.setVisibility(!TextUtils.isEmpty(barcodeData.inventoryNo) ? View.VISIBLE : View.GONE);
        tvInventoryControlNo.setText(barcodeData.inventoryNo);
        llRaceEthnicity.setVisibility(!TextUtils.isEmpty(barcodeData.raceEthnicity) ? View.VISIBLE : View.GONE);
        tvRaceEthnicity.setText(barcodeData.raceEthnicity);
        llStandardVehicleClass.setVisibility(!TextUtils.isEmpty(barcodeData.standardVehicleClass) ? View.VISIBLE : View.GONE);
        tvStandardVehicleClass.setText(barcodeData.standardVehicleClass);
        llDocumentDiscriminator.setVisibility(!TextUtils.isEmpty(barcodeData.documentDiscriminator) ? View.VISIBLE : View.GONE);
        tvDocumentDiscriminator.setText(barcodeData.documentDiscriminator);
        llResidenceCity.setVisibility(!TextUtils.isEmpty(barcodeData.ResidenceCity) ? View.VISIBLE : View.GONE);
        tvResidenceCity.setText(barcodeData.ResidenceCity);
        llResidenceJurisdictionCode.setVisibility(!TextUtils.isEmpty(barcodeData.ResidenceJurisdictionCode) ? View.VISIBLE : View.GONE);
        tvResidenceJurisdictionCode.setText(barcodeData.ResidenceJurisdictionCode);
        llResidencePostalCode.setVisibility(!TextUtils.isEmpty(barcodeData.ResidencePostalCode) ? View.VISIBLE : View.GONE);
        tvResidencePostalCode.setText(barcodeData.ResidencePostalCode);
        llMedicalIndicatorCodes.setVisibility(!TextUtils.isEmpty(barcodeData.MedicalIndicatorCodes) ? View.VISIBLE : View.GONE);
        tvMedicalIndicatorCodes.setText(barcodeData.MedicalIndicatorCodes);
        llNonResidentIndicator.setVisibility(!TextUtils.isEmpty(barcodeData.NonResidentIndicator) ? View.VISIBLE : View.GONE);
        tvNonResidentIndicator.setText(barcodeData.NonResidentIndicator);
        llVirginiaSpecificClass.setVisibility(!TextUtils.isEmpty(barcodeData.VirginiaSpecificClass) ? View.VISIBLE : View.GONE);
        tvVirginiaSpecificClass.setText(barcodeData.VirginiaSpecificClass);
        llVirginiaSpecificRestrictions.setVisibility(!TextUtils.isEmpty(barcodeData.VirginiaSpecificRestrictions) ? View.VISIBLE : View.GONE);
        tvVirginiaSpecificRestrictions.setText(barcodeData.VirginiaSpecificRestrictions);
        llVirginiaSpecificEndorsements.setVisibility(!TextUtils.isEmpty(barcodeData.VirginiaSpecificEndorsements) ? View.VISIBLE : View.GONE);
        tvVirginiaSpecificEndorsements.setText(barcodeData.VirginiaSpecificEndorsements);
        llPhysicalDescriptionWeight.setVisibility(!TextUtils.isEmpty(barcodeData.PhysicalDescriptionWeight) ? View.VISIBLE : View.GONE);
        tvPhysicalDescriptionWeight.setText(barcodeData.PhysicalDescriptionWeight);
        llCountryTerritoryOfIssuance.setVisibility(!TextUtils.isEmpty(barcodeData.CountryTerritoryOfIssuance) ? View.VISIBLE : View.GONE);
        tvCountryTerritoryOfIssuance.setText(barcodeData.CountryTerritoryOfIssuance);
        llFederalCommercialVehicleCodes.setVisibility(!TextUtils.isEmpty(barcodeData.FederalCommercialVehicleCodes) ? View.VISIBLE : View.GONE);
        tvFederalCommercialVehicleCodes.setText(barcodeData.FederalCommercialVehicleCodes);
        llPlaceOfBirth.setVisibility(!TextUtils.isEmpty(barcodeData.PlaceOfBirth) ? View.VISIBLE : View.GONE);
        tvPlaceOfBirth.setText(barcodeData.PlaceOfBirth);
        llStandardEndorsementCode.setVisibility(!TextUtils.isEmpty(barcodeData.StandardEndorsementCode) ? View.VISIBLE : View.GONE);
        tvStandardEndorsementCode.setText(barcodeData.StandardEndorsementCode);
        llStandardRestrictionCode.setVisibility(!TextUtils.isEmpty(barcodeData.StandardRestrictionCode) ? View.VISIBLE : View.GONE);
        tvStandardRestrictionCode.setText(barcodeData.StandardRestrictionCode);
        llJuriSpeciVehiClassiDescri.setVisibility(!TextUtils.isEmpty(barcodeData.JuriSpeciVehiClassiDescri) ? View.VISIBLE : View.GONE);
        tvJuriSpeciVehiClassiDes.setText(barcodeData.JuriSpeciVehiClassiDescri);
        llJuriSpeciRestriCodeDescri.setVisibility(!TextUtils.isEmpty(barcodeData.JuriSpeciRestriCodeDescri) ? View.VISIBLE : View.GONE);
        tvJuriSpeciRestriCodeDescri.setText(barcodeData.JuriSpeciRestriCodeDescri);
        llComplianceType.setVisibility(!TextUtils.isEmpty(barcodeData.ComplianceType) ? View.VISIBLE : View.GONE);
        tvComplianceType.setText(barcodeData.ComplianceType);
        llCardRevisionDate.setVisibility(!TextUtils.isEmpty(barcodeData.CardRevisionDate) ? View.VISIBLE : View.GONE);
        tvCardRevisionDate.setText(barcodeData.CardRevisionDate);
        llHazMatEndorsementExpiryDate.setVisibility(!TextUtils.isEmpty(barcodeData.HazMatEndorsementExpiryDate) ? View.VISIBLE : View.GONE);
        tvHazMatEndorsementExpiryDate.setText(barcodeData.HazMatEndorsementExpiryDate);
        llLimitedDurationDocumentIndicator.setVisibility(!TextUtils.isEmpty(barcodeData.LimitedDurationDocumentIndicator) ? View.VISIBLE : View.GONE);
        tvLimitedDurationDocumentIndicator.setText(barcodeData.LimitedDurationDocumentIndicator);
        llFamilyNameTruncation.setVisibility(!TextUtils.isEmpty(barcodeData.FamilyNameTruncation) ? View.VISIBLE : View.GONE);
        tvFamilyNameTruncation.setText(barcodeData.FamilyNameTruncation);
        llFirstNamesTruncation.setVisibility(!TextUtils.isEmpty(barcodeData.FirstNamesTruncation) ? View.VISIBLE : View.GONE);
        tvFirstNamesTruncation.setText(barcodeData.FirstNamesTruncation);
        llMiddleNamesTruncation.setVisibility(!TextUtils.isEmpty(barcodeData.MiddleNamesTruncation) ? View.VISIBLE : View.GONE);
        tvMiddleNamesTruncation.setText(barcodeData.MiddleNamesTruncation);
        llOrganDonorIndicator.setVisibility(!TextUtils.isEmpty(barcodeData.OrganDonorIndicator) ? View.VISIBLE : View.GONE);
        tvOrganDonorIndicator.setText(barcodeData.OrganDonorIndicator);
        llPermitIdentifier.setVisibility(!TextUtils.isEmpty(barcodeData.PermitIdentifier) ? View.VISIBLE : View.GONE);
        tvPermitIdentifier.setText(barcodeData.PermitIdentifier);
        llAuditInformation.setVisibility(!TextUtils.isEmpty(barcodeData.AuditInformation) ? View.VISIBLE : View.GONE);
        tvAuditInformation.setText(barcodeData.AuditInformation);
        llJurisdictionSpecific.setVisibility(!TextUtils.isEmpty(barcodeData.JurisdictionSpecific) ? View.VISIBLE : View.GONE);
        tvJurisdictionSpecific.setText(barcodeData.JurisdictionSpecific);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSave:
                overridePendingTransition(0, 0);
                setResult(RESULT_OK);
                BarcodeResultActivity.this.finish();
                break;

            case R.id.tvUSADLResult:
                llFormatting.setVisibility(View.VISIBLE);
                tvWholeData.setVisibility(View.GONE);
                break;

            case R.id.tvPDF417Barcode:
                llFormatting.setVisibility(View.GONE);
                tvWholeData.setVisibility(View.VISIBLE);
                break;
        }
    }
}
