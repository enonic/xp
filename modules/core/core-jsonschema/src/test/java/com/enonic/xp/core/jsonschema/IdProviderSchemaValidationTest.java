package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdProviderSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "idprovider.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/idprovider/valid-minimal.yml" ) ).isEmpty();
    }

    @Test
    void documentWithFormIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/idprovider/valid-with-form.yml" ) ).isEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/idprovider/invalid-unknown-property.yml" ) ).isNotEmpty();
    }

    @Test
    void formSupportsFieldSet()
    {
        assertThat( validateYaml( schema, "fixtures/idprovider/valid-form-with-fieldset.yml" ) ).isEmpty();
    }

    @Test
    void formSupportsItemSet()
    {
        assertThat( validateYaml( schema, "fixtures/idprovider/valid-form-with-itemset.yml" ) ).isEmpty();
    }

    @Test
    void formSupportsOptionSet()
    {
        assertThat( validateYaml( schema, "fixtures/idprovider/valid-form-with-optionset.yml" ) ).isEmpty();
    }

    @Test
    void formDoesNotSupportFormFragment()
    {
        assertThat( validateYaml( schema, "fixtures/idprovider/invalid-form-fragment-not-supported.yml" ) ).isNotEmpty();
    }
}
