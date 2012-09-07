package com.enonic.wem.core.content.type.formitem;

import org.junit.Test;

import com.enonic.wem.core.content.type.formitem.fieldtype.FieldTypes;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.formitem.Component.newField;
import static com.enonic.wem.core.content.type.formitem.FieldTemplateBuilder.newFieldTemplate;
import static com.enonic.wem.core.content.type.formitem.FormItemSetTemplateBuilder.newFieldSetTemplate;
import static com.enonic.wem.core.content.type.formitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;

public class FormItemSetTemplateTest
{

    @Test
    public void adding_a_fieldSetTemplate_to_another_fieldSetTemplate_throws_exception()
    {
        Module module = newModule().name( "myModule" ).build();

        ComponentTemplate ageTemplate =
            newFieldTemplate().module( module ).field( newField().name( "age" ).type( FieldTypes.TEXT_LINE ).build() ).build();

        FormItemSetTemplate personTemplate = newFieldSetTemplate().module( module ).fieldSet(
            FormItemSet.newFieldSet().name( "person" ).add( newField().name( "name" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newTemplateReference( ageTemplate ).name( "age" ).build() ).build() ).build();

        FormItemSetTemplate addressTemplate = newFieldSetTemplate().module( module ).fieldSet(
            FormItemSet.newFieldSet().name( "address" ).add( newField().type( FieldTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newField().type( FieldTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newField().type( FieldTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

        try
        {
            personTemplate.addFormItem( newTemplateReference( addressTemplate ).name( "address" ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A template cannot reference other templates unless it is of type FIELD: FIELD_SET", e.getMessage() );
        }
    }

}
