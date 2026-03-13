package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PartSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "part.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/part/valid-minimal.yaml" ) ).isEmpty();
    }

    @Test
    void documentWithFormIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/part/valid-with-form.yaml" ) ).isEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/part/invalid-unknown-property.yaml" ) ).isNotEmpty();
    }
}
