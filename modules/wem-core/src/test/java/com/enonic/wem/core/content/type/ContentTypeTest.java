package com.enonic.wem.core.content.type;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplate;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplateBuilder;
import com.enonic.wem.core.content.type.configitem.FieldTemplate;
import com.enonic.wem.core.content.type.configitem.FieldTemplateBuilder;
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

        FieldSetTemplate template = FieldSetTemplateBuilder.create().name( "address" ).module( module ).build();

        template.addField( newField().name( "label" ).label( "Label" ).type( FieldTypes.textline ).build() );
        template.addField( newField().name( "street" ).label( "Street" ).type( FieldTypes.textline ).build() );
        template.addField( newField().name( "postalNo" ).label( "Postal No" ).type( FieldTypes.textline ).build() );
        template.addField( newField().name( "country" ).label( "Country" ).type( FieldTypes.textline ).build() );

        ContentType cty = new ContentType();
        cty.addConfigItem( newTemplateReference( template ).name( "home" ).build() );
        cty.addConfigItem( newTemplateReference( template ).name( "cabin" ).build() );

        MockTemplateReferenceFetcher templateReferenceFetcher = new MockTemplateReferenceFetcher();
        templateReferenceFetcher.add( template );

        // exercise
        cty.templateReferencesToConfigItems( templateReferenceFetcher );

        // verify:
        assertNotNull( cty.getField( "home.street" ) );
        assertNotNull( cty.getField( "cabin.street" ) );
    }

    @Test
    public void fieldSetTemplate_cannot_reference_other_fieldSetTemplates()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        FieldTemplate ageTemplate = FieldTemplateBuilder.create().module( module ).name( "age" ).field(
            newField().name( "age" ).type( FieldTypes.textline ).build() ).build();

        FieldSetTemplate personTemplate = FieldSetTemplateBuilder.create().name( "person" ).module( module ).build();
        personTemplate.addField( newField().name( "name" ).type( FieldTypes.textline ).build() );
        personTemplate.addTemplateReference( newTemplateReference( ageTemplate ).name( "age" ).build() );

        FieldSetTemplate addressTemplate = FieldSetTemplateBuilder.create().name( "address" ).module( module ).build();
        addressTemplate.addField( newField().name( "label" ).type( FieldTypes.textline ).build() );
        addressTemplate.addField( newField().name( "street" ).type( FieldTypes.textline ).build() );
        addressTemplate.addField( newField().name( "postalNo" ).type( FieldTypes.textline ).build() );
        addressTemplate.addField( newField().name( "country" ).type( FieldTypes.textline ).build() );

        personTemplate.addConfigItem( newTemplateReference( addressTemplate ).name( "address" ).build() );

        ContentType cty = new ContentType();
        cty.addConfigItem( newField().name( "id" ).type( FieldTypes.textline ).build() );
        cty.addConfigItem( newTemplateReference( personTemplate ).name( "person" ).build() );

        MockTemplateReferenceFetcher templateReferenceFetcher = new MockTemplateReferenceFetcher();
        templateReferenceFetcher.add( ageTemplate );
        templateReferenceFetcher.add( personTemplate );
        templateReferenceFetcher.add( addressTemplate );

        // exercise
        cty.templateReferencesToConfigItems( templateReferenceFetcher );

        // verify:
        assertEquals( "id", cty.getField( "id" ).getPath().toString() );
        assertEquals( "person", cty.getFieldSet( "person" ).getPath().toString() );
        assertEquals( "person.name", cty.getField( "person.name" ).getPath().toString() );
        assertEquals( "person.age", cty.getField( "person.age" ).getPath().toString() );
        assertEquals( "person.address.label", cty.getField( "person.address.label" ).getPath().toString() );
        assertEquals( "person.address.street", cty.getField( "person.address.street" ).getPath().toString() );
    }

    @Test
    public void templateReferencesToConfigItems_throws_exception_when_template_is_not_of_expected_type()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        FieldSetTemplate fieldSetTemplate = newFieldSetTemplate().name( "address" ).module( module ).build();
        fieldSetTemplate.addField( newField().name( "label" ).label( "Label" ).type( FieldTypes.textline ).build() );
        fieldSetTemplate.addField( newField().name( "street" ).label( "Street" ).type( FieldTypes.textline ).build() );

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
