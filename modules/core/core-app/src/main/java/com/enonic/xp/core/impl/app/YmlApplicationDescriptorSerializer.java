package com.enonic.xp.core.impl.app;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.core.impl.schema.mapper.PropertyValueSerializer;
import com.enonic.xp.util.GenericValue;

/**
 * Writes an {@link ApplicationDescriptor} as the application descriptor YAML ({@code enonic.yaml}), the inverse of
 * {@link YmlApplicationDescriptorParser}. The icon is not part of the descriptor file and is ignored. Fields without a value
 * are left out, {@code title} and {@code description} are written in the localized form ({@code text} and {@code i18n}) only when
 * they carry an i18n key.
 */
final class YmlApplicationDescriptorSerializer
{
    static final String KIND = "Application";

    private static final YmlParserBase PARSER = new YmlParserBase();

    private YmlApplicationDescriptorSerializer()
    {
    }

    static String serialize( final ApplicationDescriptor descriptor )
    {
        final ObjectNode root = PARSER.newObjectNode();
        root.put( "kind", KIND );
        root.put( "name", descriptor.getKey().getName() );
        putLocalized( root, "title", descriptor.getTitle(), descriptor.getTitleI18nKey() );
        putLocalized( root, "description", descriptor.getDescription(), descriptor.getDescriptionI18nKey() );
        putIfPresent( root, "vendorName", descriptor.getVendorName() );
        putIfPresent( root, "vendorUrl", descriptor.getVendorUrl() );
        putIfPresent( root, "url", descriptor.getUrl() );

        final GenericValue config = descriptor.getSchemaConfig();
        if ( !config.properties().isEmpty() )
        {
            root.set( "config", PropertyValueSerializer.toYml( config, JsonNodeFactory.instance ) );
        }

        return PARSER.toYaml( root );
    }

    private static void putLocalized( final ObjectNode root, final String field, final String text, final String i18n )
    {
        if ( i18n != null )
        {
            final ObjectNode localized = root.putObject( field );
            localized.put( "text", text != null ? text : "" );
            localized.put( "i18n", i18n );
        }
        else
        {
            putIfPresent( root, field, text );
        }
    }

    private static void putIfPresent( final ObjectNode root, final String field, final String value )
    {
        if ( value != null && !value.isEmpty() )
        {
            root.put( field, value );
        }
    }
}
