package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "page.schema.json" );
    }

    @Test
    void documentWithRegionsIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/page/valid-with-regions.yaml" ) ).isEmpty();
    }

    @Test
    void regionsIsRequired()
    {
        assertThat( validateYaml( schema, "fixtures/page/invalid-missing-regions.yaml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/page/invalid-unknown-property.yaml" ) ).isNotEmpty();
    }
}
