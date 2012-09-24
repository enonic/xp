package com.enonic.wem.api.content.type.formitem;

import org.junit.Test;

import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.formitem.Component.newComponent;
import static com.enonic.wem.api.content.type.formitem.ComponentTemplateBuilder.newComponentTemplate;
import static com.enonic.wem.api.content.type.formitem.FormItemSetTemplateBuilder.newFormItemSetTemplate;
import static com.enonic.wem.api.content.type.formitem.TemplateReference.newTemplateReference;
import static org.junit.Assert.*;

public class FormItemSetTemplateTest
{

    @Test
    public void adding_a_fieldSetTemplate_to_another_fieldSetTemplate_throws_exception()
    {
        Module module = Module.newModule().name( "myModule" ).build();

        ComponentTemplate ageTemplate = newComponentTemplate().module( module ).component(
            newComponent().name( "age" ).type( ComponentTypes.TEXT_LINE ).build() ).build();

        FormItemSetTemplate personTemplate = newFormItemSetTemplate().module( module ).formItemSet(
            FormItemSet.newFormItemSet().name( "person" ).add( newComponent().name( "name" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                newTemplateReference( ageTemplate ).name( "age" ).build() ).build() ).build();

        FormItemSetTemplate addressTemplate = newFormItemSetTemplate().module( module ).formItemSet(
            FormItemSet.newFormItemSet().name( "address" ).add(
                newComponent().type( ComponentTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newComponent().type( ComponentTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newComponent().type( ComponentTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

        try
        {
            personTemplate.addFormItem( newTemplateReference( addressTemplate ).name( "address" ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A template cannot reference other templates unless it is of type ComponentTemplate: FormItemSetTemplate",
                          e.getMessage() );
        }
    }

}
