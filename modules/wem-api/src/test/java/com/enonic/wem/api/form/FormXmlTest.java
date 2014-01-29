package com.enonic.wem.api.form;

import org.junit.Test;

import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.form.inputtype.InputTypes.DECIMAL_NUMBER;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_LINE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class FormXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        final Input myTextLine = newInput().
            name( "myTextLine" ).
            inputType( TEXT_LINE ).
            label( "My text line" ).
            required( true ).
            build();

        final Input myCustomInput = newInput().
            name( "myCustomInput" ).
            inputType( TEXT_LINE ).
            label( "My custom input" ).
            required( false ).
            build();

        final FieldSet myFieldSet = newFieldSet().
            name( "myFieldSet" ).
            label( "My field set" ).
            addFormItem( newInput().
                name( "fieldSetItem" ).
                inputType( TEXT_LINE ).
                label( "Field set Item" ).
                required( false ).
                build() ).
            build();

        final FormItemSet myFormItemSet = newFormItemSet().
            name( "myFormItemSet" ).
            label( "My form item set" ).
            addFormItem( myTextLine ).
            addFormItem( myCustomInput ).
            addFormItem( myFieldSet ).
            build();

        final Input myInput = newInput().
            name( "pause" ).
            inputType( DECIMAL_NUMBER ).
            label( "Pause parameter" ).
            maximumOccurrences( 3 ).
            minimumOccurrences( 2 ).
            customText( "customText" ).
            helpText( "helpText" ).
            validationRegexp( "!@$%" ).
            indexed( true ).
            immutable( true ).
            build();

        final MixinReference myMixinReference = newMixinReference().
            name( "mixin" ).
            mixin( "reference" ).
            build();

        final Form form = Form.newForm().
            addFormItem( myInput ).
            addFormItem( myFormItemSet ).
            addFormItem( myMixinReference ).
            build();


        final FormXml formXml = new FormXml();

        formXml.from( form );

        final String result = XmlSerializers.form().serialize( formXml );

        assertXml( "form.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "form.xml" );
        final Form.Builder builder = Form.newForm();

        XmlSerializers.form().parse( xml ).to( builder );

        final Form form = builder.build();

        final Input pause = form.getFormItem( "pause" ).toInput();
        assertEquals( DECIMAL_NUMBER, pause.getInputType() );
        assertEquals( "Pause parameter", pause.getLabel() );
        assertEquals( "customText", pause.getCustomText() );
        assertEquals( "helpText", pause.getHelpText() );
        assertEquals( "!@$%", pause.getValidationRegexp().toString() );
        assertTrue( pause.isImmutable() );
        assertTrue( pause.isIndexed() );
        assertEquals( 3, pause.getOccurrences().getMaximum() );
        assertEquals( 2, pause.getOccurrences().getMinimum() );

        assertNotNull( form.getFormItem( "myFormItemSet" ).toFormItemSet() );
        assertEquals( "My form item set", form.getFormItem( "myFormItemSet" ).toFormItemSet().getLabel() );
        assertEquals( TEXT_LINE, form.getFormItem( "myFormItemSet.fieldSetItem" ).toInput().getInputType() );
        assertEquals( "Field set Item", form.getFormItem( "myFormItemSet.fieldSetItem" ).toInput().getLabel() );
    }
}
