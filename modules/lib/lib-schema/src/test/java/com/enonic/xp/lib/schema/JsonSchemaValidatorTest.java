package com.enonic.xp.lib.schema;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.networknt.schema.ValidationMessage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonSchemaValidatorTest
{
    @Test
    void test()
        throws IOException
    {
        String yaml =
            new String( JsonSchemaValidatorTest.class.getResourceAsStream( "/descriptors/article-content-type.yml" ).readAllBytes(),
                        StandardCharsets.UTF_8 );

        Set<ValidationMessage> errors = JsonSchemaValidator.validate( yaml );

        assertTrue( errors.isEmpty(), "Errors: " + errors );
    }
}
