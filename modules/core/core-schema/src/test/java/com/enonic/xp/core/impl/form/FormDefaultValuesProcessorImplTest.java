package com.enonic.xp.core.impl.form;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormDefaultValuesProcessor;
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

}