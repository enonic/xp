package com.enonic.wem.core.content.type;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.TemplateReference;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.module.Module;

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
        Module module = new Module();
        module.setName( "myModule" );

        FieldSetTemplate template = FieldSetTemplateBuilder.create().name( "address" ).module( module ).build();

        template.addField( Field.newBuilder().name( "label" ).label( "Label" ).type( FieldTypes.textline ).build() );
        template.addField( Field.newBuilder().name( "street" ).label( "Street" ).type( FieldTypes.textline ).build() );
        template.addField( Field.newBuilder().name( "postalNo" ).label( "Postal No" ).type( FieldTypes.textline ).build() );
        template.addField( Field.newBuilder().name( "country" ).label( "Country" ).type( FieldTypes.textline ).build() );

        ContentType cty = new ContentType();
        cty.addConfigItem( TemplateReference.newBuilder().name( "home" ).template( template.getTemplateQualifiedName() ).build() );
        cty.addConfigItem( TemplateReference.newBuilder().name( "cabin" ).template( template.getTemplateQualifiedName() ).build() );

        MockTemplateReferenceFetcher templateReferenceFetcher = new MockTemplateReferenceFetcher();
        templateReferenceFetcher.add( template );

        // exercise
        cty.templateReferencesToConfigItems( templateReferenceFetcher );

        // verify:
        assertNotNull( cty.getField( "home.street" ) );
        assertNotNull( cty.getField( "cabin.street" ) );
    }
}
