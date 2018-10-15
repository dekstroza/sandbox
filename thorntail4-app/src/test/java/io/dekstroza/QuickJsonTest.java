package io.dekstroza;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dekstroza.domain.Band;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.UUID;

@RunWith(JUnit4.class)
public class QuickJsonTest {

    @Test
    public void testGameSerialization() throws Exception {
        Band b = new Band();
        b.setId(UUID.randomUUID().toString());
        b.setBandName("Terrible tech");
        b.setMembers("Borisa,Sneza,Daca");
        System.out.println(new ObjectMapper().writeValueAsString(b));

    }
}
