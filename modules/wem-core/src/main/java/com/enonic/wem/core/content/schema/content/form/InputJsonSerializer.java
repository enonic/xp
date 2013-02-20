package com.enonic.wem.core.content.schema.content.form;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.inputtype.BaseInputType;
import com.enonic.wem.core.content.schema.content.form.inputtype.InputTypeConfigJsonSerializer;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.api.content.schema.content.form.Input.newInput;

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

    private final com.enonic.wem.core.content.schema.content.form.inputtype.InputTypeJsonSerializer inputTypeSerializer;

    private final OccurrencesJsonSerializer occurrencesJsonSerializer;

    private final InputTypeConfigJsonSerializer inputTypeConfigSerializer = new InputTypeConfigJsonSerializer();

    public InputJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        inputTypeSerializer = new com.enonic.wem.core.content.schema.content.form.inputtype.InputTypeJsonSerializer( objectMapper );
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
        jsonObject.put( TYPE, inputTypeSerializer.serialize( (BaseInputType) input.getInputType() ) );
        if ( input.getInputType().requiresConfig() && input.getInputTypeConfig() != null )
        {
            final JsonNode inputTypeNode =
                input.getInputType().getInputTypeConfigJsonGenerator().serialize( input.getInputTypeConfig(), objectMapper() );
            jsonObject.put( CONFIG, inputTypeNode );
        }
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
        parseInputTypeConfig( builder, inputObj.get( CONFIG ) );

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
