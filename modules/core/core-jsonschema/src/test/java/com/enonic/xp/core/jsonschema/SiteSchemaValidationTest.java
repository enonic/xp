package com.enonic.xp.core.jsonschema;

import com.networknt.schema.Schema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SiteSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "site.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/site/valid-minimal.yml" ) ).isEmpty();
    }

    @Test
    void processorWithNameIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/site/valid-with-processor.yml" ) ).isEmpty();
    }

    @Test
    void mappingWithControllerIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/site/valid-mapping-with-controller.yml" ) ).isEmpty();
    }

    @Test
    void mappingWithFilterIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/site/valid-mapping-with-filter.yml" ) ).isEmpty();
    }

    @Test
    void processorMissingNameIsInvalid()
    {
        assertThat( validateYaml( schema, "fixtures/site/invalid-processor-missing-name.yml" ) ).isNotEmpty();
    }

    @Test
    void mappingWithoutControllerOrFilterIsInvalid()
    {
        assertThat( validateYaml( schema, "fixtures/site/invalid-mapping-missing-controller-and-filter.yml" ) ).isNotEmpty();
    }

    @Test
    void mappingWithServiceAndPatternIsInvalid()
    {
        assertThat( validateYaml( schema, "fixtures/site/invalid-mapping-service-with-pattern.yml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/site/invalid-unknown-property.yml" ) ).isNotEmpty();
    }
}
