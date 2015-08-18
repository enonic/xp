package com.enonic.xp.form.inputtype;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;


@Beta
public class FileUploaderConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<FileUploaderConfig>
{
    public static final FileUploaderConfigJsonSerializer DEFAULT = new FileUploaderConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final FileUploaderConfig config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();

        final ImmutableSet<String> allowTypeNames = config.getAllowTypeNames();
        if ( !allowTypeNames.isEmpty() )
        {
            final ArrayNode allowTypesJson = jsonConfig.putArray( "allowTypes" );
            for ( String allowTypeName : allowTypeNames )
            {
                final ObjectNode typeJson = allowTypesJson.addObject();
                typeJson.put( "name", allowTypeName );
                typeJson.put( "extensions", config.getAllowTypeExtensions( allowTypeName ) );
            }
        }
        else
        {
            jsonConfig.putArray( "allowTypes" );
        }

        if ( config.hideDropZone() )
        {
            jsonConfig.put( "hideDropZone", config.hideDropZone() );
        }

        return jsonConfig;
    }

    @Override
    public FileUploaderConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final FileUploaderConfig.Builder builder = FileUploaderConfig.create();
        final JsonNode allowTypesNode = inputTypeConfigNode.get( "allowTypes" );
        if ( allowTypesNode != null && !allowTypesNode.isNull() & allowTypesNode.isArray() )
        {
            final ArrayNode allowTypesArrayNode = (ArrayNode) allowTypesNode;
            allowTypesArrayNode.forEach( ( typeNode ) -> {
                builder.allowType( typeNode.get( "name" ).textValue(), typeNode.get( "extensions" ).textValue() );
            } );
        }

        final JsonNode hideDropZoneNode = inputTypeConfigNode.get( "hideDropZone" );
        builder.hideDropZone( hideDropZoneNode != null && hideDropZoneNode.booleanValue() );

        return builder.build();
    }
}
