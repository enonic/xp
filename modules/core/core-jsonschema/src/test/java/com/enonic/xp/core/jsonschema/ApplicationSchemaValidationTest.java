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
    void typeStaticIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/application/valid-type-static.yml" ) ).isEmpty();
    }

    @Test
    void typeBundleIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/application/valid-type-bundle.yml" ) ).isEmpty();
    }

    @Test
    void typeMustBeKnownValue()
    {
        assertThat( validateYaml( schema, "fixtures/application/invalid-type-unknown.yml" ) ).isNotEmpty();
    }

    @Test
    void typeIsCaseSensitive()
    {
        assertThat( validateYaml( schema, "fixtures/application/invalid-type-lowercase.yml" ) ).isNotEmpty();
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
