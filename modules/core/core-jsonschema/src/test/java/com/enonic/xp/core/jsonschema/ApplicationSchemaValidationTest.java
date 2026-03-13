package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "application.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/application/valid-minimal.yaml" ) ).isEmpty();
    }

    @Test
    void documentWithDescriptionIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/application/valid-with-description.yaml" ) ).isEmpty();
    }

    @Test
    void descriptionMustBeString()
    {
        assertThat( validateYaml( schema, "fixtures/application/invalid-description-not-string.yaml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/application/invalid-unknown-property.yaml" ) ).isNotEmpty();
    }

    @Test
    void additionalUnknownPropertyAlongsideValid()
    {
        assertThat( validateYaml( schema, "fixtures/application/invalid-unknown-property-alongside-valid.yaml" ) ).isNotEmpty();
    }
}
