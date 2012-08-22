package com.enonic.wem.core.content.type.configitem;

import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.Field.newField;
import static com.enonic.wem.core.content.type.configitem.FieldSetTemplateBuilder.newFieldSetTemplate;
import static com.enonic.wem.core.content.type.configitem.FieldTemplateBuilder.newFieldTemplate;
import static com.enonic.wem.core.content.type.configitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;

public class FieldSetTemplateTest
{

    @Test
    public void adding_a_fieldSetTemplate_to_another_fieldSetTemplate_throws_exception()
    {
        Module module = newModule().name( "myModule" ).build();

        FieldTemplate ageTemplate =
            newFieldTemplate().module( module ).field( newField().name( "age" ).type( FieldTypes.textline ).build() ).build();

        FieldSetTemplate personTemplate = newFieldSetTemplate().module( module ).fieldSet(
            FieldSet.newFieldSet().name( "person" ).add( newField().name( "name" ).type( FieldTypes.textline ).build() ).add(
                newTemplateReference( ageTemplate ).name( "age" ).build() ).build() ).build();

        FieldSetTemplate addressTemplate = newFieldSetTemplate().module( module ).fieldSet(
            FieldSet.newFieldSet().name( "address" ).add( newField().type( FieldTypes.textline ).name( "street" ).build() ).add(
                newField().type( FieldTypes.textline ).name( "postalCode" ).build() ).add(
                newField().type( FieldTypes.textline ).name( "postalPlace" ).build() ).build() ).build();

        try
        {
            personTemplate.addConfigItem( newTemplateReference( addressTemplate ).name( "address" ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A template cannot reference other templates unless it is of type FIELD: FIELD_SET", e.getMessage() );
        }
    }

}
