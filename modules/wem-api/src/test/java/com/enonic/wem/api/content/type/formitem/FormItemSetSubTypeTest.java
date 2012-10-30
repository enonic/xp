package com.enonic.wem.api.content.type.formitem;

import org.junit.Test;

import com.enonic.wem.api.content.type.formitem.comptype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.formitem.FormItemSetSubTypeBuilder.newFormItemSetSubType;
import static com.enonic.wem.api.content.type.formitem.Input.newInput;
import static com.enonic.wem.api.content.type.formitem.InputSubType.newInputSubType;
import static com.enonic.wem.api.content.type.formitem.SubTypeReference.newSubTypeReference;
import static org.junit.Assert.*;

public class FormItemSetSubTypeTest
{

    @Test
    public void adding_a_fieldSetSubType_to_another_fieldSetSubType_throws_exception()
    {
        Module module = Module.newModule().name( "myModule" ).build();

        InputSubType ageSubType =
            newInputSubType().module( module ).input( newInput().name( "age" ).type( InputTypes.TEXT_LINE ).build() ).build();

        FormItemSetSubType personSubType = newFormItemSetSubType().module( module ).formItemSet(
            FormItemSet.newFormItemSet().name( "person" ).add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newSubTypeReference( ageSubType ).name( "age" ).build() ).build() ).build();

        FormItemSetSubType addressSubType = newFormItemSetSubType().module( module ).formItemSet(
            FormItemSet.newFormItemSet().name( "address" ).add( newInput().type( InputTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

        try
        {
            personSubType.addFormItem( newSubTypeReference( addressSubType ).name( "address" ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A SubType cannot reference other SubTypes unless it is of type InputSubType: FormItemSetSubType",
                          e.getMessage() );
        }
    }

}
