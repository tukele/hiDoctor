package com.example.hidoctor;

import android.os.AsyncTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HL7SEND extends AsyncTask {
    String symptoms;
    String flag;
    String id;
    String message;
    private String URL;
    private final OkHttpClient client = new OkHttpClient();


    public HL7SEND(String URL) {
        this.URL=URL;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String id,String symptoms,String flag) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String data=(String)(formatter.format(date));

        this.message="{\n" +
                "  \"resourceType\" : \"ValueSet\",\n" +
                "  \"id\" : \"condition-category\",\n" +
                "  \"meta\" : {\n" +
                "    \"lastUpdated\" : \""+data+"\",\n" +
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
                "  \"title\" : \""+symptoms+"\",\n" +
                "  \"status\" : \""+flag+"\",\n" +
                "  \"experimental\" : false,\n" +
                "  \"date\" : \""+data+"\",\n" +
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
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("HL7", message)
                .build();
        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
