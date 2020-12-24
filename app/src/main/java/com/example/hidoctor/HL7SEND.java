package com.example.hidoctor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HL7SEND {
    String symptoms;
    String flag;
    String id;

    public HL7SEND() {
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

    public String HL7Message(String id,String symptoms,String flag){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String data=(String)(formatter.format(date));
        String i= " \"subject\": {\n" +
                "    \"reference\": \"Patient/example\"\n" +
                "  },";
        String conditionPacketFHIR="{\n" +
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
    return conditionPacketFHIR;

    }



}
