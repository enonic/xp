package com.enonic.wem.core.content.type.component;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.component.Component;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.api.content.type.component.FieldSet;
import com.enonic.wem.api.content.type.component.HierarchicalComponent;
import com.enonic.wem.api.content.type.component.Input;
import com.enonic.wem.api.content.type.component.Layout;
import com.enonic.wem.api.content.type.component.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.component.SubTypeReference;
import com.enonic.wem.api.content.type.component.inputtype.BaseInputType;
import com.enonic.wem.core.content.AbstractSerializerJson;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.component.inputtype.InputTypeConfigSerializerJson;
import com.enonic.wem.core.content.type.component.inputtype.InputTypeSerializerJson;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.component.Input.newInput;

class ComponentSerializerJson
    extends AbstractSerializerJson<Component>
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

    private final InputTypeSerializerJson inputTypeSerializer = new InputTypeSerializerJson();

    private final InputTypeConfigSerializerJson inputTypeConfigSerializer = new InputTypeConfigSerializerJson();

    private final OccurrencesSerializerJson occurrencesSerializerJson = new OccurrencesSerializerJson();

    private final ComponentsSerializerJson componentsSerializerJson;

    public ComponentSerializerJson( final ComponentsSerializerJson componentsSerializerJson )
    {
        this.componentsSerializerJson = componentsSerializerJson;
    }

    @Override
    protected JsonNode serialize( final Component component, final ObjectMapper objectMapper )
    {
        if ( component instanceof ComponentSet )
        {
            return serializeComponentSet( (ComponentSet) component, objectMapper );
        }
        else if ( component instanceof Layout )
        {
            return serializeLayout( (Layout) component, objectMapper );
        }
        else if ( component instanceof Input )
        {
            return serializeInput( (Input) component, objectMapper );
        }
        else if ( component instanceof SubTypeReference )
        {
            return serializeReference( (SubTypeReference) component, objectMapper );
        }
        return NullNode.getInstance();
    }

    private JsonNode serializeInput( final Input input, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( TYPE, Input.class.getSimpleName() );
        jsonObject.put( NAME, input.getName() );
        jsonObject.put( LABEL, input.getLabel() );
        jsonObject.put( REQUIRED, input.isRequired() );
        jsonObject.put( IMMUTABLE, input.isImmutable() );
        jsonObject.put( "occurrences", occurrencesSerializerJson.serialize( input.getOccurrences(), objectMapper ) );
        jsonObject.put( INDEXED, input.isIndexed() );
        jsonObject.put( CUSTOM_TEXT, input.getCustomText() );
        jsonObject.put( VALIDATION_REGEXP, input.getValidationRegexp() != null ? input.getValidationRegexp().toString() : null );
        jsonObject.put( HELP_TEXT, input.getHelpText() );
        jsonObject.put( "inputType", inputTypeSerializer.serialize( (BaseInputType) input.getInputType(), objectMapper ) );
        if ( input.getInputType().requiresConfig() && input.getInputTypeConfig() != null )
        {
            final JsonNode inputTypeNode =
                input.getInputType().getInputTypeConfigJsonGenerator().serialize( input.getInputTypeConfig(), objectMapper );
            jsonObject.put( "inputTypeConfig", inputTypeNode );
        }
        return jsonObject;
    }

    private JsonNode serializeComponentSet( final ComponentSet componentSet, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( TYPE, ComponentSet.class.getSimpleName() );
        jsonObject.put( NAME, componentSet.getName() );
        jsonObject.put( LABEL, componentSet.getLabel() );
        jsonObject.put( REQUIRED, componentSet.isRequired() );
        jsonObject.put( IMMUTABLE, componentSet.isImmutable() );
        jsonObject.put( "occurrences", occurrencesSerializerJson.serialize( componentSet.getOccurrences(), objectMapper ) );
        jsonObject.put( CUSTOM_TEXT, componentSet.getCustomText() );
        jsonObject.put( HELP_TEXT, componentSet.getHelpText() );
        jsonObject.put( "items", componentsSerializerJson.serialize( componentSet.getComponents(), objectMapper ) );
        return jsonObject;
    }

    private JsonNode serializeLayout( final Layout layout, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( TYPE, Layout.class.getSimpleName() );
        jsonObject.put( "layoutType", FieldSet.class.getSimpleName() );

        if ( layout instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) layout, jsonObject, objectMapper );
        }
        return jsonObject;
    }

    private void generateFieldSet( final FieldSet fieldSet, final ObjectNode jsonObject, final ObjectMapper objectMapper )
    {
        jsonObject.put( LABEL, fieldSet.getLabel() );
        jsonObject.put( NAME, fieldSet.getName() );
        jsonObject.put( "items", componentsSerializerJson.serialize( fieldSet.getComponents(), objectMapper ) );
    }

    private JsonNode serializeReference( final SubTypeReference subTypeReference, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( TYPE, SubTypeReference.class.getSimpleName() );
        jsonObject.put( NAME, subTypeReference.getName() );
        jsonObject.put( "reference", subTypeReference.getSubTypeQualifiedName().toString() );
        jsonObject.put( "subTypeClass", subTypeReference.getSubTypeClass().getSimpleName() );
        return jsonObject;
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
            builder.occurrences( occurrencesSerializerJson.parse( occurrencesNode ) );
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
            builder.occurrences( occurrencesSerializerJson.parse( occurrencesNode ) );
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
