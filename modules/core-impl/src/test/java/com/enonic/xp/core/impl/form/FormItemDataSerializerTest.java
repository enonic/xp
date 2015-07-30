package com.enonic.xp.core.impl.form;

/*
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
        PropertyTree generatedData = new PropertyTree(new PropertyTree.PredictivePropertyIdProvider());

        // exercise
        serializer.toData( createInput( "text-area", InputTypes.TEXT_AREA ), generatedData.getRoot() );

        // verify
        PropertyTree expectedData = createInputData( "text-area", InputTypes.TEXT_AREA );
        assertData( expectedData, generatedData );
    }


    @Test
    public void serializeFormItem_given_FormItemSet()
    {

        FormItemSet formItemSet = createFormItemSet( "form-item-set", createInput( "text-line", InputTypes.TEXT_LINE ),
                                                     createInput( "html-area", InputTypes.HTML_AREA ),
                                                     createFormItemSet( "inner-form-item-snew PropertyTree(new PropertyTree.PredictivePropertyIdProvider())PropertyTree generatedData = new PropertyTree();

        // exercise
        serializer.toData( formItemSet, generatedData.getRoot() );

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
                                        new PropertyTree(new PropertyTree.PredictivePropertyIdProvider())html-area", InputTypes.HTML_AREA ) );

        PropertyTree generatedData = new PropertyTree();

        // exercise
        serializer.toData( fieldSet, generatedData.getRoot() );

        // verify
        Data expectedData = createFieldSetData( "field-set", createInputData( "text-line", InputTypes.TEXT_LINE ),
                                                createInputData( "html-area", InputTypes.HTML_AREA ) );
        assertData( expectedData, generatedData );
    }


    @Test
    public void serializeFormItems_given_Inline()
    {
        final InlineMixin inline =
            MixinRefnew PropertyTree(new PropertyTree.PredictivePropertyIdProvider())erence().name( "mymixinreference" ).mixin( "myapplication:mymixinreferencedto" ).build();

        PropertyTree generatedData = new PropertyTree();

        serializer.toData( inline, generatedData.getRoot() );

        final InlineMixin deserializedInline = serializer.deserializeInlineMixin( (DataSet) dataSet );

        Assert.assertEquals( inline.getName(), deserializedInline.getName() );
        Assert.assertEquals( inline.getMixinName(), deserializedInline.getMixinName() );
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
            inputTypeConfig( inpnew PropertyTree(new PropertyTree.PredictivePropertyIdProvider())onfig() ).
            occurrences( 0, 1 ).build();
    }

    private PropertyTree createInputData( String name, InputType inputType )
    {
        PropertyTree tree = new PropertyTree();
        tree.setString( "name", name );
        tree.setString( "label", name );
        tree.setString( "customText", "custom text" );
        tree.setString( "helpText", "help text" );
        tree.setBoolean( "immutable", false );
        tree.setBoolean( "indexed", true );
        PropertySet inputTypeSet = tree.addSet( "inputType" );
        inputTypeSet.setString( "name", inputType.getName() );

        if ( inputType.getDefaultConfig() != null )
        {
            Document configEl = inputType.getInputTypeConfigXmlSerializer().generate( inputType.getDefaultConfig() );
            String configXml = DomHelper.serialize( configEl );
            tree.setXml( "inputTypeConfig", configXml );
        }

        createOccurrences(tree.getRoot());

        return tree;
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

    private void createOccurrences(PropertySet parent)
    {
        PropertySet occurrencesSet = parent.addSet( "occurrences" );
        occurrencesSet.setLong( "minimum", 0L );
        occurrencesSet.setLong( "maximum", 1L );
    }

    private void assertData( PropertyTree expected, PropertyTree actual )
    {
        String expectedJsonString = jsonHelper.jsonToString( dataJsonSerializer.serialize( expected ) );
        String actualJsonString = jsonHelper.jsonToString( dataJsonSerializer.serialize( actual ) );
        assertEquals( expectedJsonString, actualJsonString );
    }
}*/
