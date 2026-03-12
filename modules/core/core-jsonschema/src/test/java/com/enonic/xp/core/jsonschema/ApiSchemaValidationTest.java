package com.enonic.xp.core.jsonschema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.networknt.schema.Schema;

import static org.assertj.core.api.Assertions.assertThat;

class ApiSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "api.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/api/valid-minimal.yaml" ) ).isEmpty();
    }

    @Test
    void fullDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/api/valid-full.yaml" ) ).isEmpty();
    }

    @Test
    void mountMustBeKnownValue()
    {
        assertThat( validateYaml( schema, "fixtures/api/invalid-mount-unknown-value.yaml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/api/invalid-unknown-property.yaml" ) ).isNotEmpty();
    }
}