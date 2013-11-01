package com.enonic.wem.core.support;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputType;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.core.data.serializer.DataJsonSerializer;
import com.enonic.wem.core.data.serializer.RootDataSetJsonSerializer;
import com.enonic.wem.core.schema.content.serializer.FormItemsJsonSerializer;

import static junit.framework.Assert.assertEquals;

public class SerializerForFormItemToDataTest
{
    private FormItemsJsonSerializer formItemsJsonSerializer;

    private RootDataSetJsonSerializer rootDataJsonSerializer;

    private DataJsonSerializer dataJsonSerializer;

    private SerializerForFormItemToData serializer;

    private JsonTestHelper jsonHelper;

    public SerializerForFormItemToDataTest()
    {
        this.serializer = new SerializerForFormItemToData();
        jsonHelper = new JsonTestHelper( this, true );

        formItemsJsonSerializer = new FormItemsJsonSerializer();
        formItemsJsonSerializer.prettyPrint();

        rootDataJsonSerializer = new RootDataSetJsonSerializer();
        rootDataJsonSerializer.prettyPrint();

        dataJsonSerializer = new DataJsonSerializer();
        dataJsonSerializer.prettyPrint();
    }

    @Test
    public void serializeFormItem_givenInput()
    {
        // exercise
        Data generatedData = serializer.serializeFormItem( createInput( "text-area", InputTypes.TEXT_AREA ) );

        // verify
        Data expectedData = createInputData( "text-area", InputTypes.TEXT_AREA );
        assertData( expectedData, generatedData );
    }

    @Test
    public void deserializeDatas_givenFormItems()
    {
        // setup
        List<Data> dataList = new ArrayList<>();
        dataList.add( createInputData( "text-area", InputTypes.TEXT_AREA ) );
        dataList.add( createFormItemSetData( "form-item-set", createInputData( "text-line", InputTypes.TEXT_LINE ),
                                             createInputData( "text-area", InputTypes.TEXT_AREA ) ) );
        dataList.add( createFieldSetData( "field-set", createInputData( "text-line", InputTypes.TEXT_LINE ),
                                          createInputData( "text-area", InputTypes.TEXT_AREA ) ) );

        System.out.println( jsonHelper.jsonToString( rootDataJsonSerializer.serialize( listToRootDataSet( dataList ) ) ) );

        // exercise
        FormItems formItems = serializer.deserializeFormItems( dataList );

        // verify
        FormItems expectedFormItems = new FormItems();
        expectedFormItems.add( createInput( "text-area", InputTypes.TEXT_AREA ) );
        expectedFormItems.add( createFormItemSet( "form-item-set", createInput( "text-line", InputTypes.TEXT_LINE ),
                                                  createInput( "text-area", InputTypes.TEXT_AREA ) ) );
        expectedFormItems.add( createFieldSet( "field-set", createInput( "text-line", InputTypes.TEXT_LINE ),
                                               createInput( "text-area", InputTypes.TEXT_AREA ) ) );
        assertFormItems( expectedFormItems, formItems );
    }

    @Test
    public void serializeFormItem_given_FormItemSet()
    {
        // setup
        FormItemSet formItemSet = createFormItemSet( "form-item-set", createInput( "text-line", InputTypes.TEXT_LINE ),
                                                     createInput( "html-area", InputTypes.HTML_AREA ),
                                                     createFormItemSet( "inner-form-item-set" ) );

        // exercise
        Data generatedData = serializer.serializeFormItem( formItemSet );

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
        Data generatedData = serializer.serializeFormItem( fieldSet );

        // verify
        Data expectedData = createFieldSetData( "field-set", createInputData( "text-line", InputTypes.TEXT_LINE ),
                                                createInputData( "html-area", InputTypes.HTML_AREA ) );
        assertData( expectedData, generatedData );
    }

    @Test
    public void serializeFormItems_given_FormItems()
        throws InvocationTargetException, IllegalAccessException
    {
        // setup
        FormItems formItems = new FormItems();
        formItems.add( createInput( "text-line", InputTypes.TEXT_LINE ) );
        formItems.add( createInput( "html-area", InputTypes.HTML_AREA ) );
        formItems.add( createFormItemSet( "form-item-set" ) );

        // exercise
        List<Data> generatedData = serializer.serializeFormItems( formItems );

        // verify
        List<Data> expectedData = new ArrayList<>();
        expectedData.add( createInputData( "text-line", InputTypes.TEXT_LINE ) );
        expectedData.add( createInputData( "html-area", InputTypes.HTML_AREA ) );
        expectedData.add( createFormItemSetData( "form-item-set" ) );

        assertDataList( expectedData, generatedData );
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
        DataSet.Builder inputTypeBuilder = DataSet.newDataSet();
        inputTypeBuilder.name( "inputType" ).set( "name", inputType.getName(), ValueTypes.STRING );

        DataSet.Builder inputDataBuilder = DataSet.newDataSet().name( "Input" ).
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
        DataSet.Builder formItemSetBuilder = DataSet.newDataSet().name( "FormItemSet" );
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
        DataSet.Builder fieldSetDataBuilder = DataSet.newDataSet().name( "Layout" );
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
        return DataSet.newDataSet().
            name( "occurrences" ).set( "minimum", 0, ValueTypes.LONG ).
            set( "maximum", 1, ValueTypes.LONG ).build();
    }


    private void assertDataList( List<Data> expected, List<Data> actual )
    {
        String expectedJsonString = jsonHelper.jsonToString( rootDataJsonSerializer.serialize( listToRootDataSet( expected ) ) );
        String actualJsonString = jsonHelper.jsonToString( rootDataJsonSerializer.serialize( listToRootDataSet( actual ) ) );
        assertEquals( expectedJsonString, actualJsonString );
    }

    private void assertData( Data expected, Data actual )
    {
        String expectedJsonString = jsonHelper.jsonToString( dataJsonSerializer.serialize( expected ) );
        String actualJsonString = jsonHelper.jsonToString( dataJsonSerializer.serialize( actual ) );
        assertEquals( expectedJsonString, actualJsonString );
    }

    private void assertFormItems( FormItems expected, FormItems actual )
    {
        String expectedJsonString = jsonHelper.jsonToString( formItemsJsonSerializer.serialize( expected ) );
        String actualJsonString = jsonHelper.jsonToString( formItemsJsonSerializer.serialize( actual ) );
        assertEquals( expectedJsonString, actualJsonString );
    }

    private RootDataSet listToRootDataSet( List<Data> list )
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.addAll( list );
        return rootDataSet;
    }
}
