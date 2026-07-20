package com.enonic.xp.core.impl.app;

import java.util.regex.Pattern;

public final class SchemaResourcePaths
{
    // group(1): descriptor path relative to the cms root (without extension), group(2): schema name, group(3): extension
    public static final Pattern SCHEMA_RESOURCE_PATTERN = Pattern.compile( "^" + VirtualAppConstants.CMS_ROOT_NAME + "/((?:" +
                                                                               String.join( "|",
                                                                                            VirtualAppConstants.CONTENT_TYPE_ROOT_NAME,
                                                                                            VirtualAppConstants.FORM_FRAGMENTS_ROOT_NAME,
                                                                                            VirtualAppConstants.MIXINS_ROOT_NAME,
                                                                                            VirtualAppConstants.PART_ROOT_NAME,
                                                                                            VirtualAppConstants.LAYOUT_ROOT_NAME,
                                                                                            VirtualAppConstants.PAGE_ROOT_NAME,
                                                                                            VirtualAppConstants.MACROS_ROOT_NAME ) +
                                                                               ")/([^/]+)/\\2|" + VirtualAppConstants.CMS_ROOT_NAME +
                                                                               "|" + VirtualAppConstants.STYLE_ROOT_NAME + "/" +
                                                                               VirtualAppConstants.STYLE_NAME + ")\\.(yaml|yml)$" );

    private SchemaResourcePaths()
    {
    }

    public static boolean isSchemaDescriptorPath( final String path )
    {
        final String normalized = path.startsWith( "/" ) ? path.substring( 1 ) : path;
        return SCHEMA_RESOURCE_PATTERN.matcher( normalized ).matches();
    }
}
