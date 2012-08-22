package com.enonic.wem.core.content.type.configitem;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldType;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypeConfigProxySerializerJson;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypeJsonParser;

import static com.enonic.wem.core.content.type.configitem.FieldSet.newFieldSet;
import static com.enonic.wem.core.content.type.configitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.content.type.configitem.VisualFieldSet.newVisualFieldSet;

public class ConfigItemSerializerJson
{
    private FieldTypeJsonParser fieldTypeJsonParser = new FieldTypeJsonParser();

    private final ConfigItemsSerializerJson configItemsSerializerJson;


    public ConfigItemSerializerJson()
    {
        this.configItemsSerializerJson = new ConfigItemsSerializerJson();
    }

    public ConfigItemSerializerJson( final ConfigItemsSerializerJson configItemsSerializerJson )
    {
        this.configItemsSerializerJson = configItemsSerializerJson;
    }

    public static void generate( ConfigItem configItem, JsonGenerator g )
        throws IOException
    {
        if ( configItem instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) configItem, g );
        }
        else if ( configItem instanceof VisualFieldSet )
        {
            generateVisualFieldSet( (VisualFieldSet) configItem, g );
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
        OccurrencesSerializerJson.generate( field.getOccurrences(), g );
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
        g.writeStringField( "path", fieldSet.getPath().toString() );
        g.writeStringField( "name", fieldSet.getName() );
        g.writeStringField( "label", fieldSet.getLabel() );
        g.writeBooleanField( "required", fieldSet.isRequired() );
        g.writeBooleanField( "immutable", fieldSet.isImmutable() );
        OccurrencesSerializerJson.generate( fieldSet.getOccurrences(), g );
        g.writeStringField( "customText", fieldSet.getCustomText() );
        g.writeStringField( "helpText", fieldSet.getHelpText() );
        ConfigItemsSerializerJson.generate( fieldSet.getConfigItems(), g );

        g.writeEndObject();
    }

    private static void generateVisualFieldSet( final VisualFieldSet visualFieldSet, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "configItemType", visualFieldSet.getConfigItemType().toString() );
        g.writeStringField( "label", visualFieldSet.getLabel() );
        ConfigItemsSerializerJson.generate( visualFieldSet.getConfigItems(), g );

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
        g.writeStringField( "templateType", templateReference.getTemplateType().toString() );
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
        else if ( configItemType == ConfigItemType.VISUAL_FIELD_SET )
        {
            configItem = parseVisualFieldSet( configItemNode );
        }
        else if ( configItemType == ConfigItemType.REFERENCE )
        {
            configItem = parseTemplateReference( configItemNode );
        }
        else
        {
            throw new JsonParsingException( "Unknown ConfigItemType: " + configItemType );
        }

        applyPath( configItemNode, configItem );
        return configItem;
    }

    private void applyPath( final JsonNode configItemNode, final ConfigItem configItem )
    {
        if ( configItem.getConfigItemType() != ConfigItemType.VISUAL_FIELD_SET )
        {
            configItem.setPath( new ConfigItemPath( JsonParserUtil.getStringValue( "path", configItemNode ) ) );
        }
    }

    private ConfigItem parseField( final JsonNode configItemNode )
    {
        final Field.Builder builder = Field.newBuilder();
        builder.name( JsonParserUtil.getStringValue( "name", configItemNode ) );
        builder.label( JsonParserUtil.getStringValue( "label", configItemNode, null ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", configItemNode ) );
        builder.helpText( JsonParserUtil.getStringValue( "helpText", configItemNode ) );
        builder.customText( JsonParserUtil.getStringValue( "customText", configItemNode ) );

        parseOccurrences( builder, configItemNode.get( "occurrences" ) );
        parseFieldType( builder, configItemNode.get( "fieldType" ) );
        parseFieldTypeConfig( builder, configItemNode.get( "fieldTypeConfig" ) );

        return builder.build();
    }

    private ConfigItem parseFieldSet( final JsonNode configItemNode )
    {
        final FieldSet.Builder builder = newFieldSet();
        builder.name( JsonParserUtil.getStringValue( "name", configItemNode ) );
        builder.label( JsonParserUtil.getStringValue( "label", configItemNode, null ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", configItemNode ) );
        builder.immutable( JsonParserUtil.getBooleanValue( "immutable", configItemNode ) );
        builder.helpText( JsonParserUtil.getStringValue( "helpText", configItemNode ) );
        builder.customText( JsonParserUtil.getStringValue( "customText", configItemNode ) );

        parseOccurrences( builder, configItemNode.get( "occurrences" ) );

        final ConfigItems configItems = configItemsSerializerJson.parse( configItemNode.get( "items" ) );
        for ( ConfigItem configItem : configItems )
        {
            builder.add( configItem );
        }

        return builder.build();
    }

    private ConfigItem parseVisualFieldSet( final JsonNode configItemNode )
    {
        final VisualFieldSet.Builder builder = newVisualFieldSet();
        builder.label( JsonParserUtil.getStringValue( "label", configItemNode, null ) );

        final ConfigItems configItems = configItemsSerializerJson.parse( configItemNode.get( "items" ) );
        for ( ConfigItem configItem : configItems )
        {
            builder.add( configItem );
        }

        return builder.build();
    }

    private ConfigItem parseTemplateReference( final JsonNode configItemNode )
    {
        final TemplateReference.Builder builder = newTemplateReference();
        builder.name( JsonParserUtil.getStringValue( "name", configItemNode ) );
        builder.template( new TemplateQualifiedName( JsonParserUtil.getStringValue( "reference", configItemNode ) ) );
        builder.type( TemplateType.valueOf( JsonParserUtil.getStringValue( "templateType", configItemNode ) ) );
        return builder.build();
    }

    private void parseOccurrences( final Field.Builder builder, final JsonNode occurrencesNode )
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

    private void parseOccurrences( final FieldSet.Builder builder, final JsonNode occurrencesNode )
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
