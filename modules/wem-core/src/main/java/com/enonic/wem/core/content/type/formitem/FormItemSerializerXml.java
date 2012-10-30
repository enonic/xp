package com.enonic.wem.core.content.type.formitem;


import org.jdom.Element;

import com.enonic.wem.api.content.type.formitem.FieldSet;
import com.enonic.wem.api.content.type.formitem.FormItem;
import com.enonic.wem.api.content.type.formitem.FormItemSet;
import com.enonic.wem.api.content.type.formitem.FormItems;
import com.enonic.wem.api.content.type.formitem.HierarchicalFormItem;
import com.enonic.wem.api.content.type.formitem.Input;
import com.enonic.wem.api.content.type.formitem.Layout;
import com.enonic.wem.api.content.type.formitem.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.formitem.SubTypeReference;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.formitem.comptype.InputTypeConfigSerializerXml;
import com.enonic.wem.core.content.type.formitem.comptype.InputTypeSerializerXml;

import static com.enonic.wem.api.content.type.formitem.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.formitem.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.formitem.Input.newInput;

public class FormItemSerializerXml
{
    private InputTypeSerializerXml inputTypeSerializer = new InputTypeSerializerXml();

    private InputTypeConfigSerializerXml inputTypeConfigSerializer = new InputTypeConfigSerializerXml();

    private final FormItemsSerializerXml formItemsSerializer;

    public FormItemSerializerXml()
    {
        this.formItemsSerializer = new FormItemsSerializerXml();
    }

    public FormItemSerializerXml( final FormItemsSerializerXml formItemsSerializer )
    {
        this.formItemsSerializer = formItemsSerializer;
    }

    public Element generate( FormItem formItem )
    {
        if ( formItem instanceof FormItemSet )
        {
            return generateFormItemSet( (FormItemSet) formItem );
        }
        else if ( formItem instanceof Layout )
        {
            return generateLayout( (Layout) formItem );
        }
        else if ( formItem instanceof Input )
        {
            return generateInput( (Input) formItem );
        }
        else if ( formItem instanceof SubTypeReference )
        {
            return generateReference( (SubTypeReference) formItem );
        }
        return null;
    }

    private Element generateInput( final Input input )
    {
        Element inputEl = new Element( input.getName() );
        inputEl.setAttribute( "form-item-type", Input.class.getSimpleName() );

        inputEl.addContent( inputTypeSerializer.generate( input.getInputType() ) );

        inputEl.addContent( new Element( "label" ).setText( input.getLabel() ) );
        inputEl.addContent( new Element( "required" ).setText( String.valueOf( input.isRequired() ) ) );
        inputEl.addContent( new Element( "immutable" ).setText( String.valueOf( input.isImmutable() ) ) );
        inputEl.addContent( new Element( "indexed" ).setText( String.valueOf( input.isIndexed() ) ) );
        inputEl.addContent( new Element( "customText" ).setText( input.getCustomText() ) );
        inputEl.addContent( new Element( "helpText" ).setText( input.getHelpText() ) );

        inputEl.addContent( OccurrencesSerializerXml.generate( input.getOccurrences() ) );
        generateValidationRegex( input, inputEl );
        generateInputTypeConfig( input, inputEl );
        return inputEl;
    }

    private void generateInputTypeConfig( final Input input, final Element inputEl )
    {
        if ( input.getInputType().requiresConfig() && input.getInputTypeConfig() != null )
        {
            inputEl.addContent( input.getInputType().getInputTypeConfigXmlGenerator().generate( input.getInputTypeConfig() ) );
        }
    }

    private Element generateFormItemSet( final FormItemSet formItemSet )
    {
        Element formItemSetEl = new Element( formItemSet.getName() );
        formItemSetEl.setAttribute( "form-item-type", FormItemSet.class.getSimpleName() );

        formItemSetEl.addContent( new Element( "label" ).setText( formItemSet.getLabel() ) );
        formItemSetEl.addContent( new Element( "required" ).setText( String.valueOf( formItemSet.isRequired() ) ) );
        formItemSetEl.addContent( new Element( "immutable" ).setText( String.valueOf( formItemSet.isImmutable() ) ) );
        formItemSetEl.addContent( new Element( "customText" ).setText( formItemSet.getCustomText() ) );
        formItemSetEl.addContent( new Element( "helpText" ).setText( formItemSet.getCustomText() ) );

        formItemSetEl.addContent( OccurrencesSerializerXml.generate( formItemSet.getOccurrences() ) );
        formItemSetEl.addContent( formItemsSerializer.generate( formItemSet.getFormItems() ) );
        return formItemSetEl;
    }

    private Element generateLayout( final Layout layout )
    {
        Element layoutEl = new Element( layout.getName() );
        layoutEl.setAttribute( "form-item-type", Layout.class.getSimpleName() );

        if ( layout instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) layout, layoutEl );
        }

        return layoutEl;
    }

    private void generateFieldSet( final FieldSet fieldSet, final Element layoutEl )
    {
        layoutEl.setAttribute( "layout-type", FieldSet.class.getSimpleName() );
        layoutEl.addContent( new Element( "label" ).setText( fieldSet.getLabel() ) );
        layoutEl.addContent( formItemsSerializer.generate( fieldSet.getFormItems() ) );
    }

    private Element generateReference( final SubTypeReference subTypeReference )
    {
        Element referenceEl = new Element( subTypeReference.getName() );
        referenceEl.setAttribute( "form-item-type", SubTypeReference.class.getSimpleName() );
        referenceEl.addContent( new Element( "reference" ).setText( subTypeReference.getSubTypeQualifiedName().toString() ) );
        referenceEl.addContent( new Element( "subTypeClass" ).setText( subTypeReference.getSubTypeClass().getSimpleName() ) );
        return referenceEl;
    }

    private void generateValidationRegex( final Input input, final Element inputEl )
    {
        if ( input.getValidationRegexp() != null )
        {
            inputEl.addContent( new Element( "validationRegex" ).setText( input.getValidationRegexp().toString() ) );
        }
    }

    public FormItem parse( final Element formItemEl )
    {
        final String formItemType = formItemEl.getAttributeValue( "form-item-type" );

        final FormItem formItem;
        if ( formItemType.equals( Input.class.getSimpleName() ) )
        {
            formItem = parseInput( formItemEl );
        }
        else if ( formItemType.equals( FormItemSet.class.getSimpleName() ) )
        {
            formItem = parseFormItemSet( formItemEl );
        }
        else if ( formItemType.equals( Layout.class.getSimpleName() ) )
        {
            formItem = parseLayout( formItemEl );
        }
        else if ( formItemType.equals( SubTypeReference.class.getSimpleName() ) )
        {
            formItem = parseSubTypeReference( formItemEl );
        }
        else
        {
            throw new JsonParsingException( "Unknown FormItemType: " + formItemType );
        }

        return formItem;
    }

    private FormItem parseInput( final Element formItemEl )
    {
        final Input.Builder builder = newInput();
        builder.name( formItemEl.getName() );
        builder.label( formItemEl.getChildText( "label" ) );
        builder.immutable( Boolean.valueOf( formItemEl.getChildText( "immutable" ) ) );
        builder.helpText( formItemEl.getChildText( "helpText" ) );
        builder.customText( formItemEl.getChildText( "customText" ) );
        parseValidationRegexp( builder, formItemEl );

        builder.occurrences( OccurrencesSerializerXml.parse( formItemEl ) );
        parseInputType( builder, formItemEl );
        parseInputTypeConfig( builder, formItemEl );

        return builder.build();
    }

    private HierarchicalFormItem parseFormItemSet( final Element formItemEl )
    {
        final FormItemSet.Builder builder = newFormItemSet();
        builder.name( formItemEl.getName() );
        builder.label( formItemEl.getChildText( "label" ) );
        builder.required( Boolean.valueOf( formItemEl.getChildText( "required" ) ) );
        builder.immutable( Boolean.valueOf( formItemEl.getChildText( "immutable" ) ) );
        builder.helpText( formItemEl.getChildText( "helpText" ) );
        builder.customText( formItemEl.getChildText( "customText" ) );

        builder.occurrences( OccurrencesSerializerXml.parse( formItemEl ) );

        final FormItems formItems = formItemsSerializer.parse( formItemEl );
        for ( FormItem formItem : formItems.iterable() )
        {
            builder.add( formItem );
        }

        return builder.build();
    }

    private FormItem parseLayout( final Element formItemEl )
    {
        String layoutType = formItemEl.getAttributeValue( "layout-type" );
        if ( layoutType.equals( FieldSet.class.getSimpleName() ) )
        {
            return parseFieldSet( formItemEl );
        }
        else
        {
            throw new JsonParsingException( "Unknown layoutType: " + layoutType );
        }
    }

    private FormItem parseFieldSet( final Element formItemEl )
    {
        final FieldSet.Builder builder = newFieldSet();
        builder.name( formItemEl.getName() );
        builder.label( formItemEl.getChildText( "label" ) );

        final FormItems formItems = formItemsSerializer.parse( formItemEl );
        for ( FormItem formItem : formItems.iterable() )
        {
            builder.add( formItem );
        }

        return builder.build();
    }

    private HierarchicalFormItem parseSubTypeReference( final Element formItemEl )
    {
        final SubTypeReference.Builder builder = SubTypeReference.newSubTypeReference();
        builder.name( formItemEl.getName() );
        builder.subType( new SubTypeQualifiedName( formItemEl.getChildText( "reference" ) ) );
        builder.type( formItemEl.getChildText( "subTypeClass" ) );
        return builder.build();
    }

    private void parseValidationRegexp( final Input.Builder builder, final Element formItemEl )
    {
        String validationRegexp = formItemEl.getChildText( "validationRegex" );
        if ( validationRegexp != null )
        {
            builder.validationRegexp( validationRegexp );
        }
    }

    private void parseInputTypeConfig( final Input.Builder builder, final Element formItemEl )
    {
        builder.inputTypeConfig( inputTypeConfigSerializer.parse( formItemEl ) );
    }

    private void parseInputType( final Input.Builder builder, final Element formItemEl )
    {
        builder.type( inputTypeSerializer.parse( formItemEl ) );
    }
}
