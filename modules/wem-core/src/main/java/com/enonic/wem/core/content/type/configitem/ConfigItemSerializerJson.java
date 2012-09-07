package com.enonic.wem.core.content.type.configitem;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldType;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypeConfigSerializerJson;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypeSerializerJson;

import static com.enonic.wem.core.content.type.configitem.FieldSet.newFieldSet;
import static com.enonic.wem.core.content.type.configitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.content.type.configitem.VisualFieldSet.newVisualFieldSet;

public class ConfigItemSerializerJson
{
    private FieldTypeSerializerJson fieldTypeSerializer = new FieldTypeSerializerJson();

    private FieldTypeConfigSerializerJson fieldTypeConfigSerializer = new FieldTypeConfigSerializerJson();

    private final ConfigItemsSerializerJson configItemsSerializerJson;


    public ConfigItemSerializerJson()
    {
        this.configItemsSerializerJson = new ConfigItemsSerializerJson();
    }

    public ConfigItemSerializerJson( final ConfigItemsSerializerJson configItemsSerializerJson )
    {
        this.configItemsSerializerJson = configItemsSerializerJson;
    }

    public void generate( ConfigItem configItem, JsonGenerator g )
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
        else if ( configItem instanceof Component )
        {
            generateField( (Component) configItem, g );
        }
        else if ( configItem instanceof TemplateReference )
        {
            generateReference( (TemplateReference) configItem, g );
        }
    }

    private void generateField( final Component component, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "configItemType", component.getConfigItemType().toString() );
        g.writeStringField( "path", component.getPath().toString() );
        g.writeStringField( "name", component.getName() );
        fieldTypeSerializer.generate( component.getFieldType(), g );
        g.writeStringField( "label", component.getLabel() );
        g.writeBooleanField( "required", component.isRequired() );
        g.writeBooleanField( "immutable", component.isImmutable() );
        OccurrencesSerializerJson.generate( component.getOccurrences(), g );
        g.writeBooleanField( "indexed", component.isIndexed() );
        g.writeStringField( "customText", component.getCustomText() );
        g.writeStringField( "validationRegexp", component.getValidationRegexp() );
        g.writeStringField( "helpText", component.getHelpText() );
        if ( component.getFieldType().requiresConfig() && component.getFieldTypeConfig() != null )
        {
            g.writeFieldName( "fieldTypeConfig" );
            component.getFieldType().getFieldTypeConfigJsonGenerator().generate( component.getFieldTypeConfig(), g );
        }

        g.writeEndObject();
    }

    private void generateFieldSet( final FieldSet fieldSet, JsonGenerator g )
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
        configItemsSerializerJson.generate( fieldSet.getConfigItems(), g );

        g.writeEndObject();
    }

    private void generateVisualFieldSet( final VisualFieldSet visualFieldSet, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "configItemType", visualFieldSet.getConfigItemType().toString() );
        g.writeStringField( "label", visualFieldSet.getLabel() );
        g.writeStringField( "name", visualFieldSet.getName() );
        configItemsSerializerJson.generate( visualFieldSet.getConfigItems(), g );

        g.writeEndObject();
    }

    private void generateReference( final TemplateReference templateReference, final JsonGenerator g )
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

        if ( configItem instanceof DirectAccessibleConfigItem )
        {
            applyPath( configItemNode, (DirectAccessibleConfigItem) configItem );
        }
        return configItem;
    }

    private void applyPath( final JsonNode configItemNode, final DirectAccessibleConfigItem configItem )
    {
        if ( configItem.getConfigItemType() != ConfigItemType.VISUAL_FIELD_SET )
        {
            configItem.setPath( new ConfigItemPath( JsonParserUtil.getStringValue( "path", configItemNode ) ) );
        }
    }

    private DirectAccessibleConfigItem parseField( final JsonNode configItemNode )
    {
        final Component.Builder builder = Component.newBuilder();
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

    private DirectAccessibleConfigItem parseFieldSet( final JsonNode configItemNode )
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
        for ( ConfigItem configItem : configItems.iterable() )
        {
            builder.add( configItem );
        }

        return builder.build();
    }

    private ConfigItem parseVisualFieldSet( final JsonNode configItemNode )
    {
        final VisualFieldSet.Builder builder = newVisualFieldSet();
        builder.label( JsonParserUtil.getStringValue( "label", configItemNode, null ) );
        builder.name( JsonParserUtil.getStringValue( "name", configItemNode, null ) );

        final ConfigItems configItems = configItemsSerializerJson.parse( configItemNode.get( "items" ) );
        for ( ConfigItem configItem : configItems.iterable() )
        {
            builder.add( configItem );
        }

        return builder.build();
    }

    private DirectAccessibleConfigItem parseTemplateReference( final JsonNode configItemNode )
    {
        final TemplateReference.Builder builder = newTemplateReference();
        builder.name( JsonParserUtil.getStringValue( "name", configItemNode ) );
        builder.template( new TemplateQualifiedName( JsonParserUtil.getStringValue( "reference", configItemNode ) ) );
        builder.type( TemplateType.valueOf( JsonParserUtil.getStringValue( "templateType", configItemNode ) ) );
        return builder.build();
    }

    private void parseOccurrences( final Component.Builder builder, final JsonNode occurrencesNode )
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

    private void parseFieldTypeConfig( final Component.Builder builder, final JsonNode fieldTypeConfigNode )
    {
        if ( fieldTypeConfigNode != null )
        {
            builder.fieldTypeConfig( fieldTypeConfigSerializer.parse( fieldTypeConfigNode ) );
        }
    }

    private void parseFieldType( final Component.Builder builder, final JsonNode fieldTypeNode )
    {
        if ( fieldTypeNode != null )
        {
            FieldType fieldType = fieldTypeSerializer.parse( fieldTypeNode );
            builder.type( fieldType );
        }
    }
}
