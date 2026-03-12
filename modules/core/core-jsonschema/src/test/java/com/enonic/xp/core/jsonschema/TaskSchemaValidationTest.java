package com.enonic.xp.core.jsonschema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.networknt.schema.Schema;

import static org.assertj.core.api.Assertions.assertThat;

class TaskSchemaValidationTest
    extends AbstractSchemaValidationTest
{
    private static Schema schema;

    @BeforeAll
    static void initSchema()
    {
        schema = schemaFor( "task.schema.json" );
    }

    @Test
    void emptyDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/task/valid-minimal.yaml" ) ).isEmpty();
    }

    @Test
    void fullDocumentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/task/valid-full.yaml" ) ).isEmpty();
    }

    @Test
    void descriptionMustBeString()
    {
        assertThat( validateYaml( schema, "fixtures/task/invalid-description-not-string.yaml" ) ).isNotEmpty();
    }

    @Test
    void additionalPropertiesAreNotAllowed()
    {
        assertThat( validateYaml( schema, "fixtures/task/invalid-unknown-property.yaml" ) ).isNotEmpty();
    }

    @Test
    void formSupportsFieldSet()
    {
        assertThat( validateYaml( schema, "fixtures/task/valid-form-with-fieldset.yaml" ) ).isEmpty();
    }

    @Test
    void formSupportsItemSet()
    {
        assertThat( validateYaml( schema, "fixtures/task/valid-form-with-itemset.yaml" ) ).isEmpty();
    }

    @Test
    void formSupportsOptionSet()
    {
        assertThat( validateYaml( schema, "fixtures/task/valid-form-with-optionset.yaml" ) ).isEmpty();
    }

    @Test
    void formDoesNotSupportFormFragment()
    {
        assertThat( validateYaml( schema, "fixtures/task/invalid-form-fragment-not-supported.yaml" ) ).isNotEmpty();
    }
}
