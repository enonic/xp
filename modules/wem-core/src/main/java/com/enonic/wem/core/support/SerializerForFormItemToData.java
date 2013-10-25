package com.enonic.wem.core.support;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.Layout;
import com.enonic.wem.api.form.Occurrences;
import com.enonic.wem.api.form.inputtype.InputType;
import com.enonic.wem.api.form.inputtype.InputTypeConfig;
import com.enonic.wem.core.form.inputtype.InputTypeResolver;

import static com.enonic.wem.api.data.DataSet.newDataSet;
import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;

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

    private DataSet serializeInput( final Input input )
    {
        final DataSet inputType = new DataSet( "inputType" );
        inputType.setProperty( "name", new Value.String( input.getInputType().getName() ) );

        final DataSet.Builder inputBuilder = newDataSet().name( "Input" );
        inputBuilder.set( "name", input.getName(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "label", input.getLabel(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "customText", input.getCustomText(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "helpText", input.getHelpText(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "immutable", input.isImmutable(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "indexed", input.isIndexed(), ValueTypes.STRING );
        setNotNullData( inputBuilder, "inputTypeConfig", serializeInputTypeConfig( input ), ValueTypes.XML );
        if ( input.getValidationRegexp() != null )
        {
            setNotNullData( inputBuilder, "validationRegexp", input.getValidationRegexp().toString(), ValueTypes.STRING );
        }

        final DataSet inputDataSet = inputBuilder.build();
        inputDataSet.add( serializeOccurrences( input.getOccurrences() ) );
        inputDataSet.add( inputType );

        return inputDataSet;
    }

    @SuppressWarnings("unchecked")
    private String serializeInputTypeConfig( final Input input )
    {
        String configXml = null;
        if ( input.getInputTypeConfig() != null )
        {
            Element configEl = input.getInputType().getInputTypeConfigXmlSerializer().generate( input.getInputTypeConfig() );
            configXml = new XMLOutputter().outputString( configEl );
        }
        return configXml;
    }


    private DataSet serializeFormItemSet( FormItemSet formItemSet )
    {
        DataSet.Builder formItemSetBuilder = newDataSet().name( "FormItemSet" );
        formItemSetBuilder.set( "name", formItemSet.getName(), ValueTypes.STRING );
        setNotNullData( formItemSetBuilder, "label", formItemSet.getLabel(), ValueTypes.STRING );
        setNotNullData( formItemSetBuilder, "customText", formItemSet.getCustomText(), ValueTypes.STRING );
        setNotNullData( formItemSetBuilder, "helpText", formItemSet.getHelpText(), ValueTypes.STRING );
        setNotNullData( formItemSetBuilder, "immutable", formItemSet.isImmutable(), ValueTypes.STRING );

        DataSet formItemSetData = formItemSetBuilder.build();
        formItemSetData.add( serializeOccurrences( formItemSet.getOccurrences() ) );

        DataSet formItemsDataSet = new DataSet( "items" );
        for ( FormItem formItem : formItemSet.getFormItems() )
        {
            formItemsDataSet.add( serializeFormItem( formItem ) );
        }
        formItemSetData.add( formItemsDataSet );
        return formItemSetData;
    }

    private DataSet serializeLayout( Layout layout )
    {
        DataSet.Builder layoutBuilder = newDataSet().name( "Layout" );
        layoutBuilder.set( "name", layout.getName(), ValueTypes.STRING );

        if ( layout instanceof FieldSet )
        {
            FieldSet fieldSet = (FieldSet) layout;
            layoutBuilder.set( "label", fieldSet.getLabel(), ValueTypes.STRING );
            DataSet fieldSetData = layoutBuilder.build();

            DataSet formItemsDataSet = new DataSet( "items" );
            for ( FormItem formItem : fieldSet.getFormItems() )
            {
                formItemsDataSet.add( serializeFormItem( formItem ) );
            }
            fieldSetData.add( formItemsDataSet );

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
        final DataSet dataSet = new DataSet( "occurrences" );
        dataSet.setProperty( "minimum", new Value.Long( occurrences.getMinimum() ) );
        dataSet.setProperty( "maximum", new Value.Long( occurrences.getMaximum() ) );
        return dataSet;
    }

    public FormItems deserializeFormItems( final Iterable<Data> formItemsAsData )
    {
        FormItems formItems = new FormItems();
        for ( final Data formItemAsData : formItemsAsData )
        {
            formItems.add( deserializeFormItem( formItemAsData ) );
        }
        return formItems;
    }

    private FormItem deserializeFormItem( final Data formItemAsData )
    {
        if ( formItemAsData.isDataSet() )
        {
            final DataSet formItemAsDataSet = formItemAsData.toDataSet();
            final String formItemType = formItemAsDataSet.getName();

            if ( "Input".equals( formItemType ) )
            {
                return deserializeInput( formItemAsDataSet );
            }
            else if ( "FormItemSet".equals( formItemType ) )
            {
                return deserializeFormItemSet( formItemAsDataSet );
            }
            else if ( "Layout".equals( formItemType ) )
            {
                return deserializeLayout( formItemAsDataSet );
            }
        }
        return null;
    }

    private Input deserializeInput( final DataSet inputAsDataSet )
    {
        final Input.Builder builder = newInput();

        final DataSet inputTypeAsDataSet = inputAsDataSet.getDataSet( "inputType" );
        final InputType inputType = InputTypeResolver.get().resolve( inputTypeAsDataSet.getProperty( "name" ).getString() );
        builder.inputType( inputType );

        if ( inputAsDataSet.hasData( "inputTypeConfig" ) )
        {
            builder.inputTypeConfig( deserializeInputTypeConfig( inputAsDataSet, inputType ) );
        }

        if ( inputAsDataSet.hasData( "name" ) )
        {
            builder.name( inputAsDataSet.getProperty( "name" ).getString() );
        }
        if ( inputAsDataSet.hasData( "label" ) )
        {
            builder.label( inputAsDataSet.getProperty( "label" ).getString() );
        }
        if ( inputAsDataSet.hasData( "customText" ) )
        {
            builder.customText( inputAsDataSet.getProperty( "customText" ).getString() );
        }
        if ( inputAsDataSet.hasData( "helpText" ) )
        {
            builder.helpText( inputAsDataSet.getProperty( "helpText" ).getString() );
        }
        if ( inputAsDataSet.hasData( "immutable" ) )
        {
            builder.immutable( Boolean.valueOf( inputAsDataSet.getProperty( "immutable" ).getString() ) );
        }
        if ( inputAsDataSet.hasData( "indexed" ) )
        {
            builder.indexed( Boolean.valueOf( inputAsDataSet.getProperty( "indexed" ).getString() ) );
        }
        if ( inputAsDataSet.hasData( "occurrences" ) )
        {
            builder.occurrences( deserializeOccurrences( inputAsDataSet.getDataSet( "occurrences" ) ) );
        }
        if ( inputAsDataSet.hasData( "validationRegexp" ) )
        {
            builder.validationRegexp( inputAsDataSet.getProperty( "validationRegexp" ).getString() );
        }

        return builder.build();
    }

    private InputTypeConfig deserializeInputTypeConfig( final DataSet inputAsDataSet, final InputType inputType )
    {
        final String xmlAsString = inputAsDataSet.getProperty( "inputTypeConfig" ).getString();
        final Element rootElement;
        try
        {
            rootElement = new SAXBuilder().build( new StringReader( xmlAsString ) ).getRootElement();
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Failed to deserializeInputTypeConfig", e );
        }

        return inputType.getInputTypeConfigXmlSerializer().parseConfig( rootElement );
    }

    private Occurrences deserializeOccurrences( final DataSet occurrencesAsDataSet )
    {
        return new Occurrences( occurrencesAsDataSet.getProperty( "minimum" ).getLong().intValue(),
                                occurrencesAsDataSet.getProperty( "maximum" ).getLong().intValue() );
    }

    private FormItemSet deserializeFormItemSet( final DataSet formItemAsDataSet )
    {
        final FormItemSet.Builder builder = newFormItemSet();
        if ( formItemAsDataSet.hasData( "name" ) )
        {
            builder.name( formItemAsDataSet.getProperty( "name" ).getString() );
        }
        if ( formItemAsDataSet.hasData( "label" ) )
        {
            builder.label( formItemAsDataSet.getProperty( "label" ).getString() );
        }
        if ( formItemAsDataSet.hasData( "customText" ) )
        {
            builder.customText( formItemAsDataSet.getProperty( "customText" ).getString() );
        }
        if ( formItemAsDataSet.hasData( "helpText" ) )
        {
            builder.helpText( formItemAsDataSet.getProperty( "helpText" ).getString() );
        }
        if ( formItemAsDataSet.hasData( "immutable" ) )
        {
            builder.immutable( Boolean.valueOf( formItemAsDataSet.getProperty( "immutable" ).getString() ) );
        }
        if ( formItemAsDataSet.hasData( "occurrences" ) )
        {
            builder.occurrences( deserializeOccurrences( formItemAsDataSet.getDataSet( "occurrences" ) ) );
        }

        final DataSet itemsDataSet = formItemAsDataSet.getDataSet( "items" );
        for ( final Data data : itemsDataSet )
        {
            builder.addFormItem( deserializeFormItem( data ) );
        }

        return builder.build();
    }

    private Layout deserializeLayout( final DataSet layoutAsDataSet )
    {
        return deserializeFieldSet( layoutAsDataSet );
    }

    private FieldSet deserializeFieldSet( final DataSet fieldSetAsDataSet )
    {
        final FieldSet.Builder builder = newFieldSet();
        if ( fieldSetAsDataSet.hasData( "name" ) )
        {
            builder.name( fieldSetAsDataSet.getProperty( "name" ).getString() );
        }
        if ( fieldSetAsDataSet.hasData( "label" ) )
        {
            builder.label( fieldSetAsDataSet.getProperty( "label" ).getString() );
        }

        final DataSet itemsDataSet = fieldSetAsDataSet.getDataSet( "items" );
        for ( final Data data : itemsDataSet )
        {
            builder.add( deserializeFormItem( data ) );
        }

        return builder.build();
    }
}

