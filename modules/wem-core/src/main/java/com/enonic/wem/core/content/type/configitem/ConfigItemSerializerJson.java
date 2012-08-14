package com.enonic.wem.core.content.type.configitem;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldType;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypeConfigProxySerializerJson;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypeJsonParser;

public class ConfigItemSerializerJson
{
    private FieldTypeJsonParser fieldTypeJsonParser = new FieldTypeJsonParser();

    public static void generate( ConfigItem configItem, JsonGenerator g )
        throws IOException
    {
        if ( configItem instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) configItem, g );
        }
        else if ( configItem instanceof Field )
        {
            generateField( (Field) configItem, g );
        }
        else if ( configItem instanceof TemplateReference )
        {
            generateReference( (TemplateReference) configItem, g );
        }
    }

    private static void generateField( final Field field, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "configItemType", field.getConfigItemType().toString() );
        g.writeStringField( "path", field.getPath().toString() );
        field.getFieldType().getJsonGenerator().generate( field.getFieldType(), g );
        g.writeStringField( "name", field.getName() );
        g.writeStringField( "label", field.getLabel() );
        g.writeBooleanField( "required", field.isRequired() );
        g.writeBooleanField( "immutable", field.isImmutable() );
        MultipleSerializerJson.generate( field.getMultiple(), g );
        g.writeBooleanField( "indexed", field.isIndexed() );
        g.writeStringField( "customText", field.getCustomText() );
        g.writeStringField( "validationRegexp", field.getValidationRegexp() );
        g.writeStringField( "helpText", field.getHelpText() );
        if ( field.getFieldType().requiresConfig() && field.getFieldTypeConfig() != null )
        {
            g.writeFieldName( "fieldTypeConfig" );
            field.getFieldType().getFieldTypeConfigJsonGenerator().generateBase( field.getFieldTypeConfig(), g );
        }

        g.writeEndObject();
    }

    private static void generateFieldSet( final FieldSet fieldSet, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "configItemType", fieldSet.getConfigItemType().toString() );
        g.writeStringField( "type", fieldSet.getType().toString() );
        g.writeStringField( "path", fieldSet.getPath().toString() );
        g.writeStringField( "name", fieldSet.getName() );
        g.writeStringField( "label", fieldSet.getLabel() );
        g.writeBooleanField( "required", fieldSet.isRequired() );
        g.writeBooleanField( "immutable", fieldSet.isImmutable() );
        MultipleSerializerJson.generate( fieldSet.getMultiple(), g );
        g.writeStringField( "customText", fieldSet.getCustomText() );
        g.writeStringField( "helpText", fieldSet.getHelpText() );
        ConfigItemsSerializerJson.generate( fieldSet.getConfigItems(), g );

        g.writeEndObject();
    }

    private static void generateReference( final TemplateReference templateReference, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "configItemType", templateReference.getConfigItemType().toString() );
        g.writeStringField( "path", templateReference.getPath().toString() );
        g.writeStringField( "name", templateReference.getName() );
        g.writeStringField( "reference", templateReference.getTemplateQualifiedName().toString() );
        g.writeEndObject();
    }

    public ConfigItem parse( final JsonNode configItemNode )
    {
        ConfigItemType configItemType = ConfigItemType.valueOf( JsonParserUtil.getStringValue( "configItemType", configItemNode ) );

        ConfigItem configItem;

        if ( configItemType == ConfigItemType.FIELD )
        {
            configItem = parseField( configItemNode );
        }
        else if ( configItemType == ConfigItemType.FIELD_SET )
        {
            configItem = parseFieldSet( configItemNode );
        }
        else if ( configItemType == ConfigItemType.REFERENCE )
        {
            configItem = parseTemplateReference( configItemNode );
        }
        else
        {
            throw new JsonParsingException( "Unknown ConfigItemType: " + configItemType );
        }

        configItem.setPath( new ConfigItemPath( JsonParserUtil.getStringValue( "path", configItemNode ) ) );
        return configItem;
    }

    private ConfigItem parseField( final JsonNode configItemNode )
    {
        Field.Builder builder = Field.newBuilder();
        builder.name( JsonParserUtil.getStringValue( "name", configItemNode ) );
        builder.label( JsonParserUtil.getStringValue( "label", configItemNode, null ) );
        builder.required( JsonParserUtil.getBooleanValue( "required", configItemNode ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", configItemNode ) );
        builder.helpText( JsonParserUtil.getStringValue( "helpText", configItemNode ) );
        builder.customText( JsonParserUtil.getStringValue( "customText", configItemNode ) );

        parseMultiple( builder, configItemNode.get( "multiple" ) );
        parseFieldType( builder, configItemNode.get( "fieldType" ) );
        parseFieldTypeConfig( builder, configItemNode.get( "fieldTypeConfig" ) );

        return builder.build();
    }

    private ConfigItem parseFieldSet( final JsonNode configItemNode )
    {
        FieldSet.Builder builder = FieldSet.newBuilder();
        builder.name( JsonParserUtil.getStringValue( "name", configItemNode ) );
        builder.label( JsonParserUtil.getStringValue( "label", configItemNode, null ) );
        builder.required( JsonParserUtil.getBooleanValue( "required", configItemNode ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", configItemNode ) );
        builder.helpText( JsonParserUtil.getStringValue( "helpText", configItemNode ) );
        builder.customText( JsonParserUtil.getStringValue( "customText", configItemNode ) );

        parseMultiple( builder, configItemNode.get( "multiple" ) );
        parseFieldSetType( builder, configItemNode.get( "type" ) );

        return builder.build();
    }

    private ConfigItem parseTemplateReference( final JsonNode configItemNode )
    {
        TemplateReference.Builder builder = TemplateReference.newBuilder();
        builder.name( JsonParserUtil.getStringValue( "name", configItemNode ) );
        builder.template( new TemplateQualifiedName( JsonParserUtil.getStringValue( "reference", configItemNode ) ) );

        return builder.build();
    }

    private void parseFieldSetType( final FieldSet.Builder builder, final JsonNode fieldSetTypeNode )
    {
        if ( fieldSetTypeNode != null )
        {
            builder.type( FieldSetType.valueOf( fieldSetTypeNode.getTextValue() ) );
        }
    }

    private void parseMultiple( final Field.Builder builder, final JsonNode multipleNode )
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

    private void parseMultiple( final FieldSet.Builder builder, final JsonNode multipleNode )
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

    private void parseFieldTypeConfig( final Field.Builder builder, final JsonNode fieldTypeConfigNode )
    {
        if ( fieldTypeConfigNode != null )
        {
            builder.fieldTypeConfig( FieldTypeConfigProxySerializerJson.parse( fieldTypeConfigNode ) );
        }
    }

    private void parseFieldType( final Field.Builder builder, final JsonNode fieldTypeNode )
    {
        if ( fieldTypeNode != null )
        {
            FieldType fieldType = fieldTypeJsonParser.parse( fieldTypeNode );
            builder.type( fieldType );
        }
    }
}
