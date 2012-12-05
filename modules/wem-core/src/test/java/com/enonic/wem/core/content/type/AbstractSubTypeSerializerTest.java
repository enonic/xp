package com.enonic.wem.core.content.type;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.FormItemSetSubType;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputSubType;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.AbstractSerializerTest;


public abstract class AbstractSubTypeSerializerTest
    extends AbstractSerializerTest
{
    private static final ModuleName myModule = ModuleName.from( "myModule" );

    private SubTypeSerializer serializer;

    abstract SubTypeSerializer getSerializer();


    @Before
    public void before()
    {
        this.serializer = getSerializer();
    }

    abstract void assertSerializedResult( String fileNameForExpected, String actualSerialization );

    @Test
    public void generate_InputSubType()
        throws Exception
    {
        Input.Builder inputBuilder = Input.newInput();
        inputBuilder.name( "myInput" );
        inputBuilder.label( "My input" );
        inputBuilder.type( InputTypes.TEXT_LINE );
        inputBuilder.immutable( true );
        inputBuilder.minimumOccurrences( 1 );
        inputBuilder.maximumOccurrences( 3 );
        inputBuilder.helpText( "Help text" );
        inputBuilder.customText( "Custom text" );
        inputBuilder.indexed( true );
        Input myInput = inputBuilder.build();
        InputSubType myInputSubType =
            InputSubType.newInputSubType().displayName( "My SubType" ).module( myModule ).input( myInput ).build();

        // exercise
        String serialized = toString( myInputSubType );

        assertSerializedResult( "subType-input", serialized );
    }

    @Test
    public void generate_FormItemSetSubType()
        throws Exception
    {
        Input.Builder inputBuilder = Input.newInput();
        inputBuilder.name( "myInput" );
        inputBuilder.label( "My input" );
        inputBuilder.type( InputTypes.TEXT_LINE );
        Input myInput = inputBuilder.build();

        FormItemSet.Builder formItemSetBuilder = FormItemSet.newFormItemSet();
        formItemSetBuilder.name( "mySet" );
        formItemSetBuilder.label( "My set" );
        formItemSetBuilder.minimumOccurrences( 1 );
        formItemSetBuilder.maximumOccurrences( 3 );
        formItemSetBuilder.helpText( "Help text" );
        formItemSetBuilder.customText( "Custom text" );
        formItemSetBuilder.add( myInput );
        FormItemSet myFormItemSet = formItemSetBuilder.build();

        FormItemSetSubType myFormItemSetSubType =
            FormItemSetSubType.newFormItemSetSubType().displayName( "My SubType" ).module( myModule ).formItemSet( myFormItemSet ).build();

        // exercise
        String serialized = toString( myFormItemSetSubType );

        assertSerializedResult( "subType-formItemSet", serialized );
    }


    private SubType toSubType( final String serialized )
    {
        return serializer.toSubType( serialized );
    }

    private String toString( final SubType type )
    {
        String serialized = getSerializer().toString( type );
        System.out.println( "SubType:" );
        System.out.println( serialized );
        return serialized;
    }


}
