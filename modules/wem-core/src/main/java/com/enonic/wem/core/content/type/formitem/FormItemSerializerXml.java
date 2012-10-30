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
import com.enonic.wem.core.content.type.formitem.comptype.ComponentTypeConfigSerializerXml;
import com.enonic.wem.core.content.type.formitem.comptype.ComponentTypeSerializerXml;

import static com.enonic.wem.api.content.type.formitem.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.formitem.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.formitem.Input.newInput;

public class FormItemSerializerXml
{
    private ComponentTypeSerializerXml componentTypeSerializer = new ComponentTypeSerializerXml();

    private ComponentTypeConfigSerializerXml componentTypeConfigSerializer = new ComponentTypeConfigSerializerXml();

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
            return generateComponent( (Input) formItem );
        }
        else if ( formItem instanceof SubTypeReference )
        {
            return generateReference( (SubTypeReference) formItem );
        }
        return null;
    }

    private Element generateComponent( final Input input )
    {
        Element componentEl = new Element( input.getName() );
        componentEl.setAttribute( "form-item-type", Input.class.getSimpleName() );

        componentEl.addContent( componentTypeSerializer.generate( input.getComponentType() ) );

        componentEl.addContent( new Element( "label" ).setText( input.getLabel() ) );
        componentEl.addContent( new Element( "required" ).setText( String.valueOf( input.isRequired() ) ) );
        componentEl.addContent( new Element( "immutable" ).setText( String.valueOf( input.isImmutable() ) ) );
        componentEl.addContent( new Element( "indexed" ).setText( String.valueOf( input.isIndexed() ) ) );
        componentEl.addContent( new Element( "customText" ).setText( input.getCustomText() ) );
        componentEl.addContent( new Element( "helpText" ).setText( input.getHelpText() ) );

        componentEl.addContent( OccurrencesSerializerXml.generate( input.getOccurrences() ) );
        generateValidationRegex( input, componentEl );
        generateComponentTypeConfig( input, componentEl );
        return componentEl;
    }

    private void generateComponentTypeConfig( final Input input, final Element componentEl )
    {
        if ( input.getComponentType().requiresConfig() && input.getComponentTypeConfig() != null )
        {
            componentEl.addContent(
                input.getComponentType().getComponentTypeConfigXmlGenerator().generate( input.getComponentTypeConfig() ) );
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

    private void generateValidationRegex( final Input input, final Element componentEl )
    {
        if ( input.getValidationRegexp() != null )
        {
            componentEl.addContent( new Element( "validationRegex" ).setText( input.getValidationRegexp().toString() ) );
        }
    }

    public FormItem parse( final Element formItemEl )
    {
        final String formItemType = formItemEl.getAttributeValue( "form-item-type" );

        final FormItem formItem;
        if ( formItemType.equals( Input.class.getSimpleName() ) )
        {
            formItem = parseComponent( formItemEl );
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

    private FormItem parseComponent( final Element formItemEl )
    {
        final Input.Builder builder = newInput();
        builder.name( formItemEl.getName() );
        builder.label( formItemEl.getChildText( "label" ) );
        builder.immutable( Boolean.valueOf( formItemEl.getChildText( "immutable" ) ) );
        builder.helpText( formItemEl.getChildText( "helpText" ) );
        builder.customText( formItemEl.getChildText( "customText" ) );
        parseValidationRegexp( builder, formItemEl );

        builder.occurrences( OccurrencesSerializerXml.parse( formItemEl ) );
        parseComponentType( builder, formItemEl );
        parseComponentTypeConfig( builder, formItemEl );

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

    private void parseComponentTypeConfig( final Input.Builder builder, final Element formItemEl )
    {
        builder.componentTypeConfig( componentTypeConfigSerializer.parse( formItemEl ) );
    }

    private void parseComponentType( final Input.Builder builder, final Element formItemEl )
    {
        builder.type( componentTypeSerializer.parse( formItemEl ) );
    }
}
