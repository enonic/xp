package com.enonic.wem.core.content.type.component;


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
import com.enonic.wem.core.content.type.component.inputtype.InputTypeConfigSerializerXml;
import com.enonic.wem.core.content.type.component.inputtype.InputTypeSerializerXml;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.component.Input.newInput;

class ComponentSerializerXml
{
    public static final String TYPE = "type";

    public static final String LABEL = "label";

    public static final String IMMUTABLE = "immutable";

    public static final String INDEXED = "indexed";

    public static final String CUSTOM_TEXT = "customText";

    public static final String HELP_TEXT = "helpText";

    public static final String REFERENCE = "reference";

    public static final String VALIDATION_REGEX = "validationRegex";

    public static final String SUB_TYPE_CLASS = "subTypeClass";

    public static final String LAYOUT_TYPE = "layout-type";

    private final InputTypeSerializerXml inputTypeSerializer = new InputTypeSerializerXml();

    private final InputTypeConfigSerializerXml inputTypeConfigSerializer = new InputTypeConfigSerializerXml();

    private final OccurrencesSerializerXml occurrencesSerializerXml = new OccurrencesSerializerXml();

    private final ComponentsSerializerXml componentsSerializer;

    public ComponentSerializerXml( final ComponentsSerializerXml componentsSerializer )
    {
        this.componentsSerializer = componentsSerializer;
    }

    public Element serialize( Component component )
    {
        if ( component instanceof ComponentSet )
        {
            return serializeComponentSet( (ComponentSet) component );
        }
        else if ( component instanceof Layout )
        {
            return serializeLayout( (Layout) component );
        }
        else if ( component instanceof Input )
        {
            return serializeInput( (Input) component );
        }
        else if ( component instanceof SubTypeReference )
        {
            return serializeReference( (SubTypeReference) component );
        }
        return null;
    }

    private Element serializeInput( final Input input )
    {
        Element inputEl = new Element( input.getName() );
        inputEl.setAttribute( TYPE, Input.class.getSimpleName() );

        inputEl.addContent( new Element( LABEL ).setText( input.getLabel() ) );
        inputEl.addContent( new Element( IMMUTABLE ).setText( String.valueOf( input.isImmutable() ) ) );
        inputEl.addContent( new Element( INDEXED ).setText( String.valueOf( input.isIndexed() ) ) );
        inputEl.addContent( new Element( CUSTOM_TEXT ).setText( input.getCustomText() ) );
        inputEl.addContent( new Element( HELP_TEXT ).setText( input.getHelpText() ) );
        inputEl.addContent( occurrencesSerializerXml.serialize( input.getOccurrences() ) );
        generateValidationRegex( input, inputEl );
        inputEl.addContent( inputTypeSerializer.serialize( input.getInputType() ) );
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

    private Element serializeComponentSet( final ComponentSet componentSet )
    {
        Element componentSetEl = new Element( componentSet.getName() );
        componentSetEl.setAttribute( TYPE, ComponentSet.class.getSimpleName() );

        componentSetEl.addContent( new Element( LABEL ).setText( componentSet.getLabel() ) );
        componentSetEl.addContent( new Element( IMMUTABLE ).setText( String.valueOf( componentSet.isImmutable() ) ) );
        componentSetEl.addContent( new Element( CUSTOM_TEXT ).setText( componentSet.getCustomText() ) );
        componentSetEl.addContent( new Element( HELP_TEXT ).setText( componentSet.getCustomText() ) );

        componentSetEl.addContent( occurrencesSerializerXml.serialize( componentSet.getOccurrences() ) );
        componentSetEl.addContent( componentsSerializer.serialize( componentSet.getComponents() ) );
        return componentSetEl;
    }

    private Element serializeLayout( final Layout layout )
    {
        Element layoutEl = new Element( layout.getName() );
        layoutEl.setAttribute( TYPE, Layout.class.getSimpleName() );

        if ( layout instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) layout, layoutEl );
        }

        return layoutEl;
    }

    private void generateFieldSet( final FieldSet fieldSet, final Element layoutEl )
    {
        layoutEl.setAttribute( LAYOUT_TYPE, FieldSet.class.getSimpleName() );
        layoutEl.addContent( new Element( LABEL ).setText( fieldSet.getLabel() ) );
        layoutEl.addContent( componentsSerializer.serialize( fieldSet.getComponents() ) );
    }

    private Element serializeReference( final SubTypeReference subTypeReference )
    {
        Element referenceEl = new Element( subTypeReference.getName() );
        referenceEl.setAttribute( TYPE, SubTypeReference.class.getSimpleName() );
        referenceEl.addContent( new Element( REFERENCE ).setText( subTypeReference.getSubTypeQualifiedName().toString() ) );
        referenceEl.addContent( new Element( SUB_TYPE_CLASS ).setText( subTypeReference.getSubTypeClass().getSimpleName() ) );
        return referenceEl;
    }

    private void generateValidationRegex( final Input input, final Element inputEl )
    {
        if ( input.getValidationRegexp() != null )
        {
            inputEl.addContent( new Element( VALIDATION_REGEX ).setText( input.getValidationRegexp().toString() ) );
        }
    }

    public Component parse( final Element componentEl )
    {
        final String componentType = componentEl.getAttributeValue( TYPE );

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
        builder.label( componentEl.getChildText( LABEL ) );
        builder.immutable( Boolean.valueOf( componentEl.getChildText( IMMUTABLE ) ) );
        builder.helpText( componentEl.getChildText( HELP_TEXT ) );
        builder.customText( componentEl.getChildText( CUSTOM_TEXT ) );
        parseValidationRegexp( builder, componentEl );

        builder.occurrences( occurrencesSerializerXml.parse( componentEl ) );
        parseInputType( builder, componentEl );
        parseInputTypeConfig( builder, componentEl );

        return builder.build();
    }

    private HierarchicalComponent parseComponentSet( final Element componentEl )
    {
        final ComponentSet.Builder builder = newComponentSet();
        builder.name( componentEl.getName() );
        builder.label( componentEl.getChildText( LABEL ) );
        builder.immutable( Boolean.valueOf( componentEl.getChildText( IMMUTABLE ) ) );
        builder.helpText( componentEl.getChildText( HELP_TEXT ) );
        builder.customText( componentEl.getChildText( CUSTOM_TEXT ) );

        builder.occurrences( occurrencesSerializerXml.parse( componentEl ) );

        final Components components = componentsSerializer.parse( componentEl );
        for ( Component component : components.iterable() )
        {
            builder.add( component );
        }

        return builder.build();
    }

    private Component parseLayout( final Element componentEl )
    {
        String layoutType = componentEl.getAttributeValue( LAYOUT_TYPE );
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
        builder.label( componentEl.getChildText( LABEL ) );

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
        builder.subType( new SubTypeQualifiedName( componentEl.getChildText( REFERENCE ) ) );
        builder.type( componentEl.getChildText( SUB_TYPE_CLASS ) );
        return builder.build();
    }

    private void parseValidationRegexp( final Input.Builder builder, final Element componentEl )
    {
        String validationRegexp = componentEl.getChildText( VALIDATION_REGEX );
        if ( validationRegexp != null )
        {
            builder.validationRegexp( validationRegexp );
        }
    }

    private void parseInputTypeConfig( final Input.Builder builder, final Element componentEl )
    {
        builder.inputTypeConfig( inputTypeConfigSerializer.parse( componentEl ) );
    }

    private void parseInputType( final Input.Builder builder, final Element componentEl )
    {
        builder.type( inputTypeSerializer.parse( componentEl ) );
    }
}
