package com.example.hidoctor;
import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTPost extends AsyncTask {
    //URLS FOR HTTP Posts to WebServer
    private static final String LOGIN_URL ="http://hidoctor.shardslab.com/auth";
    private static final String CALL_URL = "http://hidoctor.shardslab.com/Api/getCallCode";
    private static final String DOCTOR_URL = "http://hidoctor.shardslab.com/Api/getDoctorPerPatient";
    private static final String HL7_URL = "http://hidoctor.shardslab.com/Api/getDoctorPerPatient";

    //OKHTTPCLIENT
    private final OkHttpClient client = new OkHttpClient();
    //DATA POST: REQUESTBODY AND URL
    Object[] data= new Object[2];
    //RESPONSE OF HTTP POST MADE BY WEBSERVER
    String responseHTTP;

    //Send HTTP POST to Web Server. If credentials are correct user will login
    public Boolean loginHTTP(String email,String password){
                 RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .build();
                 data[0]=requestBody;
                 data[1]= LOGIN_URL;
                 try {
                       this.execute().get();
                       if(!(responseHTTP.equals("no"))){
                           JSONArray json=new JSONArray(responseHTTP);
                           User user=new User();
                           user.setId((String) json.getJSONObject(0).getString("id"));
                           user.setNome((String) json.getJSONObject(0).getString("nome"));
                           user.setCognome((String) json.getJSONObject(0).getString("cognome"));
                           User.currentUser =user;
                           return true;
                       }
                       return false;
                     } catch (Exception e) {
                     e.printStackTrace();
                       return false;
                 }
    }
    //GET CODE FOR WEBCALL WITH DOCTOR
    public String callHTTP(String id){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", id)
                .build();
        data[0]=requestBody;
        data[1]= CALL_URL;
        try {
            this.execute().get();
            return responseHTTP.split(":")[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    //GET DOCTOR NAME
    public String doctorHTTP(String id){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", id)
                .build();
        data[0]=requestBody;
        data[1]= DOCTOR_URL;
        try {
            this.execute().get();
            return responseHTTP.split(":")[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    //SEND HL7 MESSAGE TO WEBSERVER
    public boolean HL7_HTTP(String id,String symptom,String value){
        if(value.equals("")){
            return false;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date_D = new Date();
        String date=(String)(formatter.format(date_D));
        String HL7="{\n" +
                "  \"resourceType\" : \"ValueSet\",\n" +
                "  \"id\" : \"condition-category\",\n" +
                "  \"meta\" : {\n" +
                "    \"lastUpdated\" : \""+date+"\",\n" +
                "    \"profile\" : [\"http://hl7.org/fhir/StructureDefinition/shareablevalueset\"]\n" +
                "  },\n" +
                "  \"text\" : {\n" +
                "    \"status\" : \"generated\",\n" +
                "    \"div\" : \"<div>!-- Snipped for Brevity --></div>\"\n" +
                "  },\n" +
                "  \"extension\" : [{\n" +
                "    \"url\" : \"http://hl7.org/fhir/StructureDefinition/structuredefinition-wg\",\n" +
                "    \"valueCode\" : \"pc\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"url\" : \"http://hl7.org/fhir/StructureDefinition/structuredefinition-standards-status\",\n" +
                "    \"valueCode\" : \"trial-use\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"url\" : \"http://hl7.org/fhir/StructureDefinition/structuredefinition-fmm\",\n" +
                "    \"valueInteger\" : 3\n" +
                "  }],\n" +
                "  \"url\" : \"http://hl7.org/fhir/ValueSet/condition-category\",\n" +
                "  \"identifier\" : [{\n" +
                "    \"system\" : \"urn:ietf:rfc:3986\",\n" +
                "    \"value\" : \"urn:oid:2.16.840.1.113883.4.642.3.162\"\n" +
                "  }],\n" +
                "  \"version\" : \"4.0.1\",\n" +
                "  \"name\" : \"ConditionCategoryCodes\",\n" +
                "  \"title\" : \""+symptom+"\",\n" +
                "  \"status\" : \""+value+"\",\n" +
                "  \"experimental\" : false,\n" +
                "  \"date\" : \""+date+"\",\n" +
                "  \"publisher\" : \"FHIR Project team\",\n" +
                "  \"contact\" : [{\n" +
                "    \"telecom\" : [{\n" +
                "      \"system\" : \"url\",\n" +
                "      \"value\" : \"http://hl7.org/fhir\"\n" +
                "    }]\n" +
                "  }],\n" +
                "  \"description\" : \"Preferred value set for Condition Categories.\",\n" +
                "  \"immutable\" : true,\n" +
                "  \"compose\" : {\n" +
                "    \"include\" : [{\n" +
                "      \"system\" : \"http://terminology.hl7.org/CodeSystem/condition-category\"\n" +
                "    }]\n" +
                "  },\n" +
                " \"subject\": {\n" +
                "    \"reference\": \""+id+"\"\n" +
                "  },"+
                "}";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("HL7", HL7)
                .build();

        data[0]=requestBody;
        data[1]= HL7_URL;

        try {
            this.execute().get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //HTTP POST, ASYNCTASK METHOD
    @Override
    protected Object doInBackground(Object[] objects) {
        Request request = new Request.Builder()
                .url((String) data[1])
                .post((RequestBody) data[0])
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }
            //SAVE RESPONSE
            responseHTTP=response.body().string();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
