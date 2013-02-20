package com.enonic.wem.core.content.schema.mixin;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.AbstractSerializerTest;

import static org.junit.Assert.*;


public abstract class AbstractMixinSerializerTest
    extends AbstractSerializerTest
{
    private static final ModuleName myModule = ModuleName.from( "myModule" );

    private MixinSerializer serializer;

    abstract MixinSerializer getSerializer();


    @Before
    public void before()
    {
        this.serializer = getSerializer();
    }

    abstract void assertSerializedResult( String fileNameForExpected, String actualSerialization );

    @Test
    public void generate_InputMixin()
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
        Mixin myInputMixin = Mixin.newMixin().displayName( "My Mixin" ).module( myModule ).formItem( myInput ).build();

        // exercise
        String serialized = toString( myInputMixin );

        assertSerializedResult( "mixin-input", serialized );
    }

    @Test
    public void generate_FormItemSetMixin()
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

        Mixin myFormItemSetMixin = Mixin.newMixin().displayName( "My Mixin" ).module( myModule ).formItem( myFormItemSet ).build();

        // exercise
        String serialized = toString( myFormItemSetMixin );

        assertSerializedResult( "mixin-formItemSet", serialized );
    }

    @Test
    public void mixin_serialize_parse_serialize_formSet_roundTrip()
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

        Mixin myFormItemSetMixin = Mixin.newMixin().displayName( "My Mixin" ).module( myModule ).formItem( myFormItemSet ).build();

        // exercise
        final String serialized = toString( myFormItemSetMixin );

        // exercise
        final Mixin parsedMixin = toMixin( serialized );
        final String serializedAfterParsing = toString( parsedMixin );

        // verify
        assertEquals( serialized, serializedAfterParsing );
    }

    @Test
    public void mixin_serialize_parse_serialize_input_roundTrip()
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
        Mixin myInputMixin = Mixin.newMixin().displayName( "My Mixin" ).module( myModule ).formItem( myInput ).build();

        // exercise
        final String serialized = toString( myInputMixin );

        // exercise
        final Mixin parsedMixin = toMixin( serialized );
        final String serializedAfterParsing = toString( parsedMixin );

        // verify
        assertEquals( serialized, serializedAfterParsing );
    }

    private Mixin toMixin( final String serialized )
    {
        return serializer.toMixin( serialized );
    }

    private String toString( final Mixin type )
    {
        String serialized = getSerializer().toString( type );
        System.out.println( "Mixin:" );
        System.out.println( serialized );
        return serialized;
    }
}
