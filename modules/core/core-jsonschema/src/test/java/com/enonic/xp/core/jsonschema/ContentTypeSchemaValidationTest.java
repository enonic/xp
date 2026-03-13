package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "content-type.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/content-type/valid-minimal.yml" ) ).isEmpty();
    }

    @Test
    void fullDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/content-type/valid-full.yml" ) ).isEmpty();
    }

    @Test
    void abstractMustBeBoolean()
    {
        assertThat( validateYaml( schema, "fixtures/content-type/invalid-abstract-not-boolean.yml" ) ).isNotEmpty();
    }

    @Test
    void formInputMissingNameIsInvalid()
    {
        assertThat( validateYaml( schema, "fixtures/content-type/invalid-form-input-missing-name.yml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/content-type/invalid-unknown-property.yml" ) ).isNotEmpty();
    }

    @Test
    void formWithAllInputTypesIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/content-type/valid-all-inputs.yml" ) ).isEmpty();
    }
}
