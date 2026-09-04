package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Defines which application resources are "schema resources": descriptors, schema icons and i18n phrases located under {@code cms/}.
 * These are the resources persisted as nodes for applications that own their schema ({@code type: Static} or shipping {@code cms/cms.yaml}).
 */
public final class SchemaResourcePaths
{
    /**
     * Paths of the cms descriptor inside an application jar/bundle. An application shipping one of these owns its schema.
     */
    public static final List<String> CMS_DESCRIPTOR_PATHS =
        List.of( VirtualAppConstants.CMS_ROOT_NAME + "/" + VirtualAppConstants.CMS_ROOT_NAME + ".yaml",
                 VirtualAppConstants.CMS_ROOT_NAME + "/" + VirtualAppConstants.CMS_ROOT_NAME + ".yml" );

    public static final String MACROS_ROOT_NAME = "macros";

    public static final String I18N_ROOT_NAME = "i18n";

    public static final String PHRASES_ROOT_NAME = "phrases";

    public static final String SVG_EXTENSION = "svg";

    public static final String PNG_EXTENSION = "png";

    public static final String SVG_MIME_TYPE = "image/svg+xml";

    public static final String PNG_MIME_TYPE = "image/png";

    // descriptor path relative to the cms root, without extension
    public static final String DESCRIPTOR_PATH_GROUP = "descriptorPath";

    // schema name (folder and file name of a descriptor)
    public static final String SCHEMA_NAME_GROUP = "schemaName";

    // descriptor extension: yaml or yml
    public static final String EXTENSION_GROUP = "extension";

    // schema icon path relative to the cms root, with extension
    public static final String ICON_PATH_GROUP = "iconPath";

    // phrases .properties path relative to the cms root, with extension
    public static final String PHRASES_PATH_GROUP = "phrasesPath";

    private static final String SCHEMA_NAME_2_GROUP = "iconName";

    private static final String DESCRIPTOR_ROOTS =
        String.join( "|", VirtualAppConstants.CONTENT_TYPE_ROOT_NAME, VirtualAppConstants.FORM_FRAGMENTS_ROOT_NAME,
                     VirtualAppConstants.MIXINS_ROOT_NAME, VirtualAppConstants.PART_ROOT_NAME, VirtualAppConstants.LAYOUT_ROOT_NAME,
                     VirtualAppConstants.PAGE_ROOT_NAME, MACROS_ROOT_NAME );

    // icons exist for content types, form fragments, mixins, parts and macros
    private static final String ICON_ROOTS =
        String.join( "|", VirtualAppConstants.CONTENT_TYPE_ROOT_NAME, VirtualAppConstants.FORM_FRAGMENTS_ROOT_NAME,
                     VirtualAppConstants.MIXINS_ROOT_NAME, VirtualAppConstants.PART_ROOT_NAME, MACROS_ROOT_NAME );

    public static final Pattern SCHEMA_RESOURCE_PATTERN = Pattern.compile(
        "^" + VirtualAppConstants.CMS_ROOT_NAME + "/(?:(?<" + DESCRIPTOR_PATH_GROUP + ">(?:" + DESCRIPTOR_ROOTS + ")/(?<" +
            SCHEMA_NAME_GROUP + ">[^/]+)/\\k<" + SCHEMA_NAME_GROUP + ">|" + VirtualAppConstants.CMS_ROOT_NAME + "|" +
            VirtualAppConstants.STYLE_ROOT_NAME + "/" + VirtualAppConstants.STYLE_NAME + ")\\.(?<" + EXTENSION_GROUP + ">yaml|yml)|(?<" +
            ICON_PATH_GROUP + ">(?:" + ICON_ROOTS + ")/(?<" + SCHEMA_NAME_2_GROUP + ">[^/]+)/\\k<" + SCHEMA_NAME_2_GROUP + ">\\.(?:" +
            SVG_EXTENSION + "|" + PNG_EXTENSION + "))|(?<" + PHRASES_PATH_GROUP + ">" + I18N_ROOT_NAME + "/" + PHRASES_ROOT_NAME +
            "/[^/]+\\.properties))$" );

    private SchemaResourcePaths()
    {
    }

    public static boolean isSchemaResourcePath( final String path )
    {
        final String normalized = path.startsWith( "/" ) ? path.substring( 1 ) : path;
        return SCHEMA_RESOURCE_PATTERN.matcher( normalized ).matches();
    }

    /**
     * Mime type of a schema icon, or {@code null} if the path is not an icon.
     */
    public static String iconMimeType( final String path )
    {
        if ( path.endsWith( "." + SVG_EXTENSION ) )
        {
            return SVG_MIME_TYPE;
        }
        else if ( path.endsWith( "." + PNG_EXTENSION ) )
        {
            return PNG_MIME_TYPE;
        }
        return null;
    }
}