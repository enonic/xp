package com.enonic.wem.core.form;

import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.serializer.DataJsonSerializer;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.MixinReference;
import com.enonic.wem.api.form.inputtype.InputType;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.support.JsonTestHelper;

import static junit.framework.Assert.assertEquals;

public class FormItemDataSerializerTest
{
    private DataJsonSerializer dataJsonSerializer;

    private FormItemDataSerializer serializer;

    private JsonTestHelper jsonHelper;

    public FormItemDataSerializerTest()
    {
        this.serializer = new FormItemDataSerializer();
        jsonHelper = new JsonTestHelper( this, true );

        dataJsonSerializer = new DataJsonSerializer();
        dataJsonSerializer.prettyPrint();
    }

    @Test
    public void serializeFormItem_givenInput()
    {
        // exercise
        Data generatedData = serializer.toData( createInput( "text-area", InputTypes.TEXT_AREA ) );

        // verify
        Data expectedData = createInputData( "text-area", InputTypes.TEXT_AREA );
        assertData( expectedData, generatedData );
    }


    @Test
    public void serializeFormItem_given_FormItemSet()
    {
        // setup
        FormItemSet formItemSet = createFormItemSet( "form-item-set", createInput( "text-line", InputTypes.TEXT_LINE ),
                                                     createInput( "html-area", InputTypes.HTML_AREA ),
                                                     createFormItemSet( "inner-form-item-set" ) );

        // exercise
        Data generatedData = serializer.toData( formItemSet );

        // verify
        Data expectedData = createFormItemSetData( "form-item-set", createInputData( "text-line", InputTypes.TEXT_LINE ),
                                                   createInputData( "html-area", InputTypes.HTML_AREA ),
                                                   createFormItemSetData( "inner-form-item-set" ) );
        assertData( expectedData, generatedData );
    }

    @Test
    public void serializeFormItem_given_FieldSet()
    {
        // setup
        FieldSet fieldSet = createFieldSet( "field-set", createInput( "text-line", InputTypes.TEXT_LINE ),
                                            createInput( "html-area", InputTypes.HTML_AREA ) );

        // exercise
        Data generatedData = serializer.toData( fieldSet );

        // verify
        Data expectedData = createFieldSetData( "field-set", createInputData( "text-line", InputTypes.TEXT_LINE ),
                                                createInputData( "html-area", InputTypes.HTML_AREA ) );
        assertData( expectedData, generatedData );
    }


    @Test
    public void serializeFormItems_given_MixinReference()
    {
        final MixinReference mixinReference =
            MixinReference.newMixinReference().name( "mymixinreference" ).mixin( "mymodule:mymixinreferencedto" ).build();

        final Data dataSet = serializer.toData( mixinReference );

        final MixinReference deserializedMixinReference = serializer.deserializeMixinReference( (DataSet) dataSet );

        Assert.assertEquals( mixinReference.getName(), deserializedMixinReference.getName() );
        Assert.assertEquals( mixinReference.getMixinName(), deserializedMixinReference.getMixinName() );
    }

    private Input createInput( String name, InputType inputType )
    {
        return Input.newInput().
            name( name ).
            label( name ).
            immutable( false ).
            multiple( false ).
            indexed( true ).
            customText( "custom text" ).
            helpText( "help text" ).
            inputType( inputType ).
            inputTypeConfig( inputType.getDefaultConfig() ).
            occurrences( 0, 1 ).build();
    }

    private DataSet createInputData( String name, InputType inputType )
    {
        DataSet.Builder inputTypeBuilder = DataSet.create();
        inputTypeBuilder.name( "inputType" ).set( "name", inputType.getName(), ValueTypes.STRING );

        DataSet.Builder inputDataBuilder = DataSet.create().name( "Input" ).
            set( "name", name, ValueTypes.STRING ).
            set( "label", name, ValueTypes.STRING ).
            set( "customText", "custom text", ValueTypes.STRING ).
            set( "helpText", "help text", ValueTypes.STRING ).
            set( "immutable", Boolean.FALSE, ValueTypes.STRING ).
            set( "indexed", Boolean.TRUE, ValueTypes.STRING );

        if ( inputType.getDefaultConfig() != null )
        {
            Element configEl = inputType.getInputTypeConfigXmlSerializer().generate( inputType.getDefaultConfig() );
            String configXml = new XMLOutputter().outputString( configEl );
            inputDataBuilder.set( "inputTypeConfig", configXml, ValueTypes.XML );
        }

        DataSet inputData = inputDataBuilder.build();
        DataSet occurrences = createOccurrences();
        inputData.add( occurrences );
        inputData.add( inputTypeBuilder.build() );

        return inputData;
    }

    private FormItemSet createFormItemSet( String name, FormItem... formItems )
    {
        FormItemSet.Builder formItemSetBuilder =
            FormItemSet.newFormItemSet().name( name ).customText( "custom text" ).helpText( "help text" ).
                maximumOccurrences( 1 ).minimumOccurrences( 0 ).label( name ).immutable( false ).multiple( false ).
                required( false );
        for ( FormItem formItem : formItems )
        {
            formItemSetBuilder.addFormItem( formItem );
        }
        return formItemSetBuilder.build();
    }


    private DataSet createFormItemSetData( String name, DataSet... formItemsData )
    {
        DataSet.Builder formItemSetBuilder = DataSet.create().name( "FormItemSet" );
        formItemSetBuilder.set( "name", name, ValueTypes.STRING ).
            set( "label", name, ValueTypes.STRING ).
            set( "customText", "custom text", ValueTypes.STRING ).
            set( "helpText", "help text", ValueTypes.STRING ).
            set( "immutable", Boolean.FALSE, ValueTypes.STRING );
        DataSet formItemSetData = formItemSetBuilder.build();
        formItemSetData.add( createOccurrences() );

        DataSet itemsDataSet = new DataSet( "items" );
        for ( DataSet formItemData : formItemsData )
        {
            itemsDataSet.add( formItemData );
        }
        formItemSetData.add( itemsDataSet );
        return formItemSetData;
    }

    private FieldSet createFieldSet( String name, FormItem... formItems )
    {
        FieldSet.Builder fieldSetBuilder = FieldSet.newFieldSet().name( name ).label( name );
        for ( FormItem formItem : formItems )
        {
            fieldSetBuilder.addFormItem( formItem );
        }

        return fieldSetBuilder.build();
    }

    private DataSet createFieldSetData( String name, DataSet... formItemsData )
    {
        DataSet.Builder fieldSetDataBuilder = DataSet.create().name( "Layout" );
        fieldSetDataBuilder.set( "name", name, ValueTypes.STRING ).
            set( "label", name, ValueTypes.STRING );
        DataSet fieldSetData = fieldSetDataBuilder.build();

        DataSet itemsDataSet = new DataSet( "items" );
        for ( DataSet formItemData : formItemsData )
        {
            itemsDataSet.add( formItemData );
        }
        fieldSetData.add( itemsDataSet );
        return fieldSetData;
    }

    private DataSet createOccurrences()
    {
        return DataSet.create().
            name( "occurrences" ).set( "minimum", 0, ValueTypes.LONG ).
            set( "maximum", 1, ValueTypes.LONG ).build();
    }

    private void assertData( Data expected, Data actual )
    {
        String expectedJsonString = jsonHelper.jsonToString( dataJsonSerializer.serialize( expected ) );
        String actualJsonString = jsonHelper.jsonToString( dataJsonSerializer.serialize( actual ) );
        assertEquals( expectedJsonString, actualJsonString );
    }
}
