package com.enonic.wem.api.content.type.form;

import org.junit.Test;

import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.FormItemSetSubType.newFormItemSetSubType;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.InputSubType.newInputSubType;
import static com.enonic.wem.api.content.type.form.SubTypeReference.newSubTypeReference;
import static org.junit.Assert.*;

public class FormItemSetSubTypeTest
{

    @Test
    public void adding_a_fieldSetSubType_to_another_fieldSetSubType_throws_exception()
    {
        ModuleName module = ModuleName.from( "myModule" );

        InputSubType ageSubType =
            newInputSubType().module( module ).input( newInput().name( "age" ).type( InputTypes.TEXT_LINE ).build() ).build();

        FormItemSetSubType personSubType = newFormItemSetSubType().module( module ).formItemSet(
            newFormItemSet().name( "person" ).add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newSubTypeReference( ageSubType ).name( "age" ).build() ).build() ).build();

        FormItemSetSubType addressSubType = newFormItemSetSubType().module( module ).formItemSet(
            newFormItemSet().name( "address" ).add( newInput().type( InputTypes.TEXT_LINE ).name( "street" ).build() ).add(
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
