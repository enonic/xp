package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebappSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "webapp.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/webapp/valid-minimal.yaml" ) ).isEmpty();
    }

    @Test
    void documentWithApisIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/webapp/valid-with-apis.yaml" ) ).isEmpty();
    }

    @Test
    void apisItemMustBeString()
    {
        assertThat( validateYaml( schema, "fixtures/webapp/invalid-apis-item-not-string.yaml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/webapp/invalid-unknown-property.yaml" ) ).isNotEmpty();
    }
}
