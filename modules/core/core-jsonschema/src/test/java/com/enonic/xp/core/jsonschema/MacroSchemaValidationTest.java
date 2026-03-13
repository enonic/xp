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
        assertThat( validateYaml( schema, "fixtures/macro/valid-minimal.yml" ) ).isEmpty();
    }

    @Test
    void documentWithFormIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/macro/valid-with-form.yml" ) ).isEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/macro/invalid-unknown-property.yml" ) ).isNotEmpty();
    }

    @Test
    void formSupportsFieldSet()
    {
        assertThat( validateYaml( schema, "fixtures/macro/valid-form-with-fieldset.yml" ) ).isEmpty();
    }

    @Test
    void formDoesNotSupportItemSet()
    {
        assertThat( validateYaml( schema, "fixtures/macro/invalid-form-itemset-not-supported.yml" ) ).isNotEmpty();
    }

    @Test
    void formDoesNotSupportOptionSet()
    {
        assertThat( validateYaml( schema, "fixtures/macro/invalid-form-optionset-not-supported.yml" ) ).isNotEmpty();
    }

    @Test
    void formDoesNotSupportFormFragment()
    {
        assertThat( validateYaml( schema, "fixtures/macro/invalid-form-fragment-not-supported.yml" ) ).isNotEmpty();
    }
}
