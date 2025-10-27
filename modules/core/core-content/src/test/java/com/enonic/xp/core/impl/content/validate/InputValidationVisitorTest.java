package com.enonic.xp.core.impl.content.validate;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeValidationException;
import com.enonic.xp.inputtype.InputTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InputValidationVisitorTest
{
    @Test
    public void validateInputTypeInvalid()
        throws Exception
    {
        Input myTextLine =
            Input.create().name( "myTextLine" ).inputType( InputTypeName.TEXT_LINE ).label( "My text line" ).required( true ).build();
        Form form = Form.create().addFormItem( myTextLine ).build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setLong( "myTextLine", 33L );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        assertThrows( InputTypeValidationException.class, () -> validationVisitor.traverse( form ) );
    }

    @Test
    public void validateInputTypeValid()
        throws Exception
    {
        Input myTextLine =
            Input.create().name( "myTextLine" ).inputType( InputTypeName.TEXT_LINE ).label( "My text line" ).required( true ).build();
        Form form = Form.create().addFormItem( myTextLine ).build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myTextLine", "33" );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        validationVisitor.traverse( form );
    }

    @Test
    public void validateItemSetInvalid()
        throws Exception
    {
        FormItemSet myFormItemSet = FormItemSet.create()
            .name( "myFormItemSet" )
            .label( "My form item set" )
            .addFormItem(
                Input.create().name( "myTextLine" ).inputType( InputTypeName.TEXT_LINE ).label( "My text line" ).required( false ).build() )
            .build();

        Form form = Form.create().addFormItem( myFormItemSet ).build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setLong( "myFormItemSet.myTextLine", 33L );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        assertThrows( InputTypeValidationException.class, () -> validationVisitor.traverse( form ) );
    }

    @Test
    public void validateItemSetValid()
        throws Exception
    {
        FormItemSet myFormItemSet = FormItemSet.create()
            .name( "myFormItemSet" )
            .label( "My form item set" )
            .addFormItem(
                Input.create().name( "myTextLine" ).inputType( InputTypeName.TEXT_LINE ).label( "My text line" ).required( false ).build() )
            .build();

        Form form = Form.create().addFormItem( myFormItemSet ).build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myFormItemSet.myTextLine", "33" );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        validationVisitor.traverse( form );
    }

    @Test
    public void validateOptionSetInvalid()
        throws Exception
    {
        FormOptionSet formOptionSet = FormOptionSet.create()
            .name( "myOptionSet" )
            .label( "My option set" )
            .helpText( "Option set help text" )
            .addOptionSetOption( FormOptionSetOption.create()
                                     .name( "myOptionSetOption1" )
                                     .label( "option label1" )
                                     .helpText( "Option help text" )
                                     .addFormItem( Input.create()
                                                       .name( "myTextLine1" )
                                                       .label( "myTextLine1" )
                                                       .inputType( InputTypeName.TEXT_LINE )
                                                       .build() )
                                     .build() )
            .addOptionSetOption( FormOptionSetOption.create()
                                     .name( "myOptionSetOption2" )
                                     .label( "option label2" )
                                     .helpText( "Option help text" )
                                     .addFormItem( Input.create()
                                                       .name( "myTextLine2" )
                                                       .label( "myTextLine2" )
                                                       .inputType( InputTypeName.TEXT_LINE )
                                                       .build() )
                                     .build() )
            .build();

        Form form = Form.create().addFormItem( formOptionSet ).build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setLong( "myOptionSet.myOptionSetOption1.myTextLine1", 33L );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        assertThrows( InputTypeValidationException.class, () -> validationVisitor.traverse( form ) );
    }

    @Test
    public void validateOptionSetValid()
        throws Exception
    {
        FormOptionSet formOptionSet = FormOptionSet.create()
            .name( "myOptionSet" )
            .label( "My option set" )
            .helpText( "Option set help text" )
            .addOptionSetOption( FormOptionSetOption.create()
                                     .name( "myOptionSetOption1" )
                                     .label( "option label1" )
                                     .helpText( "Option help text" )
                                     .addFormItem( Input.create()
                                                       .name( "myTextLine1" )
                                                       .label( "myTextLine1" )
                                                       .inputType( InputTypeName.TEXT_LINE )
                                                       .build() )
                                     .build() )
            .addOptionSetOption( FormOptionSetOption.create()
                                     .name( "myOptionSetOption2" )
                                     .label( "option label2" )
                                     .helpText( "Option help text" )
                                     .addFormItem( Input.create()
                                                       .name( "myTextLine2" )
                                                       .label( "myTextLine2" )
                                                       .inputType( InputTypeName.TEXT_LINE )
                                                       .build() )
                                     .build() )
            .build();

        Form form = Form.create().addFormItem( formOptionSet ).build();

        PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myOptionSet.myOptionSetOption1.myTextLine1", "33" );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        validationVisitor.traverse( form );
    }

    @Test
    public void testInputTextLineWithInfiniteOccurrences()
    {
        FormItemSet myFormItemSet = FormItemSet.create()
            .name( "myFormItemSet" )
            .label( "My form item set" )
            .addFormItem( Input.create()
                              .name( "url" )
                              .inputType( InputTypeName.TEXT_LINE )
                              .label( "URL" )
                              .occurrences( 0, 0 )
                              .inputTypeConfig( GenericValue.object()
                                                    .put( "regexp",
                                                          "^http(s)?:\\/\\/.?(www\\.)?[a-zA-Z0-9][-a-zA-Z0-9@:%._\\+~#=]{0,255}\\b([-a-zA-Z0-9@:%_\\+.~#?&amp;//=]*)" )
                                                    .build() )
                              .build() )
            .build();

        Form form = Form.create().addFormItem( myFormItemSet ).build();

        List<String> stringValues = new ArrayList<>();
        stringValues.add( "https://www.oslo.kommune.no/barnehage/finn-barnehage-i-oslo" );
        stringValues.add(
            "https://www.oslo.kommune.no/barnehage/finn-barnehage-i-oslo/#!c%7Cf_preschool_type_student/c.f_preschool_type_student//m.list" );

        PropertyTree propertyTree = new PropertyTree();
        PropertySet propertySet = propertyTree.addSet( "myFormItemSet" );
        propertySet.addStrings( "url", stringValues );

        InputValidationVisitor validationVisitor = new InputValidationVisitor( propertyTree, InputTypes.BUILTIN );
        InputTypeValidationException exception =
            assertThrows( InputTypeValidationException.class, () -> validationVisitor.traverse( form ) );
        assertEquals(
            "Invalid value in [url: https://www.oslo.kommune.no/barnehage/finn-barnehage-i-oslo/#!c%7Cf_preschool_type_student/c.f_preschool_type_student//m.list]: Value does not match with regular expression",
            exception.getMessage() );
    }

    @Test
    public void testOptionSetOccurrences()
    {
        final Form form = Form.create()
            .addFormItem( FormOptionSet.create()
                              .name( "set" )
                              .addOptionSetOption( FormOptionSetOption.create()
                                                       .name( "option" )
                                                       .addFormItem( Input.create()
                                                                         .name( "htmlData" )
                                                                         .label( "htmlData" )
                                                                         .inputType( InputTypeName.TEXT_LINE )
                                                                         .inputTypeConfig( GenericValue.object()
                                                                                               .put( "regexp",
                                                                                                     "^http(s)?:\\/\\/.?(www\\.)?[a-zA-Z0-9][-a-zA-Z0-9@:%._\\+~#=]{0,255}\\b([-a-zA-Z0-9@:%_\\+.~#?&amp;//=]*)" )
                                                                                               .build() )
                                                                         .build() )
                                                       .build() )
                              .build() )
            .build();

        final List<String> validValues = new ArrayList<>();
        validValues.add( "https://www.oslo.kommune.no/barnehage/finn-barnehage-i-oslo1" );
        validValues.add( "https://www.oslo.kommune.no/barnehage/finn-barnehage-i-oslo2" );

        final List<String> invalidValues = new ArrayList<>();
        invalidValues.add( "https://www.oslo.kommune.no/barnehage/finn-barnehage-i-oslo1" );
        invalidValues.add(
            "https://www.oslo.kommune.no/barnehage/finn-barnehage-i-oslo/#!c%7Cf_preschool_type_student/c.f_preschool_type_student" );

        final PropertyTree data = new PropertyTree();
        PropertySet set1 = data.addSet( "set" );
        PropertySet set2 = data.addSet( "set" );

        PropertySet option1 = set1.addSet( "option" );
        PropertySet option2 = set1.addSet( "option" );
        PropertySet option3 = set2.addSet( "option" );

        option1.addStrings( "htmlData", validValues );
        option2.addStrings( "htmlData", invalidValues );
        option3.addStrings( "htmlData", validValues );

        final InputValidationVisitor validationVisitor = new InputValidationVisitor( data, InputTypes.BUILTIN );

        final InputTypeValidationException exception =
            assertThrows( InputTypeValidationException.class, () -> validationVisitor.traverse( form ) );

        assertEquals(
            "Invalid value in [htmlData: https://www.oslo.kommune.no/barnehage/finn-barnehage-i-oslo/#!c%7Cf_preschool_type_student/c.f_preschool_type_student]: Value does not match with regular expression",
            exception.getMessage() );
    }
}
