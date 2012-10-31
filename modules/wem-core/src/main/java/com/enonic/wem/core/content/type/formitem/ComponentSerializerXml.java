package com.enonic.wem.core.content.type.formitem;


import org.jdom.Element;

import com.enonic.wem.api.content.type.component.Component;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.api.content.type.component.FieldSet;
import com.enonic.wem.api.content.type.component.HierarchicalComponent;
import com.enonic.wem.api.content.type.component.Input;
import com.enonic.wem.api.content.type.component.Layout;
import com.enonic.wem.api.content.type.component.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.component.SubTypeReference;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.formitem.comptype.InputTypeConfigSerializerXml;
import com.enonic.wem.core.content.type.formitem.comptype.InputTypeSerializerXml;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.component.Input.newInput;

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
        final String componentType = componentEl.getAttributeValue( "component-type" );

        final Component component;
        if ( componentType.equals( Input.class.getSimpleName() ) )
        {
            component = parseInput( componentEl );
        }
        else if ( componentType.equals( ComponentSet.class.getSimpleName() ) )
        {
            component = parseComponentSet( componentEl );
        }
        else if ( componentType.equals( Layout.class.getSimpleName() ) )
        {
            component = parseLayout( componentEl );
        }
        else if ( componentType.equals( SubTypeReference.class.getSimpleName() ) )
        {
            component = parseSubTypeReference( componentEl );
        }
        else
        {
            throw new JsonParsingException( "Unknown ComponentType: " + componentType );
        }

        return component;
    }

    private Component parseInput( final Element componentEl )
    {
        final Input.Builder builder = newInput();
        builder.name( componentEl.getName() );
        builder.label( componentEl.getChildText( "label" ) );
        builder.immutable( Boolean.valueOf( componentEl.getChildText( "immutable" ) ) );
        builder.helpText( componentEl.getChildText( "helpText" ) );
        builder.customText( componentEl.getChildText( "customText" ) );
        parseValidationRegexp( builder, componentEl );

        builder.occurrences( OccurrencesSerializerXml.parse( componentEl ) );
        parseInputType( builder, componentEl );
        parseInputTypeConfig( builder, componentEl );

        return builder.build();
    }

    private HierarchicalComponent parseComponentSet( final Element componentEl )
    {
        final ComponentSet.Builder builder = newComponentSet();
        builder.name( componentEl.getName() );
        builder.label( componentEl.getChildText( "label" ) );
        builder.required( Boolean.valueOf( componentEl.getChildText( "required" ) ) );
        builder.immutable( Boolean.valueOf( componentEl.getChildText( "immutable" ) ) );
        builder.helpText( componentEl.getChildText( "helpText" ) );
        builder.customText( componentEl.getChildText( "customText" ) );

        builder.occurrences( OccurrencesSerializerXml.parse( componentEl ) );

        final Components components = componentsSerializer.parse( componentEl );
        for ( Component component : components.iterable() )
        {
            builder.add( component );
        }

        return builder.build();
    }

    private Component parseLayout( final Element componentEl )
    {
        String layoutType = componentEl.getAttributeValue( "layout-type" );
        if ( layoutType.equals( FieldSet.class.getSimpleName() ) )
        {
            return parseFieldSet( componentEl );
        }
        else
        {
            throw new JsonParsingException( "Unknown layoutType: " + layoutType );
        }
    }

    private Component parseFieldSet( final Element componentEl )
    {
        final FieldSet.Builder builder = newFieldSet();
        builder.name( componentEl.getName() );
        builder.label( componentEl.getChildText( "label" ) );

        final Components components = componentsSerializer.parse( componentEl );
        for ( Component component : components.iterable() )
        {
            builder.add( component );
        }

        return builder.build();
    }

    private HierarchicalComponent parseSubTypeReference( final Element componentEl )
    {
        final SubTypeReference.Builder builder = SubTypeReference.newSubTypeReference();
        builder.name( componentEl.getName() );
        builder.subType( new SubTypeQualifiedName( componentEl.getChildText( "reference" ) ) );
        builder.type( componentEl.getChildText( "subTypeClass" ) );
        return builder.build();
    }

    private void parseValidationRegexp( final Input.Builder builder, final Element componentEl )
    {
        String validationRegexp = componentEl.getChildText( "validationRegex" );
        if ( validationRegexp != null )
        {
            builder.validationRegexp( validationRegexp );
        }
    }

    private void parseInputTypeConfig( final Input.Builder builder, final Element componentEl )
    {
        builder.inputTypeConfig( inputTypeConfigSerializer.parse( componentEl ) );
    }

    private void parseInputType( final Input.Builder builder, final Element formItemEl )
    {
        builder.type( inputTypeSerializer.parse( formItemEl ) );
    }
}
