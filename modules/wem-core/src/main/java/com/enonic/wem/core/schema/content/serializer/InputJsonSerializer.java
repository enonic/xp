package com.enonic.wem.core.schema.content.serializer;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputType;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.api.schema.content.form.Input.newInput;

public class InputJsonSerializer
    extends AbstractJsonSerializer<Input>
{
    private static final String TYPE = "type";

    private static final String NAME = "name";

    public static final String LABEL = "label";

    public static final String IMMUTABLE = "immutable";

    public static final String CUSTOM_TEXT = "customText";

    public static final String HELP_TEXT = "helpText";

    public static final String OCCURRENCES = "occurrences";

    public static final String INDEXED = "indexed";

    public static final String VALIDATION_REGEXP = "validationRegexp";

    public static final String CONFIG = "config";

    private final InputTypeJsonSerializer inputTypeSerializer;

    private final OccurrencesJsonSerializer occurrencesJsonSerializer;

    private final InputTypeConfigJsonSerializer inputTypeConfigSerializer = new InputTypeConfigJsonSerializer();

    public InputJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        inputTypeSerializer = new InputTypeJsonSerializer( objectMapper );
        occurrencesJsonSerializer = new OccurrencesJsonSerializer( objectMapper );
    }

    @Override
    protected JsonNode serialize( final Input input )
    {
        final ObjectNode jsonObject = objectMapper().createObjectNode();
        jsonObject.put( NAME, input.getName() );
        jsonObject.put( LABEL, input.getLabel() );
        jsonObject.put( IMMUTABLE, input.isImmutable() );
        jsonObject.put( OCCURRENCES, occurrencesJsonSerializer.serialize( input.getOccurrences() ) );
        jsonObject.put( INDEXED, input.isIndexed() );
        jsonObject.put( CUSTOM_TEXT, input.getCustomText() );
        jsonObject.put( VALIDATION_REGEXP, input.getValidationRegexp() != null ? input.getValidationRegexp().toString() : null );
        jsonObject.put( HELP_TEXT, input.getHelpText() );

        final ObjectNode typeObject = (ObjectNode) inputTypeSerializer.serialize( (InputType) input.getInputType() );
        if ( input.getInputType().requiresConfig() && input.getInputTypeConfig() != null )
        {
            final JsonNode inputTypeNode =
                input.getInputType().getInputTypeConfigJsonSerializer().serialize( input.getInputTypeConfig(), objectMapper() );
            typeObject.put( CONFIG, inputTypeNode );
        }
        jsonObject.put( TYPE, typeObject );
        return jsonObject;
    }

    public Input parse( final JsonNode inputObj )
    {
        final Input.Builder builder = newInput();
        builder.name( JsonSerializerUtil.getStringValue( NAME, inputObj ) );
        builder.label( JsonSerializerUtil.getStringValue( LABEL, inputObj, null ) );
        builder.immutable( JsonSerializerUtil.getBooleanValue( IMMUTABLE, inputObj ) );
        builder.helpText( JsonSerializerUtil.getStringValue( HELP_TEXT, inputObj ) );
        builder.customText( JsonSerializerUtil.getStringValue( CUSTOM_TEXT, inputObj ) );
        builder.indexed( JsonSerializerUtil.getBooleanValue( INDEXED, inputObj ) );
        parseValidationRegexp( builder, inputObj );

        parseOccurrences( builder, inputObj.get( OCCURRENCES ) );
        parseInputType( builder, inputObj.get( TYPE ) );

        return builder.build();
    }

    private void parseValidationRegexp( final Input.Builder builder, final JsonNode inputNode )
    {
        final String validationRegexp = JsonSerializerUtil.getStringValue( VALIDATION_REGEXP, inputNode, null );
        if ( validationRegexp != null )
        {
            builder.validationRegexp( validationRegexp );
        }
    }

    private void parseOccurrences( final Input.Builder builder, final JsonNode occurrencesNode )
    {
        if ( occurrencesNode != null )
        {
            builder.occurrences( occurrencesJsonSerializer.parse( occurrencesNode ) );
        }
        else
        {
            builder.multiple( false );
        }
    }

    private void parseInputType( final Input.Builder builder, final JsonNode inputTypeNode )
    {
        if ( inputTypeNode != null )
        {
            final InputType inputType = inputTypeSerializer.parse( inputTypeNode );
            builder.inputType( inputType );
            if ( inputTypeNode.has( CONFIG ) )
            {
                builder.inputTypeConfig( inputTypeConfigSerializer.parse( inputTypeNode.get( CONFIG ), inputType.getClass() ) );
            }
        }
    }
}
