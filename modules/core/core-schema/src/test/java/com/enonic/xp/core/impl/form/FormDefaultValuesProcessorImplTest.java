package com.enonic.xp.core.impl.form;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormDefaultValuesProcessorImplTest
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
    void defaultValue_string_nonEmptyData()
    {
        Input input = Input.create().
            name( "testInput" ).
            label( "testInput" ).
            inputType( InputTypeName.TEXT_LINE ).
            defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "two" ).build() ).build() ).
            build();

        final Form form = Form.create().
            addFormItem( input ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        data.setProperty( PropertyPath.from( "testInput" ), ValueFactory.newString( "three" ) );
        defaultValuesProcessor.setDefaultValues( form, data );

        assertEquals( "three", data.getString( "testInput" ) );
    }

    @Test
    void defaultValue_combobox()
    {
        this.defaultValue_string( InputTypeName.COMBO_BOX );
    }

    @Test
    void defaultValue_radio()
    {
        this.defaultValue_string( InputTypeName.RADIO_BUTTON );
    }

    @Test
    void defaultValue_checkbox()
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
    void defaultValue_checkbox_invalid()
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
    void testOptionSetItemsAreDefaulted()
    {
        FormOptionSet.Builder myOptionSet =
            FormOptionSet.create().required( false ).name( "myOptionSet" ).occurrences( Occurrences.create( 1, 1 ) );
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
        assertEquals( 0, data.getDouble( "myOptionSet.option1.myDouble" ) );
    }

    @Test
    void testOptionSetIsNotDefaultedForUnselected()
    {
        FormOptionSet.Builder myOptionSet =
            FormOptionSet.create().required( false ).name( "myOptionSet" ).occurrences( Occurrences.create( 1, 1 ) );

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
    void testDefaultValueForInputAndItemSetWithOccurrences()
    {
        Input input = Input.create().
            name( "testInput" ).
            label( "testInput" ).
            inputType( InputTypeName.TEXT_LINE ).
            defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "Default Value" ).build() ).build() ).
            build();

        FormItemSet formItemSet = FormItemSet.create().
            name( "field" ).
            label( "field" ).
            addFormItem( input ).
            occurrences( Occurrences.create( 3, 3 ) ).
            build();

        final Form form = Form.create().
            addFormItem( formItemSet ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        for ( int i = 0; i < 3; i++ )
        {
            assertEquals( "Default Value", data.getProperty( "field", i ).getSet().getString( "testInput" ) );
        }
    }

    @Test
    void testDefaultValueForInputWithOccurrences()
    {
        Input input = Input.create().
            name( "testInput" ).
            label( "testInput" ).
            inputType( InputTypeName.TEXT_LINE ).
            defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "Default Value" ).build() ).build() ).
            occurrences( Occurrences.create( 3, 3 ) ).
            build();

        final Form form = Form.create().
            addFormItem( input ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        for ( int i = 0; i < 3; i++ )
        {
            assertEquals( "Default Value", data.getString( "testInput", i ) );
        }
    }

    @Test
    void testOptionSetWithDefaultValueAndMinOccurrencesMoreThanZero()
    {
        FormOptionSet.Builder checkOptionSet = FormOptionSet.create().
            required( false ).
            name( "checkOptionSet" ).
            occurrences( Occurrences.create( 2, 3 ) );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option_1" );
        FormOptionSetOption.Builder option2 = FormOptionSetOption.create().name( "option_2" ).
            defaultOption( true ).
            addFormItem( Input.create()
                             .name( "testInput" )
                             .label( "testInput" )
                             .inputType( InputTypeName.TEXT_LINE )
                             .defaultValue( InputTypeDefault.create()
                                                .property( InputTypeProperty.create( "default", "Default Value" ).build() )
                                                .build() )
                             .occurrences( Occurrences.create( 3, 3 ) )
                             .build() );

        checkOptionSet.addOptionSetOption( option1.build() );
        checkOptionSet.addOptionSetOption( option2.build() );

        final Form form = Form.create().
            addFormItem( checkOptionSet.build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        for ( int i = 0; i < 2; i++ )
        {
            Property checkOptionSet_1 = data.getProperty( "checkOptionSet", i );
            assertEquals( "option_2", checkOptionSet_1.getSet().getString( "_selected" ) );

            PropertySet propertySet = checkOptionSet_1.getSet().getPropertySet( "option_2" );
            for ( int j = 0; j < 3; j++ )
            {
                assertEquals( "Default Value", propertySet.getString( "testInput", j ) );
            }
        }
    }

    @Test
    void testOptionSetWithMinOccurrencesEqualZero()
    {
        FormOptionSet.Builder checkOptionSet = FormOptionSet.create().
            required( false ).
            name( "checkOptionSet" ).
            occurrences( Occurrences.create( 0, 3 ) );

        FormOptionSetOption.Builder option = FormOptionSetOption.create().name( "option_1" );
        checkOptionSet.addOptionSetOption( option.build() );

        final Form form = Form.create().
            addFormItem( checkOptionSet.build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertEquals( 0, data.getTotalSize() );
    }

    @Test
    void testOptionSetWithMinOccurrencesMoreZero()
    {
        FormOptionSet.Builder checkOptionSet = FormOptionSet.create().
            required( false ).
            name( "checkOptionSet" ).
            occurrences( Occurrences.create( 1, 3 ) );

        FormOptionSetOption.Builder option = FormOptionSetOption.create().name( "option_1" );
        checkOptionSet.addOptionSetOption( option.build() );

        FormItemSet.Builder formItemSet = FormItemSet.create().name( "item-set" ).
            occurrences( Occurrences.create( 1, 2 ) ).
            addFormItem( checkOptionSet.build() );

        final Form form = Form.create().
            addFormItem( formItemSet.build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertEquals( 2, data.getTotalSize() );
        assertNotNull( data.getProperty( "item-set" ) );
        assertNotNull( data.getProperty( "item-set.checkOptionSet" ) );
    }

    @Test
    void testItemSetWithMinOccurrencesMoreZero()
    {
        FormOptionSet.Builder checkOptionSet = FormOptionSet.create().
            required( false ).
            name( "checkOptionSet" ).
            occurrences( Occurrences.create( 1, 1 ) );

        FormItemSet.Builder formItemSet = FormItemSet.create().name( "item-set" ).
            occurrences( Occurrences.create( 1, 1 ) ).
            addFormItem( Input.create().name( "testInput" ).label( "testInput" ).inputType( InputTypeName.TEXT_LINE ).build() );

        FormOptionSetOption.Builder option = FormOptionSetOption.create().name( "option_1" ).
            defaultOption( true ).
            addFormItem( formItemSet.build() );

        checkOptionSet.addOptionSetOption( option.build() );

        final Form form = Form.create().
            addFormItem( checkOptionSet.build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertEquals( 4, data.getTotalSize() );
        assertNotNull( data.getProperty( "checkOptionSet" ) );
        assertEquals("option_1",  data.getString( "checkOptionSet._selected" ) );
        assertNotNull( data.getProperty( "checkOptionSet.option_1.item-set" ) );
    }

    @Test
    void testItemSetWithMinOccurrencesMoreEqualZero()
    {
        FormItemSet.Builder checkItemSet = FormItemSet.create().
            name( "checkItemSet" ).
            occurrences( Occurrences.create( 0, 3 ) );

        Input input = Input.create().
            name( "testInput" ).
            label( "testInput" ).
            inputType( InputTypeName.TEXT_LINE ).
            defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "Default Value" ).build() ).build() ).
            occurrences( Occurrences.create( 3, 3 ) ).
            build();

        checkItemSet.addFormItem( input );

        final Form form = Form.create().
            addFormItem( checkItemSet.build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertEquals( 0, data.getTotalSize() );
    }

    @Test
    void testItemSetWithMinOccurrencesGreaterMoreZero()
    {
        FormItemSet.Builder itemSet = FormItemSet.create().
            name( "rootItemSet" ).
            occurrences( Occurrences.create( 1, 3 ) );

        itemSet.addFormItem( FormItemSet.create().
            name( "childItemSet-1" ).
            occurrences( Occurrences.create( 1, 3 ) ).
            addFormItem( FormItemSet.create()
                             .name( "childItemSet-2" )
                             .occurrences( Occurrences.create( 0, 1 ) )
                             .addFormItem(
                                 Input.create().name( "testInput" ).label( "testInput" ).inputType( InputTypeName.TEXT_LINE ).build() )
                             .build() ).
            addFormItem( Input.create()
                             .name( "testInput" )
                             .label( "testInput" )
                             .inputType( InputTypeName.TEXT_LINE )
                             .defaultValue( InputTypeDefault.create()
                                                .property( InputTypeProperty.create( "default", "Default Value" ).build() )
                                                .build() )
                             .build() ).build() );

        final Form form = Form.create().
            addFormItem( itemSet.build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertEquals( 3, data.getTotalSize() );
        assertNotNull( data.getProperty( "rootItemSet" ) );
        assertNotNull( data.getProperty( "rootItemSet.childItemSet-1" ) );
        assertNotNull( data.getProperty( "rootItemSet.childItemSet-1.testInput" ) );
    }

    @Test
    void testFieldSet()
    {
        FieldSet.Builder fieldSet = FieldSet.create()
            .label( "FieldSet" )
            .addFormItem( InlineMixin.create().mixin( "testApp:testMixin" ).build() )
            .addFormItem( Input.create()
                              .name( "testInput" )
                              .label( "testInput" )
                              .inputType( InputTypeName.TEXT_LINE )
                              .defaultValue( InputTypeDefault.create()
                                                 .property( InputTypeProperty.create( "default", "Default Value" ).build() )
                                                 .build() )
                              .build() )
            .addFormItem( Input.create()
                              .name( "testInput2" )
                              .label( "testInput2" )
                              .inputType( InputTypeName.TEXT_LINE )
                              .defaultValue( InputTypeDefault.create()
                                                 .property( InputTypeProperty.create( "default", "Default Value 2" ).build() )
                                                 .build() )
                              .build() );

        FormItemSet.Builder itemSet = FormItemSet.create()
            .name( "item-set" )
            .label( "Item Set" )
            .occurrences( Occurrences.create( 1, 1 ) )
            .addFormItem( fieldSet.build() );

        final Form form = Form.create().
            addFormItem( itemSet.build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertEquals( "Default Value", data.getString( "item-set.testInput" ) );
        assertEquals( "Default Value 2", data.getString( "item-set.testInput2" ) );
    }

    @Test
    void testItemSetWithOptionSetWithMinOccurrencesMoreEqualZero()
    {
        FormItemSet.Builder checkItemSet = FormItemSet.create().
            name( "checkItemSet" ).
            occurrences( Occurrences.create( 1, 3 ) );

        checkItemSet.addFormItem( FormOptionSet.create().
            name( "checkOptionSet" ).
            occurrences( Occurrences.create( 0, 1 ) ).
            addOptionSetOption( FormOptionSetOption.create().name( "option" ).build() ).
            build() );

        final Form form = Form.create().
            addFormItem( checkItemSet.build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertEquals( 1, data.getTotalSize() );
        assertNotNull( data.getString( "checkItemSet" ) );
    }

    @Test
    void testOptionSetWithItemSetWithMinOccurrencesMoreEqualZero()
    {
        final Form form = Form.create().
            addFormItem( FormOptionSet.create().
                name( "checkOptionSet" ).
                occurrences( Occurrences.create( 1, 1 ) ).
                addOptionSetOption( FormOptionSetOption.create().name( "option" ).addFormItem( FormItemSet.create().
                    name( "checkItemSet" ).
                    occurrences( Occurrences.create( 0, 1 ) ).build() ).build() ).
                build() ).
            build();

        final FormDefaultValuesProcessor defaultValuesProcessor = new FormDefaultValuesProcessorImpl();
        final PropertyTree data = new PropertyTree();
        defaultValuesProcessor.setDefaultValues( form, data );

        assertEquals( 1, data.getTotalSize() );
        assertNotNull( data.getString( "checkOptionSet" ) );
    }
}
