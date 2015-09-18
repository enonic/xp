package com.enonic.xp.core.impl.form;

/*
public class FormItemsDataSerializerTest
{
    private FormItemsJsonSerializer formItemsJsonSerializer;

    private RootDataSetJsonSerializer rootDataJsonSerializer;

    private DataJsonSerializer dataJsonSerializer;

    private FormItemsDataSerializer serializer;

    private JsonTestHelper jsonHelper;

    public FormItemsDataSerializerTest()
    {
        this.serializer = new FormItemsDataSerializer();
        jsonHelper = new JsonTestHelper( this, true );

        formItemsJsonSerializer = new FormItemsJsonSerializer();
        formItemsJsonSerializer.prettyPrint();

        rootDataJsonSerializer = new RootDataSetJsonSerializer();
        rootDataJsonSerializer.prettyPrint();

        dataJsonSerializer = new DataJsonSerializer();
        dataJsonSerializer.prettyPrint();
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
        FormItems formItems = serializer.fromData( dataList );

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
    public void serializeFormItems_given_FormItems()
        throws InvocationTargetException, IllegalAccessException
    {
        // setup
        FormItems formItems = new FormItems();
        formItems.add( createInput( "text-line", InputTypes.TEXT_LINE ) );
        formItems.add( createInput( "html-area", InputTypes.HTML_AREA ) );
        formItems.add( createFormItemSet( "form-item-set" ) );

        // exercise
        List<Data> generatedData = serializer.toData( formItems );

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
            Document configEl = inputType.getInputTypeConfigXmlSerializer().generate( inputType.getDefaultConfig() );
            String configXml = DomHelper.serialize( configEl );
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


    private void assertDataList( List<Data> expected, List<Data> actual )
    {
        String expectedJsonString = jsonHelper.jsonToString( rootDataJsonSerializer.serialize( listToRootDataSet( expected ) ) );
        String actualJsonString = jsonHelper.jsonToString( rootDataJsonSerializer.serialize( listToRootDataSet( actual ) ) );
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
*/