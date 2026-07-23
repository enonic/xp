package com.enonic.xp.core.impl.app;

import java.util.regex.Pattern;

public final class SchemaResourcePaths
{
    // group(1): descriptor path relative to the cms root (without extension), group(2): schema name, group(3): extension
    public static final Pattern SCHEMA_RESOURCE_PATTERN = Pattern.compile( "^" + NamespaceAppConstants.CMS_ROOT_NAME + "/((?:" +
                                                                               String.join( "|",
                                                                                            NamespaceAppConstants.CONTENT_TYPE_ROOT_NAME,
                                                                                            NamespaceAppConstants.FORM_FRAGMENTS_ROOT_NAME,
                                                                                            NamespaceAppConstants.MIXINS_ROOT_NAME,
                                                                                            NamespaceAppConstants.PART_ROOT_NAME,
                                                                                            NamespaceAppConstants.LAYOUT_ROOT_NAME,
                                                                                            NamespaceAppConstants.PAGE_ROOT_NAME,
                                                                                            NamespaceAppConstants.MACROS_ROOT_NAME ) +
                                                                               ")/([^/]+)/\\2|" + NamespaceAppConstants.CMS_ROOT_NAME +
                                                                               "|" + NamespaceAppConstants.STYLE_ROOT_NAME + "/" +
                                                                               NamespaceAppConstants.STYLE_NAME + ")\\.(yaml|yml)$" );

    private SchemaResourcePaths()
    {
    }

    public static boolean isSchemaDescriptorPath( final String path )
    {
        final String normalized = path.startsWith( "/" ) ? path.substring( 1 ) : path;
        return SCHEMA_RESOURCE_PATTERN.matcher( normalized ).matches();
    }
}
