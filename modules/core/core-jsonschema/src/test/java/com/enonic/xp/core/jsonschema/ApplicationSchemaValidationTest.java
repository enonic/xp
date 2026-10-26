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
        schema = schemaFor( "application" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/application/valid-minimal.yml" ) ).isEmpty();
    }

    @Test
    void documentWithDescriptionIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/application/valid-with-description.yml" ) ).isEmpty();
    }

    @Test
    void nameIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/application/valid-with-name.yml" ) ).isEmpty();
    }

    @Test
    void nameMustBeValidApplicationName()
    {
        assertThat( validateYaml( schema, "fixtures/application/invalid-name-with-dash.yml" ) ).isNotEmpty();
    }

    @Test
    void descriptionMustBeString()
    {
        assertThat( validateYaml( schema, "fixtures/application/invalid-description-not-string.yml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/application/invalid-unknown-property.yml" ) ).isNotEmpty();
    }

    @Test
    void additionalUnknownPropertyAlongsideValid()
    {
        assertThat( validateYaml( schema, "fixtures/application/invalid-unknown-property-alongside-valid.yml" ) ).isNotEmpty();
    }
}
