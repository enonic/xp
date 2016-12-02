package com.enonic.xp.core.impl.content.validate;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeValidationException;
import com.enonic.xp.inputtype.InputTypes;

public class InputValidationVisitorTest
{
    @Test(expected = InputTypeValidationException.class)
    public void validateInputTypeInvalid()
        throws Exception
    {
        Input myTextLine = Input.create().
            name( "myTextLine" ).
            inputType( InputTypeName.TEXT_LINE ).
            label( "My text line" ).
            required( true ).
            build();
        Form form = Form.create().
            addFormItem( myTextLine ).
            build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setLong( "myTextLine", 33L );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        validationVisitor.traverse( form );
    }

    @Test
    public void validateInputTypeValid()
        throws Exception
    {
        Input myTextLine = Input.create().
            name( "myTextLine" ).
            inputType( InputTypeName.TEXT_LINE ).
            label( "My text line" ).
            required( true ).
            build();
        Form form = Form.create().
            addFormItem( myTextLine ).
            build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myTextLine", "33" );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        validationVisitor.traverse( form );
    }

    @Test(expected = InputTypeValidationException.class)
    public void validateItemSetInvalid()
        throws Exception
    {
        FormItemSet myFormItemSet = FormItemSet.create().
            name( "myFormItemSet" ).
            label( "My form item set" ).
            addFormItem( Input.create().
                name( "myTextLine" ).
                inputType( InputTypeName.TEXT_LINE ).
                label( "My text line" ).
                required( false ).
                build() ).
            build();

        Form form = Form.create().
            addFormItem( myFormItemSet ).
            build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setLong( "myFormItemSet.myTextLine", 33L );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        validationVisitor.traverse( form );
    }

    @Test
    public void validateItemSetValid()
        throws Exception
    {
        FormItemSet myFormItemSet = FormItemSet.create().
            name( "myFormItemSet" ).
            label( "My form item set" ).
            addFormItem( Input.create().
                name( "myTextLine" ).
                inputType( InputTypeName.TEXT_LINE ).
                label( "My text line" ).
                required( false ).
                build() ).
            build();

        Form form = Form.create().
            addFormItem( myFormItemSet ).
            build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myFormItemSet.myTextLine", "33" );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        validationVisitor.traverse( form );
    }

    @Test(expected = InputTypeValidationException.class)
    public void validateOptionSetInvalid()
        throws Exception
    {
        FormOptionSet formOptionSet = FormOptionSet.create().
            name( "myOptionSet" ).
            label( "My option set" ).
            helpText( "Option set help text" ).
            addOptionSetOption(
                FormOptionSetOption.create().name( "myOptionSetOption1" ).label( "option label1" ).helpText( "Option help text" ).
                    addFormItem( Input.create().name( "myTextLine1" ).label( "myTextLine1" ).inputType(
                        InputTypeName.TEXT_LINE ).build() ).build() ).
            addOptionSetOption(
                FormOptionSetOption.create().name( "myOptionSetOption2" ).label( "option label2" ).helpText( "Option help text" ).
                    addFormItem( Input.create().name( "myTextLine2" ).label( "myTextLine2" ).inputType(
                        InputTypeName.TEXT_LINE ).build() ).build() ).
            build();

        Form form = Form.create().
            addFormItem( formOptionSet ).
            build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setLong( "myOptionSet.myOptionSetOption1.myTextLine1", 33L );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        validationVisitor.traverse( form );
    }

    @Test
    public void validateOptionSetValid()
        throws Exception
    {
        FormOptionSet formOptionSet = FormOptionSet.create().
            name( "myOptionSet" ).
            label( "My option set" ).
            helpText( "Option set help text" ).
            addOptionSetOption(
                FormOptionSetOption.create().name( "myOptionSetOption1" ).label( "option label1" ).helpText( "Option help text" ).
                    addFormItem( Input.create().name( "myTextLine1" ).label( "myTextLine1" ).inputType(
                        InputTypeName.TEXT_LINE ).build() ).build() ).
            addOptionSetOption(
                FormOptionSetOption.create().name( "myOptionSetOption2" ).label( "option label2" ).helpText( "Option help text" ).
                    addFormItem( Input.create().name( "myTextLine2" ).label( "myTextLine2" ).inputType(
                        InputTypeName.TEXT_LINE ).build() ).build() ).
            build();

        Form form = Form.create().
            addFormItem( formOptionSet ).
            build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myOptionSet.myOptionSetOption1.myTextLine1", "33" );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        validationVisitor.traverse( form );
    }
}