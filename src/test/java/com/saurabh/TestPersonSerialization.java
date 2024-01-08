package com.saurabh;

import com.saurabh.model.Person;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestPersonSerialization {

    @Test
    public void givenObjectSerializedThenTrueReturned() throws Exception {
        Person person = new Person("soufiane", "cheouati", "34");
        ObjectToJsonConverter serializer = new ObjectToJsonConverter();
        String jsonString = serializer.convertToJson(person);
        assertEquals(
                "{\"personAge\":\"34\",\"firstName\":\"Soufiane\",\"lastName\":\"Cheouati\"}",
                jsonString);
    }
}
