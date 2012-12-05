package com.enonic.wem.core.content.type.form;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.inputtype.BaseInputType;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.type.form.inputtype.InputTypeConfigJsonSerializer;

import static com.enonic.wem.api.content.type.form.Input.newInput;

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

    private final com.enonic.wem.core.content.type.form.inputtype.InputTypeJsonSerializer inputTypeSerializer =
        new com.enonic.wem.core.content.type.form.inputtype.InputTypeJsonSerializer();

    private final InputTypeConfigJsonSerializer inputTypeConfigSerializer = new InputTypeConfigJsonSerializer();

    private final OccurrencesJsonSerializer occurrencesJsonSerializer = new OccurrencesJsonSerializer();

    @Override
    protected JsonNode serialize( final Input input, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( NAME, input.getName() );
        jsonObject.put( LABEL, input.getLabel() );
        jsonObject.put( IMMUTABLE, input.isImmutable() );
        jsonObject.put( OCCURRENCES, occurrencesJsonSerializer.serialize( input.getOccurrences(), objectMapper ) );
        jsonObject.put( INDEXED, input.isIndexed() );
        jsonObject.put( CUSTOM_TEXT, input.getCustomText() );
        jsonObject.put( VALIDATION_REGEXP, input.getValidationRegexp() != null ? input.getValidationRegexp().toString() : null );
        jsonObject.put( HELP_TEXT, input.getHelpText() );
        jsonObject.put( TYPE, inputTypeSerializer.serialize( (BaseInputType) input.getInputType(), objectMapper ) );
        if ( input.getInputType().requiresConfig() && input.getInputTypeConfig() != null )
        {
            final JsonNode inputTypeNode =
                input.getInputType().getInputTypeConfigJsonGenerator().serialize( input.getInputTypeConfig(), objectMapper );
            jsonObject.put( CONFIG, inputTypeNode );
        }
        return jsonObject;
    }

    public Input parse( final JsonNode inputObj )
    {
        final Input.Builder builder = newInput();
        builder.name( JsonParserUtil.getStringValue( NAME, inputObj ) );
        builder.label( JsonParserUtil.getStringValue( LABEL, inputObj, null ) );
        builder.immutable( JsonParserUtil.getBooleanValue( IMMUTABLE, inputObj ) );
        builder.helpText( JsonParserUtil.getStringValue( HELP_TEXT, inputObj ) );
        builder.customText( JsonParserUtil.getStringValue( CUSTOM_TEXT, inputObj ) );
        parseValidationRegexp( builder, inputObj );

        parseOccurrences( builder, inputObj.get( OCCURRENCES ) );
        parseInputType( builder, inputObj.get( TYPE ) );
        parseInputTypeConfig( builder, inputObj.get( CONFIG ) );

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
