package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MixinSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "mixin.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/mixin/valid-minimal.yaml" ) ).isEmpty();
    }

    @Test
    void documentWithFormIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/mixin/valid-with-form.yaml" ) ).isEmpty();
    }

    @Test
    void formInputMissingLabelIsInvalid()
    {
        assertThat( validateYaml( schema, "fixtures/mixin/invalid-form-input-missing-label.yaml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/mixin/invalid-unknown-property.yaml" ) ).isNotEmpty();
    }
}
