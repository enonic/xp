package com.enonic.wem.api.support;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.Layout;
import com.enonic.wem.api.form.Occurrences;

public class SerializerForFormItemToData
{

    public List<Data> serializeFormItems( FormItems formItems )
    {
        List<Data> data = new ArrayList<>();
        for ( FormItem formItem : formItems )
        {
            data.add( serializeFormItem( formItem ) );
        }

        return data;
    }

    public Data serializeFormItem( FormItem formItem )
    {

        if ( formItem instanceof Input )
        {
            return serializeInput( (Input) formItem ).copy();
        }
        if ( formItem instanceof FormItemSet )
        {
            return serializeFormItemSet( (FormItemSet) formItem ).copy();
        }
        if ( formItem instanceof Layout )
        {
            return serializeLayout( (Layout) formItem );

        }
        throw new UnsupportedOperationException();
    }

    private DataSet serializeInput( Input input )
    {

        DataSet.Builder inputTypeBuilder = DataSet.newDataSet();
        inputTypeBuilder.name( "inputType" ).set( "name", input.getInputType().getName(), ValueTypes.STRING ).
            set( "builtIn", String.valueOf( input.getInputType().isBuiltIn() ), ValueTypes.STRING );

        String configXml = null;
        if ( input.getInputTypeConfig() != null )
        {
            Element configEl = input.getInputType().getInputTypeConfigXmlSerializer().generate( input.getInputTypeConfig() );
            configXml = new XMLOutputter().outputString( configEl );
        }

        DataSet.Builder inputBuilder = DataSet.newDataSet().name( "input" );
        inputBuilder.set( "name", input.getName(), ValueTypes.STRING );
        inputBuilder.set( "path", input.getPath().toString(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "label", input.getLabel(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "customText", input.getCustomText(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "helpText", input.getHelpText(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "immutable", input.isImmutable(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "indexed", input.isIndexed(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "multiple", input.isMultiple(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "required", input.isRequired(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "inputTypeConfig", configXml, ValueTypes.XML );
        if ( input.getValidationRegexp() != null )
        {
            setNotNullData( inputBuilder, "validationRegexp", input.getValidationRegexp().toString(), ValueTypes.STRING );
        }

        DataSet inputDataSet = inputBuilder.build();
        inputDataSet.add( serializeOccurrences( input.getOccurrences() ) );
        inputDataSet.add( inputTypeBuilder.build() );

        return inputDataSet;
    }


    private DataSet serializeFormItemSet( FormItemSet formItemSet )
    {
        DataSet.Builder formItemSetBuilder = DataSet.newDataSet().name( "formItemSet" ).name( "formItemSet" );
        formItemSetBuilder.set( "name", formItemSet.getName(), ValueTypes.STRING );
        formItemSetBuilder.set( "path", formItemSet.getPath().toString(), ValueTypes.STRING );
        setNotNullData( formItemSetBuilder, "label", formItemSet.getLabel(), ValueTypes.STRING );
        setNotNullData( formItemSetBuilder, "customText", formItemSet.getCustomText(), ValueTypes.STRING );
        setNotNullData( formItemSetBuilder, "helpText", formItemSet.getHelpText(), ValueTypes.STRING );
        setNotNullData( formItemSetBuilder, "immutable", formItemSet.isImmutable(), ValueTypes.STRING );
        setNotNullData( formItemSetBuilder, "multiple", formItemSet.isMultiple(), ValueTypes.STRING );
        setNotNullData( formItemSetBuilder, "required", formItemSet.isRequired(), ValueTypes.STRING );

        DataSet formItemSetData = formItemSetBuilder.build();
        formItemSetData.add( serializeOccurrences( formItemSet.getOccurrences() ) );
        for ( FormItem formItem : formItemSet.getFormItems() )
        {
            formItemSetData.add( serializeFormItem( formItem ) );
        }
        return formItemSetData;
    }

    private DataSet serializeLayout( Layout layout )
    {
        DataSet.Builder layoutBuilder = DataSet.newDataSet().name( "layout" );
        layoutBuilder.set( "name", layout.getName(), ValueTypes.STRING );
        layoutBuilder.set( "path", layout.getPath().toString(), ValueTypes.STRING );

        if ( layout instanceof FieldSet )
        {
            FieldSet fieldSet = (FieldSet) layout;
            layoutBuilder.set( "label", fieldSet.getLabel(), ValueTypes.STRING );
            DataSet fieldSetData = layoutBuilder.build();

            for ( FormItem formItem : fieldSet.getFormItems() )
            {
                fieldSetData.add( serializeFormItem( formItem ) );
            }

            return fieldSetData;
        }

        return layoutBuilder.build();
    }

    private void setNotNullData( DataSet.Builder builder, String name, Object value, ValueType type )
    {
        if ( value != null )
        {
            builder.set( name, value, type );
        }
    }

    private DataSet serializeOccurrences( Occurrences occurrences )
    {
        return DataSet.newDataSet().
            name( "occurrences" ).set( "minimum", occurrences.getMinimum(), ValueTypes.LONG ).
            set( "maximum", occurrences.getMaximum(), ValueTypes.LONG ).build();
    }

}
