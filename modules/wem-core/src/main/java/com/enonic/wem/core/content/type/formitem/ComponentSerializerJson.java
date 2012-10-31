package com.enonic.wem.core.content.type.formitem;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.type.formitem.Component;
import com.enonic.wem.api.content.type.formitem.ComponentSet;
import com.enonic.wem.api.content.type.formitem.Components;
import com.enonic.wem.api.content.type.formitem.FieldSet;
import com.enonic.wem.api.content.type.formitem.HierarchicalComponent;
import com.enonic.wem.api.content.type.formitem.Input;
import com.enonic.wem.api.content.type.formitem.Layout;
import com.enonic.wem.api.content.type.formitem.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.formitem.SubTypeReference;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.formitem.comptype.InputTypeConfigSerializerJson;
import com.enonic.wem.core.content.type.formitem.comptype.InputTypeSerializerJson;

import static com.enonic.wem.api.content.type.formitem.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.formitem.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.formitem.Input.newInput;

public class ComponentSerializerJson
{
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
        g.writeStringField( "componentType", Input.class.getSimpleName() );
        g.writeStringField( "name", input.getName() );
        inputTypeSerializer.generate( input.getInputType(), g );
        g.writeStringField( "label", input.getLabel() );
        g.writeBooleanField( "required", input.isRequired() );
        g.writeBooleanField( "immutable", input.isImmutable() );
        OccurrencesSerializerJson.generate( input.getOccurrences(), g );
        g.writeBooleanField( "indexed", input.isIndexed() );
        g.writeStringField( "customText", input.getCustomText() );
        g.writeStringField( "validationRegexp", input.getValidationRegexp() != null ? input.getValidationRegexp().toString() : null );
        g.writeStringField( "helpText", input.getHelpText() );
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
        g.writeStringField( "componentType", ComponentSet.class.getSimpleName() );
        g.writeStringField( "name", componentSet.getName() );
        g.writeStringField( "label", componentSet.getLabel() );
        g.writeBooleanField( "required", componentSet.isRequired() );
        g.writeBooleanField( "immutable", componentSet.isImmutable() );
        OccurrencesSerializerJson.generate( componentSet.getOccurrences(), g );
        g.writeStringField( "customText", componentSet.getCustomText() );
        g.writeStringField( "helpText", componentSet.getHelpText() );
        componentsSerializerJson.generate( componentSet.getComponents(), g );

        g.writeEndObject();
    }

    private void generateLayout( final Layout layout, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "componentType", Layout.class.getSimpleName() );

        if ( layout instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) layout, g );
        }

        g.writeEndObject();
    }

    private void generateFieldSet( final FieldSet fieldSet, JsonGenerator g )
        throws IOException
    {
        g.writeStringField( "layoutType", FieldSet.class.getSimpleName() );
        g.writeStringField( "label", fieldSet.getLabel() );
        g.writeStringField( "name", fieldSet.getName() );
        componentsSerializerJson.generate( fieldSet.getComponents(), g );
    }

    private void generateReference( final SubTypeReference subTypeReference, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "componentType", SubTypeReference.class.getSimpleName() );
        g.writeStringField( "name", subTypeReference.getName() );
        g.writeStringField( "reference", subTypeReference.getSubTypeQualifiedName().toString() );
        g.writeStringField( "subTypeClass", subTypeReference.getSubTypeClass().getSimpleName() );
        g.writeEndObject();
    }

    public Component parse( final JsonNode componentNode )
    {
        final String componentType = JsonParserUtil.getStringValue( "componentType", componentNode );

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
        builder.name( JsonParserUtil.getStringValue( "name", componentNode ) );
        builder.label( JsonParserUtil.getStringValue( "label", componentNode, null ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", componentNode ) );
        builder.helpText( JsonParserUtil.getStringValue( "helpText", componentNode ) );
        builder.customText( JsonParserUtil.getStringValue( "customText", componentNode ) );
        parseValidationRegexp( builder, componentNode );

        parseOccurrences( builder, componentNode.get( "occurrences" ) );
        parseInputType( builder, componentNode.get( "inputType" ) );
        parseInputTypeConfig( builder, componentNode.get( "inputTypeConfig" ) );

        return builder.build();
    }

    private HierarchicalComponent parseComponentSet( final JsonNode componentNode )
    {
        final ComponentSet.Builder builder = newComponentSet();
        builder.name( JsonParserUtil.getStringValue( "name", componentNode ) );
        builder.label( JsonParserUtil.getStringValue( "label", componentNode, null ) );
        builder.required( JsonParserUtil.getBooleanValue( "required", componentNode ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", componentNode ) );
        builder.helpText( JsonParserUtil.getStringValue( "helpText", componentNode ) );
        builder.customText( JsonParserUtil.getStringValue( "customText", componentNode ) );

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
        builder.label( JsonParserUtil.getStringValue( "label", componentNode, null ) );
        builder.name( JsonParserUtil.getStringValue( "name", componentNode, null ) );

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
        builder.name( JsonParserUtil.getStringValue( "name", componentNode ) );
        builder.subType( new SubTypeQualifiedName( JsonParserUtil.getStringValue( "reference", componentNode ) ) );
        builder.type( JsonParserUtil.getStringValue( "subTypeClass", componentNode ) );
        return builder.build();
    }

    private void parseValidationRegexp( final Input.Builder builder, final JsonNode inputNode )
    {
        final String validationRegexp = JsonParserUtil.getStringValue( "validationRegexp", inputNode, null );
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
