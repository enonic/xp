package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExtensionSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "admin-extension" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/admin-extension/valid-minimal.yml" ) ).isEmpty();
    }

    @Test
    void fullDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/admin-extension/valid-full.yml" ) ).isEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/admin-extension/invalid-unknown-property.yml" ) ).isNotEmpty();
    }
}
