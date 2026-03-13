package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CmsSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "cms.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/cms/valid-minimal.yml" ) ).isEmpty();
    }

    @Test
    void mixinWithNameIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/cms/valid-with-mixin.yml" ) ).isEmpty();
    }

    @Test
    void mixinMissingNameIsInvalid()
    {
        assertThat( validateYaml( schema, "fixtures/cms/invalid-mixin-missing-name.yml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/cms/invalid-unknown-property.yml" ) ).isNotEmpty();
    }
}
