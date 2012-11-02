package com.enonic.wem.core.content.type.component;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.type.component.Component;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.api.content.type.component.FieldSet;
import com.enonic.wem.api.content.type.component.HierarchicalComponent;
import com.enonic.wem.api.content.type.component.Input;
import com.enonic.wem.api.content.type.component.Layout;
import com.enonic.wem.api.content.type.component.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.component.SubTypeReference;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.component.inputtype.InputTypeConfigSerializerJson;
import com.enonic.wem.core.content.type.component.inputtype.InputTypeSerializerJson;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.component.Input.newInput;

public class ComponentSerializerJson
{
    private static final String TYPE = "type";

    private static final String NAME = "name";

    public static final String LABEL = "label";

    public static final String IMMUTABLE = "immutable";

    public static final String CUSTOM_TEXT = "customText";

    public static final String HELP_TEXT = "helpText";

    public static final String REQUIRED = "required";

    public static final String INDEXED = "indexed";

    public static final String VALIDATION_REGEXP = "validationRegexp";

    private InputTypeSerializerJson inputTypeSerializer = new InputTypeSerializerJson();

    private InputTypeConfigSerializerJson inputTypeConfigSerializer = new InputTypeConfigSerializerJson();

    private final ComponentsSerializerJson componentsSerializerJson;


    public ComponentSerializerJson()
    {
        this.componentsSerializerJson = new ComponentsSerializerJson();
    }

    public ComponentSerializerJson( final ComponentsSerializerJson componentsSerializerJson )
    {
        this.componentsSerializerJson = componentsSerializerJson;
    }

    public void generate( Component component, JsonGenerator g )
        throws IOException
    {
        if ( component instanceof ComponentSet )
        {
            generateComponentSet( (ComponentSet) component, g );
        }
        else if ( component instanceof Layout )
        {
            generateLayout( (Layout) component, g );
        }
        else if ( component instanceof Input )
        {
            generateInput( (Input) component, g );
        }
        else if ( component instanceof SubTypeReference )
        {
            generateReference( (SubTypeReference) component, g );
        }
    }

    private void generateInput( final Input input, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( TYPE, Input.class.getSimpleName() );
        g.writeStringField( NAME, input.getName() );
        g.writeStringField( LABEL, input.getLabel() );
        g.writeBooleanField( REQUIRED, input.isRequired() );
        g.writeBooleanField( IMMUTABLE, input.isImmutable() );
        OccurrencesSerializerJson.generate( input.getOccurrences(), g );
        g.writeBooleanField( INDEXED, input.isIndexed() );
        g.writeStringField( CUSTOM_TEXT, input.getCustomText() );
        g.writeStringField( VALIDATION_REGEXP, input.getValidationRegexp() != null ? input.getValidationRegexp().toString() : null );
        g.writeStringField( HELP_TEXT, input.getHelpText() );
        inputTypeSerializer.generate( input.getInputType(), g );
        if ( input.getInputType().requiresConfig() && input.getInputTypeConfig() != null )
        {
            g.writeFieldName( "inputTypeConfig" );
            input.getInputType().getInputTypeConfigJsonGenerator().generate( input.getInputTypeConfig(), g );
        }

        g.writeEndObject();
    }

    private void generateComponentSet( final ComponentSet componentSet, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( TYPE, ComponentSet.class.getSimpleName() );
        g.writeStringField( NAME, componentSet.getName() );
        g.writeStringField( LABEL, componentSet.getLabel() );
        g.writeBooleanField( REQUIRED, componentSet.isRequired() );
        g.writeBooleanField( IMMUTABLE, componentSet.isImmutable() );
        OccurrencesSerializerJson.generate( componentSet.getOccurrences(), g );
        g.writeStringField( CUSTOM_TEXT, componentSet.getCustomText() );
        g.writeStringField( HELP_TEXT, componentSet.getHelpText() );
        componentsSerializerJson.generate( componentSet.getComponents(), g );

        g.writeEndObject();
    }

    private void generateLayout( final Layout layout, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( TYPE, Layout.class.getSimpleName() );
        g.writeStringField( "layoutType", FieldSet.class.getSimpleName() );

        if ( layout instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) layout, g );
        }

        g.writeEndObject();
    }

    private void generateFieldSet( final FieldSet fieldSet, JsonGenerator g )
        throws IOException
    {
        g.writeStringField( LABEL, fieldSet.getLabel() );
        g.writeStringField( NAME, fieldSet.getName() );
        componentsSerializerJson.generate( fieldSet.getComponents(), g );
    }

    private void generateReference( final SubTypeReference subTypeReference, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( TYPE, SubTypeReference.class.getSimpleName() );
        g.writeStringField( NAME, subTypeReference.getName() );
        g.writeStringField( "reference", subTypeReference.getSubTypeQualifiedName().toString() );
        g.writeStringField( "subTypeClass", subTypeReference.getSubTypeClass().getSimpleName() );
        g.writeEndObject();
    }

    public Component parse( final JsonNode componentNode )
    {
        final String componentType = JsonParserUtil.getStringValue( TYPE, componentNode );

        final Component component;

        if ( componentType.equals( Input.class.getSimpleName() ) )
        {
            component = parseInput( componentNode );
        }
        else if ( componentType.equals( ComponentSet.class.getSimpleName() ) )
        {
            component = parseComponentSet( componentNode );
        }
        else if ( componentType.equals( Layout.class.getSimpleName() ) )
        {
            component = parseLayout( componentNode );
        }
        else if ( componentType.equals( SubTypeReference.class.getSimpleName() ) )
        {
            component = parseSubTypeReference( componentNode );
        }
        else
        {
            throw new JsonParsingException( "Unknown ComponentType: " + componentType );
        }

        return component;
    }

    private HierarchicalComponent parseInput( final JsonNode componentNode )
    {
        final Input.Builder builder = newInput();
        builder.name( JsonParserUtil.getStringValue( NAME, componentNode ) );
        builder.label( JsonParserUtil.getStringValue( LABEL, componentNode, null ) );
        builder.immutable( JsonParserUtil.getBooleanValue( IMMUTABLE, componentNode ) );
        builder.helpText( JsonParserUtil.getStringValue( HELP_TEXT, componentNode ) );
        builder.customText( JsonParserUtil.getStringValue( CUSTOM_TEXT, componentNode ) );
        parseValidationRegexp( builder, componentNode );

        parseOccurrences( builder, componentNode.get( "occurrences" ) );
        parseInputType( builder, componentNode.get( "inputType" ) );
        parseInputTypeConfig( builder, componentNode.get( "inputTypeConfig" ) );

        return builder.build();
    }

    private HierarchicalComponent parseComponentSet( final JsonNode componentNode )
    {
        final ComponentSet.Builder builder = newComponentSet();
        builder.name( JsonParserUtil.getStringValue( NAME, componentNode ) );
        builder.label( JsonParserUtil.getStringValue( LABEL, componentNode, null ) );
        builder.required( JsonParserUtil.getBooleanValue( REQUIRED, componentNode ) );
        builder.immutable( JsonParserUtil.getBooleanValue( IMMUTABLE, componentNode ) );
        builder.helpText( JsonParserUtil.getStringValue( HELP_TEXT, componentNode ) );
        builder.customText( JsonParserUtil.getStringValue( CUSTOM_TEXT, componentNode ) );

        parseOccurrences( builder, componentNode.get( "occurrences" ) );

        final Components components = componentsSerializerJson.parse( componentNode.get( "items" ) );
        for ( Component component : components.iterable() )
        {
            builder.add( component );
        }

        return builder.build();
    }

    private Component parseLayout( final JsonNode componentNode )
    {
        final String layoutType = JsonParserUtil.getStringValue( "layoutType", componentNode );
        if ( layoutType.equals( FieldSet.class.getSimpleName() ) )
        {
            return parseFieldSet( componentNode );
        }
        else
        {
            throw new JsonParsingException( "Unknown layoutType: " + layoutType );
        }
    }

    private Component parseFieldSet( final JsonNode componentNode )
    {
        final FieldSet.Builder builder = newFieldSet();
        builder.label( JsonParserUtil.getStringValue( LABEL, componentNode, null ) );
        builder.name( JsonParserUtil.getStringValue( NAME, componentNode, null ) );

        final Components components = componentsSerializerJson.parse( componentNode.get( "items" ) );
        for ( Component component : components.iterable() )
        {
            builder.add( component );
        }

        return builder.build();
    }

    private HierarchicalComponent parseSubTypeReference( final JsonNode componentNode )
    {
        final SubTypeReference.Builder builder = SubTypeReference.newSubTypeReference();
        builder.name( JsonParserUtil.getStringValue( NAME, componentNode ) );
        builder.subType( new SubTypeQualifiedName( JsonParserUtil.getStringValue( "reference", componentNode ) ) );
        builder.type( JsonParserUtil.getStringValue( "subTypeClass", componentNode ) );
        return builder.build();
    }

    private void parseValidationRegexp( final Input.Builder builder, final JsonNode inputNode )
    {
        final String validationRegexp = JsonParserUtil.getStringValue( VALIDATION_REGEXP, inputNode, null );
        if ( validationRegexp != null )
        {
            builder.validationRegexp( validationRegexp );
        }
    }

    private void parseOccurrences( final Input.Builder builder, final JsonNode occurrencesNode )
    {
        if ( occurrencesNode != null )
        {
            builder.occurrences( OccurrencesSerializerJson.parse( occurrencesNode ) );
        }
        else
        {
            builder.multiple( false );
        }
    }

    private void parseOccurrences( final ComponentSet.Builder builder, final JsonNode occurrencesNode )
    {
        if ( occurrencesNode != null )
        {
            builder.occurrences( OccurrencesSerializerJson.parse( occurrencesNode ) );
        }
        else
        {
            builder.multiple( false );
        }
    }

    private void parseInputTypeConfig( final Input.Builder builder, final JsonNode inputTypeConfigNode )
    {
        if ( inputTypeConfigNode != null )
        {
            builder.inputTypeConfig( inputTypeConfigSerializer.parse( inputTypeConfigNode ) );
        }
    }

    private void parseInputType( final Input.Builder builder, final JsonNode inputTypeNode )
    {
        if ( inputTypeNode != null )
        {
            builder.type( inputTypeSerializer.parse( inputTypeNode ) );
        }
    }
}
