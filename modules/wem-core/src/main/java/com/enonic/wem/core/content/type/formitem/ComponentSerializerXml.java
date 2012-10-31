package com.enonic.wem.core.content.type.formitem;


import org.jdom.Element;

import com.enonic.wem.api.content.type.formitem.Component;
import com.enonic.wem.api.content.type.formitem.ComponentSet;
import com.enonic.wem.api.content.type.formitem.Components;
import com.enonic.wem.api.content.type.formitem.FieldSet;
import com.enonic.wem.api.content.type.formitem.HierarchicalComponent;
import com.enonic.wem.api.content.type.formitem.Input;
import com.enonic.wem.api.content.type.formitem.Layout;
import com.enonic.wem.api.content.type.formitem.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.formitem.SubTypeReference;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.formitem.comptype.InputTypeConfigSerializerXml;
import com.enonic.wem.core.content.type.formitem.comptype.InputTypeSerializerXml;

import static com.enonic.wem.api.content.type.formitem.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.formitem.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.formitem.Input.newInput;

public class ComponentSerializerXml
{
    private InputTypeSerializerXml inputTypeSerializer = new InputTypeSerializerXml();

    private InputTypeConfigSerializerXml inputTypeConfigSerializer = new InputTypeConfigSerializerXml();

    private final ComponentsSerializerXml componentsSerializer;

    public ComponentSerializerXml()
    {
        this.componentsSerializer = new ComponentsSerializerXml();
    }

    public ComponentSerializerXml( final ComponentsSerializerXml componentsSerializer )
    {
        this.componentsSerializer = componentsSerializer;
    }

    public Element generate( Component component )
    {
        if ( component instanceof ComponentSet )
        {
            return generateComponentSet( (ComponentSet) component );
        }
        else if ( component instanceof Layout )
        {
            return generateLayout( (Layout) component );
        }
        else if ( component instanceof Input )
        {
            return generateInput( (Input) component );
        }
        else if ( component instanceof SubTypeReference )
        {
            return generateReference( (SubTypeReference) component );
        }
        return null;
    }

    private Element generateInput( final Input input )
    {
        Element inputEl = new Element( input.getName() );
        inputEl.setAttribute( "component-type", Input.class.getSimpleName() );

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

    private Element generateComponentSet( final ComponentSet componentSet )
    {
        Element componentSetEl = new Element( componentSet.getName() );
        componentSetEl.setAttribute( "component-type", ComponentSet.class.getSimpleName() );

        componentSetEl.addContent( new Element( "label" ).setText( componentSet.getLabel() ) );
        componentSetEl.addContent( new Element( "required" ).setText( String.valueOf( componentSet.isRequired() ) ) );
        componentSetEl.addContent( new Element( "immutable" ).setText( String.valueOf( componentSet.isImmutable() ) ) );
        componentSetEl.addContent( new Element( "customText" ).setText( componentSet.getCustomText() ) );
        componentSetEl.addContent( new Element( "helpText" ).setText( componentSet.getCustomText() ) );

        componentSetEl.addContent( OccurrencesSerializerXml.generate( componentSet.getOccurrences() ) );
        componentSetEl.addContent( componentsSerializer.generate( componentSet.getComponents() ) );
        return componentSetEl;
    }

    private Element generateLayout( final Layout layout )
    {
        Element layoutEl = new Element( layout.getName() );
        layoutEl.setAttribute( "component-type", Layout.class.getSimpleName() );

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
        layoutEl.addContent( componentsSerializer.generate( fieldSet.getComponents() ) );
    }

    private Element generateReference( final SubTypeReference subTypeReference )
    {
        Element referenceEl = new Element( subTypeReference.getName() );
        referenceEl.setAttribute( "component-type", SubTypeReference.class.getSimpleName() );
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

    public Component parse( final Element componentEl )
    {
        final String formItemType = componentEl.getAttributeValue( "component-type" );

        final Component component;
        if ( formItemType.equals( Input.class.getSimpleName() ) )
        {
            component = parseInput( componentEl );
        }
        else if ( formItemType.equals( ComponentSet.class.getSimpleName() ) )
        {
            component = parseFormItemSet( componentEl );
        }
        else if ( formItemType.equals( Layout.class.getSimpleName() ) )
        {
            component = parseLayout( componentEl );
        }
        else if ( formItemType.equals( SubTypeReference.class.getSimpleName() ) )
        {
            component = parseSubTypeReference( componentEl );
        }
        else
        {
            throw new JsonParsingException( "Unknown ComponentType: " + formItemType );
        }

        return component;
    }

    private Component parseInput( final Element formItemEl )
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

    private HierarchicalComponent parseFormItemSet( final Element formItemEl )
    {
        final ComponentSet.Builder builder = newComponentSet();
        builder.name( formItemEl.getName() );
        builder.label( formItemEl.getChildText( "label" ) );
        builder.required( Boolean.valueOf( formItemEl.getChildText( "required" ) ) );
        builder.immutable( Boolean.valueOf( formItemEl.getChildText( "immutable" ) ) );
        builder.helpText( formItemEl.getChildText( "helpText" ) );
        builder.customText( formItemEl.getChildText( "customText" ) );

        builder.occurrences( OccurrencesSerializerXml.parse( formItemEl ) );

        final Components components = componentsSerializer.parse( formItemEl );
        for ( Component component : components.iterable() )
        {
            builder.add( component );
        }

        return builder.build();
    }

    private Component parseLayout( final Element formItemEl )
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

    private Component parseFieldSet( final Element formItemEl )
    {
        final FieldSet.Builder builder = newFieldSet();
        builder.name( formItemEl.getName() );
        builder.label( formItemEl.getChildText( "label" ) );

        final Components components = componentsSerializer.parse( formItemEl );
        for ( Component component : components.iterable() )
        {
            builder.add( component );
        }

        return builder.build();
    }

    private HierarchicalComponent parseSubTypeReference( final Element formItemEl )
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
