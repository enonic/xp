package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputVisitorTest
{

    @Test
    void traverse()
        throws Exception
    {
        Input myTextLine = Input.create().
            name( "myTextLine" ).
            inputType( InputTypeName.TEXT_LINE ).
            label( "My text line" ).
            required( true ).
            build();

        Input myCustomInput = Input.create().
            name( "myCheckbox" ).
            inputType( InputTypeName.CHECK_BOX ).
            label( "My checkbox input" ).
            required( false ).
            build();

        FieldSet myFieldSet = FieldSet.create().
            label( "My field set" ).
            addFormItem( Input.create().
            name( "myLong" ).
            inputType( InputTypeName.LONG ).
            label( "My long" ).
            required( false ).
            build() ).
            build();

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

        FormFragment myInline = FormFragment.create().formFragment( "myapplication:myFormFragment" ).
            build();

        Form form = Form.create().
            addFormItem( myTextLine ).
            addFormItem( myCustomInput ).
            addFormItem( myFieldSet ).
            addFormItem( myFormItemSet ).
            addFormItem( myInline ).
            addFormItem( formOptionSet ).
            build();

        List<String> itemPathsVisited = new ArrayList<>();
        InputVisitor iv = new InputVisitor()
        {
            @Override
            public void visit( final Input input )
            {
                itemPathsVisited.add( input.getPath().toString() );
            }
        };

        iv.traverse( form );

        final List<String> expected =
            Arrays.asList( "myTextLine", "myCheckbox", "myLong", "myFormItemSet.myTextLine", "myOptionSet.myOptionSetOption1.myTextLine1",
                           "myOptionSet.myOptionSetOption2.myTextLine2" );

        assertEquals( expected, itemPathsVisited );
    }

}
