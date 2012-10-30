package com.enonic.wem.api.content.type.formitem;

import org.junit.Test;

import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.formitem.Component.newComponent;
import static com.enonic.wem.api.content.type.formitem.ComponentSubTypeBuilder.newComponentSubType;
import static com.enonic.wem.api.content.type.formitem.FormItemSetSubTypeBuilder.newFormItemSetSubType;
import static com.enonic.wem.api.content.type.formitem.SubTypeReference.newSubTypeReference;
import static org.junit.Assert.*;

public class FormItemSetSubTypeTest
{

    @Test
    public void adding_a_fieldSetSubType_to_another_fieldSetSubType_throws_exception()
    {
        Module module = Module.newModule().name( "myModule" ).build();

        ComponentSubType ageSubType = newComponentSubType().module( module ).component(
            newComponent().name( "age" ).type( ComponentTypes.TEXT_LINE ).build() ).build();

        FormItemSetSubType personSubType = newFormItemSetSubType().module( module ).formItemSet(
            FormItemSet.newFormItemSet().name( "person" ).add( newComponent().name( "name" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                newSubTypeReference( ageSubType ).name( "age" ).build() ).build() ).build();

        FormItemSetSubType addressSubType = newFormItemSetSubType().module( module ).formItemSet(
            FormItemSet.newFormItemSet().name( "address" ).add(
                newComponent().type( ComponentTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newComponent().type( ComponentTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newComponent().type( ComponentTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

        try
        {
            personSubType.addFormItem( newSubTypeReference( addressSubType ).name( "address" ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A SubType cannot reference other SubTypes unless it is of type ComponentSubType: FormItemSetSubType",
                          e.getMessage() );
        }
    }

}
