package com.enonic.wem.core.schema.mixin;

import org.junit.Before;
import org.junit.Test;


import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.FormItems;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.AbstractSerializerTest;

import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static org.junit.Assert.*;


public abstract class AbstractMixinSerializerTest
    extends AbstractSerializerTest
{
    private MixinSerializer serializer;

    abstract MixinSerializer getSerializer();


    @Before
    public void before()
    {
        this.serializer = getSerializer();
    }

    abstract void assertSerializedResult( String fileNameForExpected, String actualSerialization );

    abstract String getSerializedString( String fileName );

    @Test
    public void generate_InputMixin()
        throws Exception
    {
        Input.Builder inputBuilder = newInput();
        inputBuilder.name( "my_input" );
        inputBuilder.label( "My input" );
        inputBuilder.inputType( InputTypes.TEXT_LINE );
        inputBuilder.immutable( true );
        inputBuilder.minimumOccurrences( 1 );
        inputBuilder.maximumOccurrences( 3 );
        inputBuilder.helpText( "Help text" );
        inputBuilder.customText( "Custom text" );
        inputBuilder.indexed( true );
        Input myInput = inputBuilder.build();
        Mixin myInputMixin = Mixin.newMixin().name( "my_input" ).displayName( "My Mixin" ).addFormItem( myInput ).build();

        // exercise
        String serialized = toString( myInputMixin );

        assertSerializedResult( "mixin-input", serialized );
    }

    @Test
    public void generate_FormItemSetMixin()
        throws Exception
    {
        Input.Builder inputBuilder = newInput();
        inputBuilder.name( "my_input" );
        inputBuilder.label( "My input" );
        inputBuilder.inputType( InputTypes.TEXT_LINE );
        Input myInput = inputBuilder.build();

        FormItemSet.Builder formItemSetBuilder = FormItemSet.newFormItemSet();
        formItemSetBuilder.name( "my_set" );
        formItemSetBuilder.label( "My set" );
        formItemSetBuilder.minimumOccurrences( 1 );
        formItemSetBuilder.maximumOccurrences( 3 );
        formItemSetBuilder.helpText( "Help text" );
        formItemSetBuilder.customText( "Custom text" );
        formItemSetBuilder.addFormItem( myInput );
        FormItemSet myFormItemSet = formItemSetBuilder.build();

        Mixin myFormItemSetMixin =
            Mixin.newMixin().name( "my_set" ).displayName( "My Mixin" ).addFormItem( myFormItemSet ).build();

        // exercise
        String serialized = toString( myFormItemSetMixin );

        assertSerializedResult( "mixin-formItemSet", serialized );
    }

    @Test
    public void parse_FormItemSetMixin()
        throws Exception
    {
        // exercise
        Mixin mixin = toMixin( getSerializedString( "parse-FormItemSetMixin" ) );

        // verify
        assertEquals( "address", mixin.getName() );
        assertEquals( "Address Mixin", mixin.getDisplayName() );

        assertTrue( mixin.getFormItems() instanceof FormItems );
        FormItemSet formItemSet = (FormItemSet) mixin.getFormItems().iterator().next();
        assertEquals( "address.street", formItemSet.getInput( "street" ).getPath().toString() );

        assertEquals( "Street", formItemSet.getInput( "street" ).getLabel() );
        assertEquals( 0, formItemSet.getInput( "street" ).getOccurrences().getMinimum() );
        assertEquals( 2, formItemSet.getInput( "street" ).getOccurrences().getMaximum() );

        assertEquals( "address.postalCode", formItemSet.getInput( "postalCode" ).getPath().toString() );
        assertEquals( 1, formItemSet.getInput( "postalCode" ).getOccurrences().getMinimum() );
        assertEquals( 1, formItemSet.getInput( "postalCode" ).getOccurrences().getMaximum() );
        assertEquals( "address.postalPlace", formItemSet.getInput( "postalPlace" ).getPath().toString() );
        assertEquals( 1, formItemSet.getInput( "postalPlace" ).getOccurrences().getMinimum() );
        assertEquals( 1, formItemSet.getInput( "postalPlace" ).getOccurrences().getMaximum() );
    }

    @Test
    public void mixin_serialize_parse_serialize_formSet_roundTrip()
    {
        Input.Builder inputBuilder = newInput();
        inputBuilder.name( "my_input" );
        inputBuilder.label( "My input" );
        inputBuilder.inputType( InputTypes.TEXT_LINE );
        Input myInput = inputBuilder.build();

        FormItemSet.Builder formItemSetBuilder = FormItemSet.newFormItemSet();
        formItemSetBuilder.name( "my_set" );
        formItemSetBuilder.label( "My set" );
        formItemSetBuilder.minimumOccurrences( 1 );
        formItemSetBuilder.maximumOccurrences( 3 );
        formItemSetBuilder.helpText( "Help text" );
        formItemSetBuilder.customText( "Custom text" );
        formItemSetBuilder.addFormItem( myInput );
        formItemSetBuilder.addFormItem(
            Input.newInput().name( "myOtherInput" ).label( "My other input" ).inputType( InputTypes.TEXT_LINE ).build() );
        FormItemSet myFormItemSet = formItemSetBuilder.build();

        Mixin myFormItemSetMixin =
            Mixin.newMixin().name( "my_set" ).displayName( "My Mixin" ).addFormItem( myFormItemSet ).build();

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
        Input.Builder inputBuilder = newInput();
        inputBuilder.name( "my_input" );
        inputBuilder.label( "My input" );
        inputBuilder.inputType( InputTypes.TEXT_LINE );
        inputBuilder.immutable( true );
        inputBuilder.minimumOccurrences( 1 );
        inputBuilder.maximumOccurrences( 3 );
        inputBuilder.helpText( "Help text" );
        inputBuilder.customText( "Custom text" );
        inputBuilder.indexed( true );
        Input myInput = inputBuilder.build();
        Mixin myInputMixin = Mixin.newMixin().name( "my_input" ).displayName( "My Mixin" ).addFormItem( myInput ).build();

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
