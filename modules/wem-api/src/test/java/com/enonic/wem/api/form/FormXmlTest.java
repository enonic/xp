package com.enonic.wem.api.form;

import org.junit.Test;

import com.acme.DummyCustomInputType;

import com.enonic.wem.api.form.inputtype.ComboBoxConfig;
import com.enonic.wem.api.form.inputtype.ImageSelectorConfig;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.form.inputtype.RelationshipConfig;
import com.enonic.wem.api.form.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.xml.BaseXmlSerializerTest;
import com.enonic.wem.api.xml.XmlSerializers;

import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.form.inputtype.ComboBoxConfig.newComboBoxConfig;
import static com.enonic.wem.api.form.inputtype.ImageSelectorConfig.newImageSelectorConfig;
import static com.enonic.wem.api.form.inputtype.InputTypes.DOUBLE;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_LINE;
import static com.enonic.wem.api.form.inputtype.RelationshipConfig.newRelationshipConfig;
import static com.enonic.wem.api.form.inputtype.SingleSelectorConfig.newSingleSelectorConfig;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
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
            inputType( DOUBLE ).
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
            mixin( "mymodule-1.0.0:reference" ).
            build();

        final Form form = Form.newForm().
            addFormItem( myInput ).
            addFormItem( myFormItemSet ).
            addFormItem( myMixinReference ).
            build();

        final String result = toXml( form );

        assertXml( "form.xml", result );
    }

    @Test
    public void from_all_Input_types()
        throws Exception
    {

        Form form = createFormWithAllInputTypes();

        // exercise
        String result = toXml( form );

        // verify
        assertXml( "form-with-all-input-types.xml", result );
    }

    @Test
    public void from_all_FormItem_types()
        throws Exception
    {

        Form form = createFormWithAllFormItemTypes();

        // exercise
        String result = toXml( form );

        // verify
        assertXml( "form-with-all-form-item-types.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {

        final String xml = readFromFile( "form.xml" );

        final Form form = toForm( xml );

        final Input pause = form.getFormItem( "pause" ).toInput();
        assertEquals( DOUBLE, pause.getInputType() );
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

    @Test
    public void to_all_Input_types()
        throws Exception
    {

        String xml = readFromFile( "form-with-all-input-types.xml" );

        // exercise
        Form parsedForm = toForm( xml );

        // verify
        Form expectedForm = createFormWithAllInputTypes();

        assertEquals( expectedForm.size(), parsedForm.size() );
        assertEquals( toXml( expectedForm ), toXml( parsedForm ) );
    }

    @Test
    public void toForm_all_FormItem_types()
        throws Exception
    {

        String xml = readFromFile( "form-with-all-form-item-types.xml" );

        // exercise
        Form parsedForm = toForm( xml );

        // verify
        Form expectedForm = createFormWithAllFormItemTypes();

        assertEquals( expectedForm.size(), parsedForm.size() );
        assertEquals( toXml( expectedForm ), toXml( parsedForm ) );
    }

    private Form createFormWithAllFormItemTypes()
    {

        Mixin inputMixin = newMixin().name( "mymodule-1.0.0:my_shared_input" ).addFormItem(
            Input.newInput().name( "my_shared_input" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        FormItemSet set = newFormItemSet().name( "mySet" ).build();
        Layout layout = FieldSet.newFieldSet().label( "My field set" ).name( "myFieldSet" ).addFormItem(
            newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        set.add( layout );
        set.add( newMixinReference().name( "myCommonInput" ).mixin( inputMixin ).build() );

        Form.Builder formBuilder = Form.newForm();
        formBuilder.addFormItem( set );
        return formBuilder.build();
    }

    private Form createFormWithAllInputTypes()
    {

        ComboBoxConfig comboBoxConfig = newComboBoxConfig().
            addOption( "myOption 1", "o1" ).
            addOption( "myOption 2", "o2" ).
            build();

        SingleSelectorConfig singleSelectorConfig = newSingleSelectorConfig().
            typeDropdown().
            addOption( "myOption 1", "o1" ).
            addOption( "myOption 2", "o2" ).
            build();

        RelationshipConfig relationshipConfig = newRelationshipConfig().
            relationshipType( RelationshipTypeName.LIKE ).
            build();

        ImageSelectorConfig imageSelectorConfig = newImageSelectorConfig().
            relationshipType( RelationshipTypeName.DEFAULT ).
            build();

        Form.Builder formBuilder = Form.newForm();

        formBuilder.addFormItem( newInput().name( "myColor" ).inputType( InputTypes.COLOR ).build() );
        formBuilder.addFormItem( newInput().name( "myDate" ).inputType( InputTypes.DATE ).build() );
        formBuilder.addFormItem( newInput().name( "myCheckbox" ).inputType( InputTypes.CHECKBOX ).build() );
        formBuilder.addFormItem( newInput().name( "myTime" ).inputType( InputTypes.TIME ).build() );
        formBuilder.addFormItem( newInput().name( "myDateTime" ).inputType( InputTypes.DATE_TIME ).build() );
        formBuilder.addFormItem( newInput().name( "myDouble" ).inputType( InputTypes.DOUBLE ).build() );
        formBuilder.addFormItem( newInput().name( "myGeoPoint" ).inputType( InputTypes.GEO_POINT ).build() );
        formBuilder.addFormItem( newInput().name( "myHtmlArea" ).inputType( InputTypes.HTML_AREA ).build() );
        formBuilder.addFormItem( newInput().name( "myMoney" ).inputType( InputTypes.MONEY ).build() );
        formBuilder.addFormItem( newInput().name( "myPhone" ).inputType( InputTypes.PHONE ).build() );
        formBuilder.addFormItem(
            newInput().name( "myComboBox" ).inputType( InputTypes.COMBO_BOX ).inputTypeConfig( comboBoxConfig ).build() );
        formBuilder.addFormItem(
            newInput().name( "mySingleSelector" ).inputType( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );
        formBuilder.addFormItem( newInput().name( "myTag" ).inputType( InputTypes.TAG ).build() );
        formBuilder.addFormItem( newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() );
        formBuilder.addFormItem( newInput().name( "myTextArea" ).inputType( InputTypes.TEXT_AREA ).inputTypeConfig(
            InputTypes.TEXT_AREA.getDefaultConfig() ).build() );
        formBuilder.addFormItem( newInput().name( "myLong" ).inputType( InputTypes.LONG ).build() );
        formBuilder.addFormItem( newInput().name( "myXml" ).inputType( InputTypes.XML ).build() );
        formBuilder.addFormItem(
            newInput().name( "myRelationship" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( relationshipConfig ).build() );
        formBuilder.addFormItem(
            newInput().name( "myImage" ).inputType( InputTypes.IMAGE_SELECTOR ).inputTypeConfig( imageSelectorConfig ).build() );

        formBuilder.addFormItem( newInput().name( "myCustomInput" ).inputType( new DummyCustomInputType() ).build() );

        return formBuilder.build();
    }

    private String toXml( Form form )
    {

        FormXml formXml = new FormXml();
        formXml.from( form );
        return XmlSerializers.form().serialize( formXml );
    }

    private Form toForm( String xml )
    {

        Form.Builder builder = Form.newForm();
        XmlSerializers.form().parse( xml ).to( builder );
        return builder.build();
    }
}
