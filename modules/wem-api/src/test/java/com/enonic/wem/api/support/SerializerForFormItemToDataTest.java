package com.enonic.wem.api.support;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.Layout;
import com.enonic.wem.api.form.inputtype.InputType;
import com.enonic.wem.api.form.inputtype.InputTypes;

import static junit.framework.Assert.assertEquals;

public class SerializerForFormItemToDataTest
{
    private SerializerForFormItemToData serializer;

    @Before
    public void setUp()
    {
        this.serializer = new SerializerForFormItemToData();
    }

    @Test
    public void testFormItemsSerialization()
        throws InvocationTargetException, IllegalAccessException
    {
        FormItems formItems = new FormItems( createFormItemSet( "form-item-set" ) );
        formItems.add( createInput( "text-line", InputTypes.TEXT_LINE ) );
        formItems.add( createInput( "html-area", InputTypes.HTML_AREA ) );
        formItems.add( createFormItemSet( "inner-form-item-set" ) );
        List<Data> generatedData = serializer.serializeFormItems( formItems );
        List<Data> actualData = new ArrayList<>();
        actualData.add( createInputData( "text-line", "form-item-set", InputTypes.TEXT_LINE ) );
        actualData.add( createInputData( "html-area", "form-item-set", InputTypes.HTML_AREA ) );
        actualData.add( createFormItemSetData( "inner-form-item-set", "form-item-set" ) );
        assertEquals( actualData, generatedData );
    }

    @Test
    public void testInputSerialization()
    {
        Data generatedData = serializer.serializeFormItem( createInput( "text-line", InputTypes.TEXT_AREA ) );
        DataSet actualData = createInputData( "text-line", null, InputTypes.TEXT_AREA );
        assertEquals( actualData, generatedData );
    }

    @Test
    public void testFormItemSetSerialization()
    {
        Data generatedData = serializer.serializeFormItem(
            createFormItemSet( "form-item-set", createInput( "text-line", InputTypes.TEXT_LINE ),
                               createInput( "html-area", InputTypes.HTML_AREA ), createFormItemSet( "inner-form-item-set" ) ) );
        Data actualData =
            createFormItemSetData( "form-item-set", null, createInputData( "text-line", "form-item-set", InputTypes.TEXT_LINE ),
                                   createInputData( "html-area", "form-item-set", InputTypes.HTML_AREA ),
                                   createFormItemSetData( "inner-form-item-set", "form-item-set" ) );
        assertEquals( actualData, generatedData );
    }

    @Test
    public void testLayoutSerialization()
    {
        Data generatedData = serializer.serializeFormItem( createFieldSet( "field-set", createInput( "text-line", InputTypes.TEXT_LINE ),
                                                                           createInput( "html-area", InputTypes.HTML_AREA ) ) );
        Data actualData = createFieldSetData( "field-set", createInputData( "text-line", null, InputTypes.TEXT_LINE ),
                                              createInputData( "html-area", null, InputTypes.HTML_AREA ) );
        assertEquals( actualData, generatedData );
    }

    private Input createInput( String name, InputType inputType )
    {

        return Input.newInput().name( name ).customText( "custom text" ).helpText( "help text" ).inputType( inputType ).
            inputTypeConfig( inputType.getDefaultConfig() ).immutable( false ).multiple( false ).indexed( true ).label( name ).
            maximumOccurrences( 1 ).minimumOccurrences( 0 ).required( false ).build();
    }


    private DataSet createInputData( String name, String parentPath, InputType inputType )
    {
        DataSet occurrences = createOccurrences();

        DataSet.Builder inputTypeBuilder = DataSet.newDataSet();
        inputTypeBuilder.name( "inputType" ).set( "name", inputType.getName(), ValueTypes.STRING ).
            set( "builtIn", String.valueOf( inputType.isBuiltIn() ), ValueTypes.STRING );

        DataSet.Builder inputDataBuilder = DataSet.newDataSet().name( "input" ).
            set( "name", name, ValueTypes.STRING ).
            set( "path", parentPath == null ? name : parentPath + "." + name, ValueTypes.STRING ).
            set( "label", name, ValueTypes.STRING ).
            set( "customText", "custom text", ValueTypes.STRING ).
            set( "helpText", "help text", ValueTypes.STRING ).
            set( "immutable", Boolean.FALSE, ValueTypes.STRING ).
            set( "indexed", Boolean.TRUE, ValueTypes.STRING ).
            set( "multiple", Boolean.FALSE, ValueTypes.STRING ).
            set( "required", Boolean.FALSE, ValueTypes.STRING );

        if ( inputType.getDefaultConfig() != null )
        {
            Element configEl = inputType.getInputTypeConfigXmlSerializer().generate( inputType.getDefaultConfig() );
            String configXml = new XMLOutputter().outputString( configEl );
            inputDataBuilder.set( "inputTypeConfig", configXml, ValueTypes.XML );
        }

        DataSet inputData = inputDataBuilder.build();
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

    private DataSet createFormItemSetData( String name, String parentPath, DataSet... formItemsData )
    {
        DataSet.Builder formItemSetBuilder = DataSet.newDataSet().name( "formItemSet" );
        formItemSetBuilder.set( "name", name, ValueTypes.STRING ).
            set( "path", parentPath == null ? name : parentPath + "." + name, ValueTypes.STRING ).
            set( "label", name, ValueTypes.STRING ).
            set( "customText", "custom text", ValueTypes.STRING ).
            set( "helpText", "help text", ValueTypes.STRING ).
            set( "immutable", Boolean.FALSE, ValueTypes.STRING ).
            set( "multiple", Boolean.FALSE, ValueTypes.STRING ).
            set( "required", Boolean.FALSE, ValueTypes.STRING );
        DataSet formItemSetData = formItemSetBuilder.build();
        formItemSetData.add( createOccurrences() );
        for ( DataSet formItemData : formItemsData )
        {
            formItemSetData.add( formItemData );
        }
        return formItemSetData;
    }

    private FieldSet createFieldSet( String name, FormItem... formItems )
    {
        FieldSet.Builder fieldSetBuilder = FieldSet.newFieldSet().name( name ).label( name );
        for ( FormItem formItem : formItems )
        {
            fieldSetBuilder.add( formItem );
        }

        return fieldSetBuilder.build();
    }

    private DataSet createFieldSetData( String name, DataSet... formItemsData )
    {
        DataSet.Builder fieldSetDataBuilder = DataSet.newDataSet().name( "layout" );
        fieldSetDataBuilder.set( "name", name, ValueTypes.STRING ).
            set( "path", "", ValueTypes.STRING ).
            set( "label", name, ValueTypes.STRING );
        DataSet fieldSetData = fieldSetDataBuilder.build();
        for ( DataSet formItemData : formItemsData )
        {
            fieldSetData.add( formItemData );
        }
        return fieldSetData;
    }


    private Layout createLayout( String name )
    {
        return FieldSet.newFieldSet().name( name ).label( name ).build();
    }

    private DataSet createOccurrences()
    {
        return DataSet.newDataSet().
            name( "occurrences" ).set( "minimum", 0, ValueTypes.LONG ).
            set( "maximum", 1, ValueTypes.LONG ).build();
    }
}
