package com.enonic.xp.form;


import org.junit.jupiter.api.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class FormTest
{

    @Test
    void duplicatedItemNames()
    {
        assertThrows( IllegalArgumentException.class, () -> Form.create().
            addFormItem( Input.create().name( "myInput" ).label( "my input" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            addFormItem( FormItemSet.create().name( "myInput" ).label( "my input" ).build() ).
            build() );
    }

    @Test
    void duplicatedItemInFieldSet()
    {
        final FieldSet fieldSet1 = FieldSet.create().
            label( "fieldset1" ).
            addFormItem( FormItemSet.create().name( "duplicated" ).label( "duplicated" ).addFormItem(
                Input.create().name( "something" ).label( "something" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).
            build();

        final FieldSet fieldSet2 = FieldSet.create().
            label( "fieldSet2" ).
            addFormItem( Input.create().name( "duplicated" ).label( "duplicated" ).inputType( InputTypeName.CONTENT_SELECTOR ).build() ).
            build();

        try
        {
            Form form = Form.create().
                addFormItem( fieldSet1 ).
                addFormItem( fieldSet2 ).
                build();
            fail( "Expected exception" );
        }
        catch ( IllegalArgumentException e )
        {
            assertTrue( e.getMessage().equals( "FormItem already added: duplicated" ) );
        }
    }

    @Test
    void duplicatedItemInItemSet()
    {
        try
        {
            final FormItemSet fis1 = FormItemSet.create().
                name( "form-item-set" ).
                addFormItem( Input.create().name( "myInput1" ).label( "my input" ).inputType( InputTypeName.TEXT_LINE ).build() ).
                build();

            final FormItemSet fis2 = FormItemSet.create().
                name( "form-item-set" ).
                addFormItem( FormItemSet.create().name( "myInput2" ).label( "my input" ).build() ).
                build();

            final FieldSet fieldSet1 = FieldSet.create().
                label( "fieldSet1" ).
                addFormItem( fis1 ).
                build();

            final FieldSet fieldSet2 = FieldSet.create().
                label( "fieldSet2" ).
                addFormItem( fis2 ).
                build();

            Form.create().
                addFormItem( fieldSet1 ).
                addFormItem( fieldSet2 ).
                build();

            fail( "Expected exception" );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "FormItem already added: form-item-set", e.getMessage() );
        }
    }

    @Test
    void formOptionSetWithoutDuplicates()
    {
        FormOptionSet formOptionSet1 = FormOptionSet.create().
            name( "formOptionSet1" ).
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

        FormOptionSet formOptionSet2 = FormOptionSet.create().
            name( "formOptionSet2" ).
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

        Form.create().
            addFormItem( formOptionSet1 ).
            addFormItem( formOptionSet2 ).
            build();

    }

    @Test
    void formOptionSetWithDuplicates()
    {
        try
        {
            FormOptionSet formOptionSet1 = FormOptionSet.create().
                name( "formOptionSet" ).
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

            FormOptionSet formOptionSet2 = FormOptionSet.create().
                name( "formOptionSet" ).
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

            final FieldSet fieldSet1 = FieldSet.create().
                label( "fieldset1" ).
                addFormItem( formOptionSet1 ).
                build();

            final FieldSet fieldSet2 = FieldSet.create().
                label( "fieldSet2" ).
                addFormItem( formOptionSet2 ).
                build();

            Form.create().
                addFormItem( fieldSet1 ).
                addFormItem( fieldSet2 ).
                build();

            fail( "Expected exception" );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "FormItem already added: formOptionSet", e.getMessage() );
        }
    }
}
