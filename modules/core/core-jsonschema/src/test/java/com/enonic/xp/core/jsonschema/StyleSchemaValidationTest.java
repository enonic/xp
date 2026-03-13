package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StyleSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "style.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/style/valid-minimal.yml" ) ).isEmpty();
    }

    @Test
    void documentWithCssAndImageIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/style/valid-with-image.yml" ) ).isEmpty();
    }

    @Test
    void cssMustBeString()
    {
        assertThat( validateYaml( schema, "fixtures/style/invalid-css-not-string.yml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/style/invalid-unknown-property.yml" ) ).isNotEmpty();
    }
}
