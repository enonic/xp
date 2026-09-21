package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Defines which application resources are "schema resources": the application descriptor and icon in the root of the application,
 * and descriptors, schema icons and i18n phrases located under {@code cms/}.
 * These are the resources persisted as nodes for applications that own their schema (shipping {@code cms/cms.yaml}).
 */
public final class SchemaResourcePaths
{
    /**
     * Paths of the cms descriptor inside an application jar/bundle. An application shipping one of these owns its schema.
     */
    public static final List<String> CMS_DESCRIPTOR_PATHS =
        List.of( SchemaResourceNames.CMS_ROOT_NAME + "/" + SchemaResourceNames.CMS_ROOT_NAME + ".yaml",
                 SchemaResourceNames.CMS_ROOT_NAME + "/" + SchemaResourceNames.CMS_ROOT_NAME + ".yml" );

    /**
     * Name of the node the application descriptor ({@code enonic.yaml} or {@code enonic.yml}) is persisted as.
     */
    public static final String APP_DESCRIPTOR_NAME = "enonic.yaml";

    /**
     * Name of the node the application icon is persisted as.
     */
    public static final String APP_ICON_NAME = "enonic.svg";

    /**
     * Names of the nodes below the application node that make up the persisted schema. They are replaced as a whole.
     */
    public static final List<String> PERSISTED_ROOT_NAMES = List.of( SchemaResourceNames.CMS_ROOT_NAME, APP_DESCRIPTOR_NAME, APP_ICON_NAME );

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

    // application descriptor in the application root, without extension (always "enonic")
    public static final String APP_DESCRIPTOR_GROUP = "appDescriptor";

    // application descriptor extension: yaml or yml
    public static final String APP_DESCRIPTOR_EXTENSION_GROUP = "appDescriptorExtension";

    // application icon in the application root, with extension
    public static final String APP_ICON_PATH_GROUP = "appIconPath";

    private static final String SCHEMA_NAME_2_GROUP = "iconName";

    private static final String DESCRIPTOR_ROOTS =
        String.join( "|", SchemaResourceNames.CONTENT_TYPE_ROOT_NAME, SchemaResourceNames.FORM_FRAGMENTS_ROOT_NAME,
                     SchemaResourceNames.MIXINS_ROOT_NAME, SchemaResourceNames.PART_ROOT_NAME, SchemaResourceNames.LAYOUT_ROOT_NAME,
                     SchemaResourceNames.PAGE_ROOT_NAME, MACROS_ROOT_NAME );

    // icons exist for content types, form fragments, mixins, parts and macros
    private static final String ICON_ROOTS =
        String.join( "|", SchemaResourceNames.CONTENT_TYPE_ROOT_NAME, SchemaResourceNames.FORM_FRAGMENTS_ROOT_NAME,
                     SchemaResourceNames.MIXINS_ROOT_NAME, SchemaResourceNames.PART_ROOT_NAME, MACROS_ROOT_NAME );

    private static final String CMS_RESOURCES =
        SchemaResourceNames.CMS_ROOT_NAME + "/(?:(?<" + DESCRIPTOR_PATH_GROUP + ">(?:" + DESCRIPTOR_ROOTS + ")/(?<" + SCHEMA_NAME_GROUP +
            ">[^/]+)/\\k<" + SCHEMA_NAME_GROUP + ">|" + SchemaResourceNames.CMS_ROOT_NAME + "|" + SchemaResourceNames.STYLE_ROOT_NAME + "/" +
            SchemaResourceNames.STYLE_NAME + ")\\.(?<" + EXTENSION_GROUP + ">yaml|yml)|(?<" + ICON_PATH_GROUP + ">(?:" + ICON_ROOTS +
            ")/(?<" + SCHEMA_NAME_2_GROUP + ">[^/]+)/\\k<" + SCHEMA_NAME_2_GROUP + ">\\.(?:" + SVG_EXTENSION + "|" + PNG_EXTENSION +
            "))|(?<" + PHRASES_PATH_GROUP + ">" + I18N_ROOT_NAME + "/" + PHRASES_ROOT_NAME + "/[^/]+\\.properties))";

    private static final String ROOT_RESOURCES =
        "(?<" + APP_DESCRIPTOR_GROUP + ">enonic)\\.(?<" + APP_DESCRIPTOR_EXTENSION_GROUP + ">yaml|yml)|(?<" + APP_ICON_PATH_GROUP + ">" +
            APP_ICON_NAME + ")";

    public static final Pattern SCHEMA_RESOURCE_PATTERN = Pattern.compile( "^(?:" + CMS_RESOURCES + "|" + ROOT_RESOURCES + ")$" );

    private SchemaResourcePaths()
    {
    }

    public static boolean isSchemaResourcePath( final String path )
    {
        return SCHEMA_RESOURCE_PATTERN.matcher( normalize( path ) ).matches();
    }

    /**
     * {@code true} for the paths of the persisted application descriptor and icon ({@code enonic.yaml}, {@code enonic.svg}),
     * the only persisted resources outside {@code cms/}.
     */
    public static boolean isPersistedRootResource( final String path )
    {
        final String normalized = normalize( path );
        return APP_DESCRIPTOR_NAME.equals( normalized ) || APP_ICON_NAME.equals( normalized );
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

    private static String normalize( final String path )
    {
        return path.startsWith( "/" ) ? path.substring( 1 ) : path;
    }
}