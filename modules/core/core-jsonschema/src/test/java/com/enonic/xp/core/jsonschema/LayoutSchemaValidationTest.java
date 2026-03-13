package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LayoutSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "layout.schema.json" );
    }

    @Test
    void documentWithRegionsIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/layout/valid-with-regions.yml" ) ).isEmpty();
    }

    @Test
    void fullDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/layout/valid-full.yml" ) ).isEmpty();
    }

    @Test
    void regionsIsRequired()
    {
        assertThat( validateYaml( schema, "fixtures/layout/invalid-missing-regions.yml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/layout/invalid-unknown-property.yml" ) ).isNotEmpty();
    }
}
