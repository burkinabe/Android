package com.accurascandemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.accurascandemo.api.GenerateRequest;
import com.accurascandemo.api.HandleResponse;
import com.accurascandemo.api.RequestTask;
import com.accurascandemo.util.AlertDialogAbstract;
import com.accurascandemo.util.ParsedResponse;
import com.accurascandemo.util.Utils;

import java.util.ArrayList;

/**
 * Created by richa on 11/5/17.
 */

public class ContactUsActivity extends BaseActivity implements View.OnClickListener {

    private EditText etCompanyName, etName, etEmailId, etCell, etMessage;
    private TextView etCountry;
    private PopupWindow countryWindow;
    private ArrayList<String> countryArray;
    private boolean flag;

    @Override
    protected void onResume() {
        super.onResume();
        AccuraDemoApplication.getInstance().reportToGoogleAnalytics(getString(R.string.contact_screen));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        countryArray = new ArrayList<>();
        countryArray.add("Afghanistan");
        countryArray.add("Albania");
        countryArray.add("Algeria");
        countryArray.add("Andorra");
        countryArray.add("Angola");
        countryArray.add("Antigua and Barbuda");
        countryArray.add("Argentina");
        countryArray.add("Armenia");
        countryArray.add("Aruba");
        countryArray.add("Australia");
        countryArray.add("Austria");
        countryArray.add("Azerbaijan");
        countryArray.add("Bahamas");
        countryArray.add("Bahrain");
        countryArray.add("Bangladesh");
        countryArray.add("Barbados");
        countryArray.add("Belarus");
        countryArray.add("Belgium");
        countryArray.add("Belize");
        countryArray.add("Benin");
        countryArray.add("Bhutan");
        countryArray.add("Bolivia");
        countryArray.add("Bosnia and Herzegovina");
        countryArray.add("Botswana");
        countryArray.add("Brazil");
        countryArray.add("Brunei");
        countryArray.add("Bulgaria");
        countryArray.add("Burkina Faso");
        countryArray.add("Burma");
        countryArray.add("Burundi");
        countryArray.add("Cambodia");
        countryArray.add("Cameroon");
        countryArray.add("Canada");
        countryArray.add("Cabo Verde");
        countryArray.add("Central African Republic");
        countryArray.add("Chad");
        countryArray.add("Chile");
        countryArray.add("China");
        countryArray.add("Colombia");
        countryArray.add("Comoros");
        countryArray.add("Congo, Democratic Republic of the");
        countryArray.add("Congo, Republic of the");
        countryArray.add("Costa Rica");
        countryArray.add("Cote d'Ivoire");
        countryArray.add("Croatia");
        countryArray.add("Cuba");
        countryArray.add("Curacao");
        countryArray.add("Cyprus");
        countryArray.add("Czechia");
        countryArray.add("Czech Republic");
        countryArray.add("Denmark");
        countryArray.add("Djibouti");
        countryArray.add("Dominica");
        countryArray.add("Democratic Republic of Congo");
        countryArray.add("East Timor (see Timor-Leste)");
        countryArray.add("Ecuador");
        countryArray.add("Egypt");
        countryArray.add("El Salvador");
        countryArray.add("Equatorial Guinea");
        countryArray.add("Eritrea");
        countryArray.add("Estonia");
        countryArray.add("Ethiopia");
        countryArray.add("Fiji");
        countryArray.add("Finland");
        countryArray.add("France");
        countryArray.add("Gabon");
        countryArray.add("Gambia");
        countryArray.add("The");
        countryArray.add("Georgia");
        countryArray.add("Germany");
        countryArray.add("Ghana");
        countryArray.add("Greece");
        countryArray.add("Grenada");
        countryArray.add("Guatemala");
        countryArray.add("Guinea");
        countryArray.add("Guinea-Bissau");
        countryArray.add("Guyana");
        countryArray.add("Haiti");
        countryArray.add("Holy See");
        countryArray.add("Honduras");
        countryArray.add("Hong Kong");
        countryArray.add("Hungary");
        countryArray.add("Iceland");
        countryArray.add("India");
        countryArray.add("Indonesia");
        countryArray.add("Iran");
        countryArray.add("Iraq");
        countryArray.add("Ireland");
        countryArray.add("Israel");
        countryArray.add("Italy");
        countryArray.add("Jamaica");
        countryArray.add("Japan");
        countryArray.add("Jordan");
        countryArray.add("Kazakhstan");
        countryArray.add("Kenya");
        countryArray.add("Kiribati");
        countryArray.add("Korea, North");
        countryArray.add("Korea, South");
        countryArray.add("Kosovo");
        countryArray.add("Kuwait");
        countryArray.add("Kyrgyzstan");
        countryArray.add("Laos");
        countryArray.add("Latvi");
        countryArray.add("Lebanon");
        countryArray.add("Lesotho");
        countryArray.add("Liberia");
        countryArray.add("Libya");
        countryArray.add("Liechtenstein");
        countryArray.add("Lithuania");
        countryArray.add("Luxembourg");
        countryArray.add("Macau");
        countryArray.add("Macedonia");
        countryArray.add("Madagascar");
        countryArray.add("Malawi");
        countryArray.add("Malaysia");
        countryArray.add("Maldives");
        countryArray.add("Mali");
        countryArray.add("Malta");
        countryArray.add("Marshall Islands");
        countryArray.add("Mauritania");
        countryArray.add("Mauritius");
        countryArray.add("Mexico");
        countryArray.add("Micronesia");
        countryArray.add("Monaco");
        countryArray.add("Mongolia");
        countryArray.add("Montenegro");
        countryArray.add("Morocco");
        countryArray.add("Mozambique");
        countryArray.add("Namibia");
        countryArray.add("Nauru");
        countryArray.add("Nepal");
        countryArray.add("Netherlands");
        countryArray.add("New Zealand");
        countryArray.add("Nicaragua");
        countryArray.add("Niger");
        countryArray.add("Nigeria");
        countryArray.add("North Korea");
        countryArray.add("Norway");
        countryArray.add("Oman");
        countryArray.add("Pakistan");
        countryArray.add("Palau");
        countryArray.add("Palestinian Territories");
        countryArray.add("Panama");
        countryArray.add("Papua New Guinea");
        countryArray.add("Paraguay");
        countryArray.add("Peru");
        countryArray.add("Philippines");
        countryArray.add("Poland");
        countryArray.add("Portugal");
        countryArray.add("Qatar");
        countryArray.add("Romania");
        countryArray.add("Russia");
        countryArray.add("Rwanda");
        countryArray.add("Saint Kitts and Nevis");
        countryArray.add("Saint Lucia");
        countryArray.add("Saint Vincent and the Grenadines");
        countryArray.add("Samoa");
        countryArray.add("San Marino");
        countryArray.add("Sao Tome and Principe");
        countryArray.add("Saudi Arabia");
        countryArray.add("Senegal");
        countryArray.add("Serbia");
        countryArray.add("Seychelles");
        countryArray.add("Sierra Leone");
        countryArray.add("Singapore");
        countryArray.add("Sint Maarten");
        countryArray.add("Slovakia");
        countryArray.add("Slovenia");
        countryArray.add("Solomon Islands");
        countryArray.add("Somalia");
        countryArray.add("South Africa");
        countryArray.add("South Korea");
        countryArray.add("South Sudan");
        countryArray.add("Spain");
        countryArray.add("Sri Lanka");
        countryArray.add("Sudan");
        countryArray.add("Suriname");
        countryArray.add("Swaziland");
        countryArray.add("Sweden");
        countryArray.add("Syria");
        countryArray.add("Taiwan");
        countryArray.add("Tajikistan");
        countryArray.add("Tanzania");
        countryArray.add("Thailand");
        countryArray.add("Timor-Leste");
        countryArray.add("Togo");
        countryArray.add("Tonga");
        countryArray.add("Trinidad and Tobago");
        countryArray.add("Tunisia");
        countryArray.add("Turkey");
        countryArray.add("Turkmenistan");
        countryArray.add("Tuvalu");
        countryArray.add("Uganda");
        countryArray.add("Ukraine");
        countryArray.add("United Arab Emirates");
        countryArray.add("United Kingdom");
        countryArray.add("Uruguay");
        countryArray.add("Uzbekistan");
        countryArray.add("Vanuatu");
        countryArray.add("Venezuela");
        countryArray.add("Vietnam");
        countryArray.add("Yemen");
        countryArray.add("Zambia");
        countryArray.add("Zimbabwe");
        countryArray.add("Austria");
        countryArray.add("Afghanistan");
        countryArray.add("Brazil");
        countryArray.add("Belgium");
        countryArray.add("Cambodia");
        countryArray.add("China");
        countryArray.add("Colombia");
        countryArray.add("Canada");
        countryArray.add("Denmark");
        countryArray.add("Dominica");
        countryArray.add("Estonia");
        countryArray.add("Ethiopia");
        countryArray.add("France");
        countryArray.add("Germany");
        countryArray.add("Gabon");
        countryArray.add("Iceland");
        countryArray.add("India");
        countryArray.add("Iran");
        countryArray.add("Iraq");
        countryArray.add("Ireland");
        countryArray.add("Japan");
        countryArray.add("Kuwait");
        countryArray.add("Malaysia");
        countryArray.add("Nepal");
        countryArray.add("Netherlands");
        countryArray.add("New Zealand");
        countryArray.add("Norway");
        countryArray.add("Oman");
        countryArray.add("Pakistan");
        countryArray.add("Peru");
        countryArray.add("Qatar");
        countryArray.add("Russia");
        countryArray.add("Singapore");
        countryArray.add("Switzerland");
        countryArray.add("Turkey");
        countryArray.add("United States of America (USA)");
        countryArray.add("United Kingdom (UK)");
        countryArray.add("Venezuela");
        countryArray.add("Yemen");
        countryArray.add("Zambia");
        countryArray.add("Zimbabwe");

        etCompanyName = (EditText) findViewById(R.id.etCompanyName);
        etName = (EditText) findViewById(R.id.etName);
        etEmailId = (EditText) findViewById(R.id.etEmailId);
        etCell = (EditText) findViewById(R.id.etCell);
        etCountry = (TextView) findViewById(R.id.etCountry);
        etMessage = (EditText) findViewById(R.id.etMessage);

        findViewById(R.id.ivBack).setOnClickListener(this);

        etCountry.setOnClickListener(this);
        final TextView tvSend = (TextView) findViewById(R.id.tvSend);
        tvSend.setOnClickListener(this);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    scrollView.smoothScrollTo(0, (int) tvSend.getY());
                }
            }
        });
    }

    private void showCountryPopUp() {

        LayoutInflater layoutInflater = (LayoutInflater)
                getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dropdown_popup, null);
        countryWindow = new PopupWindow(view);

        int width = etCountry.getWidth();
        if (width > 0) {
            countryWindow.setWidth(width);
        } else {
            countryWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        }
        countryWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        countryWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        countryWindow.setOutsideTouchable(true);
        countryWindow.setTouchable(true);
        countryWindow.setFocusable(true);

        StringDropDownAdapter stringDropDownAdapter = new StringDropDownAdapter(this, countryArray);

        final ListView ageList = (ListView) view.findViewById(R.id.listView);
        ageList.setAdapter(stringDropDownAdapter);

        ageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etCountry.setText(countryArray.get(position));
                etCountry.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                countryWindow.dismiss();
            }
        });

        countryWindow.showAsDropDown(etCountry, 0, 2);

    }

    private boolean isValid() {
        if (TextUtils.isEmpty(etCompanyName.getText().toString().trim())) {
            new AlertDialogAbstract(ContactUsActivity.this, "Please enter Company Name", getString(R.string.ok), "") {
                @Override
                public void positive_negativeButtonClick(int pos_neg_id) {
                    etCompanyName.requestFocus();
                }
            };
            return false;
        } else if (TextUtils.isEmpty(etName.getText().toString().trim())) {
            new AlertDialogAbstract(ContactUsActivity.this, "Please enter Name", getString(R.string.ok), "") {
                @Override
                public void positive_negativeButtonClick(int pos_neg_id) {
                    etName.requestFocus();
                }
            };
            return false;
        } else if (TextUtils.isEmpty(etEmailId.getText().toString().trim())) {
            new AlertDialogAbstract(ContactUsActivity.this, "Please enter Email ID", getString(R.string.ok), "") {
                @Override
                public void positive_negativeButtonClick(int pos_neg_id) {
                    etEmailId.requestFocus();
                }
            };
            return false;
        } else if (!Utils.isValidEmail(etEmailId.getText().toString().trim())) {
            new AlertDialogAbstract(ContactUsActivity.this, "Please enter valid Email ID", getString(R.string.ok), "") {
                @Override
                public void positive_negativeButtonClick(int pos_neg_id) {
                    etEmailId.requestFocus();
                }
            };
            return false;
        } else if (TextUtils.isEmpty(etCell.getText().toString().trim())) {
            new AlertDialogAbstract(ContactUsActivity.this, "Please enter Mobile Number", getString(R.string.ok), "") {
                @Override
                public void positive_negativeButtonClick(int pos_neg_id) {
                    etCell.requestFocus();
                }
            };
            return false;
        } else if (TextUtils.isEmpty(etCountry.getText().toString().trim())) {
            new AlertDialogAbstract(ContactUsActivity.this, "Please enter Country", getString(R.string.ok), "") {
                @Override
                public void positive_negativeButtonClick(int pos_neg_id) {
                    etCountry.requestFocus();
                }
            };
            return false;
        } else if (TextUtils.isEmpty(etMessage.getText().toString().trim())) {
            new AlertDialogAbstract(ContactUsActivity.this, "Please enter message", getString(R.string.ok), "") {
                @Override
                public void positive_negativeButtonClick(int pos_neg_id) {
                    etMessage.requestFocus();
                }
            };
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvSend:
                if (isValid()) {
                    showProgressDialog();
                    new RequestTask(ContactUsActivity.this, getString(R.string.header_default_key),
                            GenerateRequest.requestContactUs(etCompanyName.getText().toString().trim(),
                                    etName.getText().toString().trim(),
                                    etEmailId.getText().toString(),
                                    etCell.getText().toString().trim(),
                                    etCountry.getText().toString().trim(),
                                    etMessage.getText().toString()), getString(R.string.api_contact_us), false) {

                        @Override
                        protected void onPostExecute(String response) {
                            super.onPostExecute(response);
                            dismissProgressDialog();

                            ParsedResponse p = HandleResponse.responseContactUs(ContactUsActivity.this, response);
                            if (!p.error) {
                                String msg = (String) p.o;
                                new AlertDialogAbstract(ContactUsActivity.this, msg + "", getString(R.string.ok), "") {
                                    @Override
                                    public void positive_negativeButtonClick(int pos_neg_id) {
                                        startActivity(new Intent(ContactUsActivity.this, ScanNowPassportImageActivity.class));
                                        overridePendingTransition(0, 0);
                                    }
                                };
                            }
                        }
                    }.execute();
                }
                break;

            case R.id.etCountry:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etCountry.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                showCountryPopUp();
                break;

            case R.id.ivBack:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
