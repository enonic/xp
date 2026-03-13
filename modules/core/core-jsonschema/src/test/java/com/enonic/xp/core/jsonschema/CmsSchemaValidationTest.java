package com.enonic.xp.core.jsonschema;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.networknt.schema.Schema;

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

    @Test
    void formWithInputIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/cms/valid-with-form.yml" ) ).isEmpty();
    }

    @Test
    void formWithFieldSetIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/cms/valid-form-with-fieldset.yml" ) ).isEmpty();
    }

    @Test
    void formWithItemSetIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/cms/valid-form-with-itemset.yml" ) ).isEmpty();
    }

    @Test
    void formWithOptionSetIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/cms/valid-form-with-optionset.yml" ) ).isEmpty();
    }

    @Test
    void formWithFragmentIsValid()
    {
        assertThat( validateYaml( schema, "fixtures/cms/valid-form-with-fragment.yml" ) ).isEmpty();
    }

    @Test
    void formInputMissingNameIsInvalid()
    {
        assertThat( validateYaml( schema, "fixtures/cms/invalid-form-input-missing-name.yml" ) ).isNotEmpty();
    }

    @Test
    void formInputMissingLabelIsInvalid()
    {
        assertThat( validateYaml( schema, "fixtures/cms/invalid-form-input-missing-label.yml" ) ).isNotEmpty();
    }

    @Test
    void formInputUnknownPropertyIsInvalid()
    {
        assertThat( validateYaml( schema, "fixtures/cms/invalid-form-input-unknown-property.yml" ) ).isNotEmpty();
    }
}
