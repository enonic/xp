package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MacroSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "macro.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/macro/valid-minimal.yaml" ) ).isEmpty();
    }

    @Test
    void documentWithFormIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/macro/valid-with-form.yaml" ) ).isEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/macro/invalid-unknown-property.yaml" ) ).isNotEmpty();
    }

    @Test
    void formSupportsFieldSet()
    {
        assertThat( validateYaml( schema, "fixtures/macro/valid-form-with-fieldset.yaml" ) ).isEmpty();
    }

    @Test
    void formDoesNotSupportItemSet()
    {
        assertThat( validateYaml( schema, "fixtures/macro/invalid-form-itemset-not-supported.yaml" ) ).isNotEmpty();
    }

    @Test
    void formDoesNotSupportOptionSet()
    {
        assertThat( validateYaml( schema, "fixtures/macro/invalid-form-optionset-not-supported.yaml" ) ).isNotEmpty();
    }

    @Test
    void formDoesNotSupportFormFragment()
    {
        assertThat( validateYaml( schema, "fixtures/macro/invalid-form-fragment-not-supported.yaml" ) ).isNotEmpty();
    }
}