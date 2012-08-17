package com.enonic.wem.core.content.type;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplate;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplateBuilder;
import com.enonic.wem.core.content.type.configitem.MockTemplateReferenceFetcher;
import com.enonic.wem.core.content.type.configitem.TemplateReference;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.Field.newField;
import static com.enonic.wem.core.content.type.configitem.FieldSetTemplateBuilder.newFieldSetTemplate;
import static com.enonic.wem.core.content.type.configitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;

public class ContentTypeTest
{

    @Test
    public void address()
    {
        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "address" ).build();
        fieldSet.addField( Field.newBuilder().name( "label" ).label( "Label" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "street" ).label( "Street" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "postalNo" ).label( "Postal No" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "country" ).label( "Country" ).type( FieldTypes.textline ).build() );

        ContentType contentType = new ContentType();
        contentType.addConfigItem( Field.newBuilder().name( "title" ).type( FieldTypes.textline ).build() );
        contentType.addConfigItem( fieldSet );

        String json = ContentTypeSerializerJson.toJson( contentType );
    }

    @Test
    public void templateReferencesToConfigItems()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        FieldSetTemplate template = FieldSetTemplateBuilder.create().module( module ).fieldSet(
            FieldSet.newFieldSet().typeGroup().name( "address" ).addConfigItem(
                newField().name( "label" ).label( "Label" ).type( FieldTypes.textline ).build() ).addConfigItem(
                newField().name( "street" ).label( "Street" ).type( FieldTypes.textline ).build() ).addConfigItem(
                newField().name( "postalNo" ).label( "Postal No" ).type( FieldTypes.textline ).build() ).addConfigItem(
                newField().name( "country" ).label( "Country" ).type( FieldTypes.textline ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addConfigItem( newTemplateReference( template ).name( "home" ).build() );
        cty.addConfigItem( newTemplateReference( template ).name( "cabin" ).build() );

        MockTemplateReferenceFetcher templateReferenceFetcher = new MockTemplateReferenceFetcher();
        templateReferenceFetcher.add( template );

        // exercise
        cty.templateReferencesToConfigItems( templateReferenceFetcher );

        // verify:
        assertEquals( "home.street", cty.getField( "home.street" ).getPath().toString() );
        assertEquals( "cabin.street", cty.getField( "cabin.street" ).getPath().toString() );
    }

    @Test
    public void templateReferencesToConfigItems_throws_exception_when_template_is_not_of_expected_type()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        FieldSetTemplate fieldSetTemplate = newFieldSetTemplate().module( module ).fieldSet(
            FieldSet.newFieldSet().typeGroup().name( "address" ).addConfigItem(
                newField().name( "label" ).label( "Label" ).type( FieldTypes.textline ).build() ).addConfigItem(
                newField().name( "street" ).label( "Street" ).type( FieldTypes.textline ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addConfigItem(
            TemplateReference.newBuilder().name( "home" ).typeField().template( fieldSetTemplate.getTemplateQualifiedName() ).build() );

        MockTemplateReferenceFetcher templateReferenceFetcher = new MockTemplateReferenceFetcher();
        templateReferenceFetcher.add( fieldSetTemplate );

        // exercise
        try
        {
            cty.templateReferencesToConfigItems( templateReferenceFetcher );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Template expected to be of type FIELD: FIELD_SET", e.getMessage() );
        }
    }
}
