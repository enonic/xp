package com.enonic.xp.core.impl.form;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

import static org.junit.Assert.*;

public class FormDefaultValuesProcessorImplTest
{

    private void defaultValue_string( final InputTypeName inputTypeName )
    {
        Input input = Input.create().
            name( "testInput" ).
            label( "testInput" ).
            inputTypeProperty( InputTypeProperty.create( "one", "one" ).build() ).
            inputTypeProperty( InputTypeProperty.create( "two", "two" ).build() ).
            inputTypeProperty( InputTypeProperty.create( "three", "three" ).build() ).
            inputType( inputTypeName ).
            defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "two" ).build() ).build() ).
            build();

        final Form form = Form.create().
            addFormItem( input ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertTrue( data.getString( "testInput" ).equals( "two" ) );
    }

    @Test
    public void defaultValue_combobox()
    {
        this.defaultValue_string( InputTypeName.COMBO_BOX );
    }

    @Test
    public void defaultValue_radio()
    {
        this.defaultValue_string( InputTypeName.RADIO_BUTTON );
    }

    @Test
    public void defaultValue_checkbox()
    {
        Input input = Input.create().
            name( "testInput" ).
            label( "testInput" ).
            inputType( InputTypeName.CHECK_BOX ).
            defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "checked" ).build() ).build() ).
            build();

        final Form form = Form.create().
            addFormItem( input ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertTrue( data.getString( "testInput" ).equals( "true" ) );
    }

    @Test
    public void defaultValue_checkbox_invalid()
    {
        Input input = Input.create().
            name( "testInput" ).
            label( "testInput" ).
            inputType( InputTypeName.CHECK_BOX ).
            defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "unchecked" ).build() ).build() ).
            build();

        final Form form = Form.create().
            addFormItem( input ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertNull( data.getString( "testInput" ) );
    }

    @Test
    public void testOptionSetItemsAreDefaulted()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().required( false ).name( "myOptionSet" );
        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );

        option1.
            addFormItem( Input.create().
                name( "myInput" ).
                label( "Input" ).
                inputType( InputTypeName.TEXT_LINE ).
                defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "default" ).build() ).build() ).
                build() ).
            addFormItem( Input.create().
                name( "myDouble" ).
                label( "double" ).
                inputType( InputTypeName.DOUBLE ).
                defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "0" ).build() ).build() ).
                build() );

        myOptionSet.addOptionSetOption( option1.build() );

        final Form form = Form.create().
            addFormItem( myOptionSet.build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertEquals( "default", data.getString( "myOptionSet.option1.myInput" ) );
        assertEquals( new Double( 0 ), data.getDouble( "myOptionSet.option1.myDouble" ) );
    }

    @Test
    public void testOptionSetIsNotDefaultedForUnselected()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().required( false ).name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );
        FormOptionSetOption.Builder option2 = FormOptionSetOption.create().name( "option2" );

        option1.
            addFormItem( Input.create().
                name( "myInput" ).
                label( "Input" ).
                inputType( InputTypeName.TEXT_LINE ).
                defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "default" ).build() ).build() ).
                build() );
        option2.
            addFormItem( Input.create().
                name( "myDouble" ).
                label( "double" ).
                inputType( InputTypeName.DOUBLE ).
                defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "0" ).build() ).build() ).
                build() );

        myOptionSet.addOptionSetOption( option1.build() );
        myOptionSet.addOptionSetOption( option2.build() );

        final Form form = Form.create().
            addFormItem( myOptionSet.build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertEquals( "default", data.getString( "myOptionSet.option1.myInput" ) );
        assertNull( data.getDouble( "myOptionSet.option1.myDouble" ) );
    }

    @Test
    public void testOptionSetHasDefaultOptionsInData()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().required( false ).name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );
        FormOptionSetOption.Builder option2 = FormOptionSetOption.create().name( "option2" );

        myOptionSet.addOptionSetOption( option1.build() );
        myOptionSet.addOptionSetOption( option2.build() );

        final Form form = Form.create().
            addFormItem( myOptionSet.build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertNotNull( data.getSet( "myOptionSet.option1" ) );
        assertNull( data.getSet( "myOptionSet.option2" ) );
    }
}