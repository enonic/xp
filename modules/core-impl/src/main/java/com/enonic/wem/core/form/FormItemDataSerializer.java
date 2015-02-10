package com.enonic.wem.core.form;

import org.w3c.dom.Document;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.FormItemType;
import com.enonic.wem.api.form.InlineMixin;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.Layout;
import com.enonic.wem.api.form.Occurrences;
import com.enonic.wem.api.form.inputtype.InputType;
import com.enonic.wem.api.form.inputtype.InputTypeConfig;
import com.enonic.wem.api.form.inputtype.InputTypeResolver;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;
import com.enonic.wem.api.xml.DomHelper;

import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;

public class FormItemDataSerializer
    extends AbstractDataSetSerializer<FormItem, FormItem>
{
    private final FormItemType type;

    public FormItemDataSerializer( final FormItemType type )
    {
        this.type = type;
    }

    public void toData( final FormItem formItem, final PropertySet parent )
    {
        serializeFormItem( formItem, parent );
    }

    public FormItem fromData( final PropertySet formItemPropertySet )
    {
        return deserializeFormItem( formItemPropertySet );
    }

    private void serializeFormItem( FormItem formItem, final PropertySet parent )
    {
        final PropertySet formItemAsSet = parent.addSet( formItem.getClass().getSimpleName() );
        if ( formItem instanceof Input )
        {
            serializeInput( (Input) formItem, formItemAsSet );
        }
        else if ( formItem instanceof InlineMixin )
        {
            serializeInlineMixin( (InlineMixin) formItem, formItemAsSet );
        }
        else if ( formItem instanceof FormItemSet )
        {
            serializeFormItemSet( (FormItemSet) formItem, formItemAsSet );
        }
        else if ( formItem instanceof Layout )
        {
            serializeLayout( (Layout) formItem, formItemAsSet );
        }
        else
        {
            throw new UnsupportedOperationException( "FormItem not serializable: " + formItem.getClass().getSimpleName() );
        }
    }

    private void serializeInput( final Input input, final PropertySet inputSet )
    {
        final PropertySet inputTypeSet = inputSet.addSet( "inputType" );
        inputTypeSet.setString( "name", input.getInputType().getName() );
        inputSet.ifNotNull().setString( "name", input.getName() );
        inputSet.ifNotNull().setString( "label", input.getLabel() );
        inputSet.ifNotNull().setString( "customText", input.getCustomText() );
        inputSet.ifNotNull().setString( "helpText", input.getHelpText() );
        inputSet.ifNotNull().setBoolean( "immutable", input.isImmutable() );
        inputSet.ifNotNull().setBoolean( "indexed", input.isIndexed() );
        inputSet.ifNotNull().setXml( "inputTypeConfig", serializeInputTypeConfig( input ) );
        inputSet.ifNotNull().setString( "validationRegexp",
                                        input.getValidationRegexp() != null ? input.getValidationRegexp().toString() : null );
        serializeOccurrences( input.getOccurrences(), inputSet );
    }

    @SuppressWarnings("unchecked")
    private String serializeInputTypeConfig( final Input input )
    {
        if ( input.getInputTypeConfig() == null )
        {
            return null;
        }

        final Document doc = input.getInputType().getInputTypeConfigXmlSerializer().generate( input.getInputTypeConfig() );
        return DomHelper.serialize( doc );
    }

    private void serializeInlineMixin( final InlineMixin inline, final PropertySet inlineSet )
    {
        inlineSet.setString( "name", inline.getName() );
        inlineSet.ifNotNull().setString( "mixinName",
                                                 inline.getMixinName() != null ? inline.getMixinName().toString() : null );
    }

    InlineMixin deserializeInlineMixin( final PropertySet inlineAsSet )
    {
        final InlineMixin.Builder builder = InlineMixin.newInlineMixin();
        builder.mixin( inlineAsSet.getString( "mixinName" ) );
        return builder.build();
    }

    private void serializeFormItemSet( FormItemSet formItemSet, final PropertySet formItemSetAsSet )
    {
        formItemSetAsSet.setString( "name", formItemSet.getName() );
        formItemSetAsSet.ifNotNull().setString( "label", formItemSet.getLabel() );
        formItemSetAsSet.ifNotNull().setString( "customText", formItemSet.getCustomText() );
        formItemSetAsSet.ifNotNull().setString( "helpText", formItemSet.getHelpText() );
        formItemSetAsSet.ifNotNull().setBoolean( "immutable", formItemSet.isImmutable() );

        serializeOccurrences( formItemSet.getOccurrences(), formItemSetAsSet );

        final PropertySet itemsAsSet = formItemSetAsSet.addSet( "items" );
        for ( FormItem formItem : formItemSet.getFormItems() )
        {
            serializeFormItem( formItem, itemsAsSet );
        }
    }

    private void serializeLayout( final Layout layout, final PropertySet layoutAsSet )
    {
        if ( layout instanceof FieldSet )
        {
            final FieldSet fieldSet = (FieldSet) layout;
            layoutAsSet.setString( "name", layout.getName() );
            layoutAsSet.setString( "label", fieldSet.getLabel() );

            final PropertySet itemsAsSet = layoutAsSet.addSet( "items" );
            for ( FormItem formItem : fieldSet.getFormItems() )
            {
                new FormItemDataSerializer( formItem.getType() ).toData( formItem, itemsAsSet );
            }
        }
        else
        {
            throw new UnsupportedOperationException( "Unsupported Layout: " + layout.getClass().getSimpleName() );
        }
    }

    private void serializeOccurrences( final Occurrences occurrences, final PropertySet parent )
    {
        final PropertySet occurrencesSet = parent.addSet( "occurrences" );
        occurrencesSet.setLong( "minimum", (long) occurrences.getMinimum() );
        occurrencesSet.setLong( "maximum", (long) occurrences.getMaximum() );
    }

    private FormItem deserializeFormItem( final PropertySet formItemPropertySet )
    {
        if ( type.equals( FormItemType.INPUT ) )
        {
            return deserializeInput( formItemPropertySet );
        }
        else if ( type.equals( FormItemType.FORM_ITEM_SET ) )
        {
            return deserializeFormItemSet( formItemPropertySet );
        }
        else if ( type.equals( FormItemType.LAYOUT ) )
        {
            return deserializeLayout( formItemPropertySet );
        }
        else if ( type.equals( FormItemType.MIXIN_REFERENCE ) )
        {
            return deserializeInlineMixin( formItemPropertySet );
        }
        else
        {
            throw new UnsupportedOperationException( "FormItem not serializable: " + type );
        }
    }

    private Input deserializeInput( final PropertySet inputAsSet )
    {
        final Input.Builder builder = newInput();

        final PropertySet inputTypeAsDataSet = inputAsSet.getSet( "inputType" );
        final InputType inputType = InputTypeResolver.get().resolve( inputTypeAsDataSet.getString( "name" ) );
        builder.inputType( inputType );

        if ( inputAsSet.hasProperty( "inputTypeConfig" ) )
        {
            builder.inputTypeConfig( deserializeInputTypeConfig( inputAsSet, inputType ) );
        }

        builder.name( inputAsSet.getString( "name" ) );
        builder.label( inputAsSet.getString( "label" ) );
        builder.customText( inputAsSet.getString( "customText" ) );
        builder.helpText( inputAsSet.getString( "helpText" ) );
        builder.immutable( inputAsSet.getBoolean( "immutable" ) );
        builder.indexed( inputAsSet.getBoolean( "indexed" ) );

        if ( inputAsSet.hasProperty( "occurrences" ) )
        {
            builder.occurrences( deserializeOccurrences( inputAsSet.getSet( "occurrences" ) ) );
        }
        if ( inputAsSet.hasProperty( "validationRegexp" ) )
        {
            builder.validationRegexp( inputAsSet.getString( "validationRegexp" ) );
        }

        return builder.build();
    }

    private InputTypeConfig deserializeInputTypeConfig( final PropertySet inputAsDataSet, final InputType inputType )
    {
        final String xmlAsString = inputAsDataSet.getString( "inputTypeConfig" );
        final Document doc = DomHelper.parse( xmlAsString );
        return inputType.getInputTypeConfigXmlSerializer().parseConfig( doc );
    }

    private Occurrences deserializeOccurrences( final PropertySet occurrencesAsSet )
    {
        return new Occurrences( occurrencesAsSet.getLong( "minimum" ).intValue(), occurrencesAsSet.getLong( "maximum" ).intValue() );
    }

    private FormItemSet deserializeFormItemSet( final PropertySet formItemAsDataSet )
    {
        final FormItemSet.Builder builder = newFormItemSet();
        builder.name( formItemAsDataSet.getString( "name" ) );
        builder.label( formItemAsDataSet.getString( "label" ) );
        builder.customText( formItemAsDataSet.getString( "customText" ) );
        builder.helpText( formItemAsDataSet.getString( "helpText" ) );
        builder.immutable( formItemAsDataSet.getBoolean( "immutable" ) );

        if ( formItemAsDataSet.hasProperty( "occurrences" ) )
        {
            builder.occurrences( deserializeOccurrences( formItemAsDataSet.getSet( "occurrences" ) ) );
        }

        final PropertySet itemsDataSet = formItemAsDataSet.getSet( "items" );
        for ( final Property itemAsProperty : itemsDataSet.getProperties() )
        {
            builder.addFormItem(
                new FormItemDataSerializer( FormItemType.parse( itemAsProperty.getName() ) ).fromData( itemAsProperty.getSet() ) );
        }

        return builder.build();
    }

    private Layout deserializeLayout( final PropertySet layoutAsDataSet )
    {
        return deserializeFieldSet( layoutAsDataSet );
    }

    private FieldSet deserializeFieldSet( final PropertySet fieldSetAsDataSet )
    {
        final FieldSet.Builder builder = newFieldSet();
        builder.name( fieldSetAsDataSet.getString( "name" ) );
        builder.label( fieldSetAsDataSet.getString( "label" ) );
        final PropertySet itemsAsSet = fieldSetAsDataSet.getSet( "items" );
        for ( final Property itemAsProperty : itemsAsSet.getProperties() )
        {
            builder.addFormItem(
                new FormItemDataSerializer( FormItemType.parse( itemAsProperty.getName() ) ).fromData( itemAsProperty.getSet() ) );
        }

        return builder.build();
    }
}

