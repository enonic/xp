package com.enonic.wem.core.content.type.configitem;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.type.configitem.field.type.FieldType;
import com.enonic.wem.core.content.type.configitem.field.type.FieldTypeConfigProxySerializerJson;
import com.enonic.wem.core.content.type.configitem.field.type.FieldTypeJsonParser;

public abstract class ConfigItemSerializerJson
{
    public abstract void generate( ConfigItem configItem, JsonGenerator g )
        throws IOException;

    public static ConfigItem parse( final JsonNode configItemNode )
        throws IOException
    {
        final ConfigItem.Builder builder = ConfigItem.newConfigItemBuilder();

        builder.itemType( ConfigItemType.valueOf( JsonParserUtil.getStringValue( "type", configItemNode ) ) );
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
            builder.multiple( MultipleSerializerJson.parse( multipleNode ) );
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
            builder.fieldTypeConfig( FieldTypeConfigProxySerializerJson.parse( fieldTypeConfigNode ) );
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
