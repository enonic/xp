package com.enonic.wem.core.content.config.field;


import java.io.IOException;

import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.config.field.type.FieldType;
import com.enonic.wem.core.content.config.field.type.FieldTypeConfigProxyJsonParser;
import com.enonic.wem.core.content.config.field.type.FieldTypeJsonParser;

public class ConfigItemJsonParser
{
    public static ConfigItem parse( final JsonNode configItemNode )
        throws IOException
    {
        final ConfigItem.Builder builder = ConfigItem.newConfigItemBuilder();

        builder.configType( ConfigType.valueOf( JsonParserUtil.getStringValue( "type", configItemNode ) ) );
        builder.name( JsonParserUtil.getStringValue( "name", configItemNode ) );
        builder.label( JsonParserUtil.getStringValue( "label", configItemNode ) );
        builder.required( JsonParserUtil.getBooleanValue( "required", configItemNode ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", configItemNode ) );
        builder.helpText( JsonParserUtil.getStringValue( "helpText", configItemNode ) );
        builder.customText( JsonParserUtil.getStringValue( "customText", configItemNode ) );

        parseFieldType( builder, configItemNode.get( "fieldType" ) );
        parseFieldTypeConfig( builder, configItemNode.get( "fieldTypeConfig" ) );
        parseMultiple( builder, configItemNode.get( "multiple" ) );

        final ConfigItem configItem = builder.build();
        configItem.setPath( new FieldPath( JsonParserUtil.getStringValue( "path", configItemNode ) ) );
        return configItem;
    }

    private static void parseMultiple( final ConfigItem.Builder builder, final JsonNode multipleNode )
        throws IOException
    {
        if ( multipleNode != null )
        {
            builder.multiple( MultipleJsonParser.parse( multipleNode ) );
        }
        else
        {
            builder.multiple( false );
        }
    }

    private static void parseFieldTypeConfig( final ConfigItem.Builder builder, final JsonNode fieldTypeConfigNode )
        throws IOException
    {
        if ( fieldTypeConfigNode != null )
        {
            builder.fieldTypeConfig( FieldTypeConfigProxyJsonParser.parse( fieldTypeConfigNode ) );
        }
    }

    private static void parseFieldType( final ConfigItem.Builder builder, final JsonNode fieldTypeNode )
        throws IOException
    {
        if ( fieldTypeNode != null )
        {
            FieldType fieldType = FieldTypeJsonParser.parse( fieldTypeNode );
            builder.fieldType( fieldType );
        }
    }
}
