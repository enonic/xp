package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines which application resources are "schema resources": the application descriptor and icon in the root of the application
 * ({@code enonic.yaml|yml}, {@code enonic.svg}, or the legacy {@code application.yaml|yml}, {@code application.svg}),
 * and descriptors, schema icons and i18n phrases located under {@code cms/}. Schemas are matched in the flat structure
 * ({@code cms/<kind>/<name>.yaml}) and in the legacy folder structure ({@code cms/<kind>/<name>/<name>.yaml}) of application bundles;
 * they are persisted in the flat structure only. The style descriptor keeps its folder: {@code cms/style/style.yaml}.
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

    public static final String SVG_EXTENSION = "svg";

    public static final String PNG_EXTENSION = "png";

    public static final String SVG_MIME_TYPE = "image/svg+xml";

    public static final String PNG_MIME_TYPE = "image/png";

    // folder of a schema below the cms root: content-types, parts, ...
    public static final String KIND_GROUP = "kind";

    // schema name of a descriptor in the legacy folder structure <kind>/<name>/<name>.yaml|yml
    public static final String NAME_GROUP = "name";

    // schema name of a descriptor in the flat structure <kind>/<name>.yaml|yml
    public static final String FLAT_NAME_GROUP = "flatName";

    // descriptor extension: yaml or yml
    public static final String EXTENSION_GROUP = "extension";

    // folder of a schema icon below the cms root
    public static final String ICON_KIND_GROUP = "iconKind";

    // schema name of an icon in the legacy folder structure <kind>/<name>/<name>.svg|png
    public static final String ICON_NAME_GROUP = "iconName";

    // schema name of an icon in the flat structure <kind>/<name>.svg|png
    public static final String FLAT_ICON_NAME_GROUP = "flatIconName";

    // icon extension: svg or png
    public static final String ICON_EXTENSION_GROUP = "iconExtension";

    // single descriptor of the cms: cms (cms/cms.yaml) or style/style (cms/style/style.yaml)
    public static final String CMS_DESCRIPTOR_GROUP = "cmsDescriptor";

    // extension of a descriptor in the cms root: yaml or yml
    public static final String CMS_DESCRIPTOR_EXTENSION_GROUP = "cmsDescriptorExtension";

    // phrases .properties path relative to the cms root, with extension
    public static final String PHRASES_PATH_GROUP = "phrasesPath";

    // application descriptor in the application root, without extension: enonic or the legacy application
    public static final String APP_DESCRIPTOR_GROUP = "appDescriptor";

    // application descriptor extension: yaml or yml
    public static final String APP_DESCRIPTOR_EXTENSION_GROUP = "appDescriptorExtension";

    // application icon in the application root, without extension: enonic or the legacy application
    public static final String APP_ICON_GROUP = "appIcon";

    /**
     * Name of the legacy application descriptor ({@code application.yaml|yml}) and icon ({@code application.svg}) still shipped by
     * older application bundles, persisted as {@code enonic.yaml} and {@code enonic.svg}.
     */
    public static final String LEGACY_APP_RESOURCE_NAME = "application";

    /**
     * The style descriptor keeps its folder: {@code cms/style/style.yaml}.
     */
    public static final String STYLE_DESCRIPTOR = SchemaResourceNames.STYLE_ROOT_NAME + "/" + SchemaResourceNames.STYLE_NAME;

    private static final String DESCRIPTOR_ROOTS =
        String.join( "|", SchemaResourceNames.CONTENT_TYPE_ROOT_NAME, SchemaResourceNames.FORM_FRAGMENTS_ROOT_NAME,
                     SchemaResourceNames.MIXINS_ROOT_NAME, SchemaResourceNames.PART_ROOT_NAME, SchemaResourceNames.LAYOUT_ROOT_NAME,
                     SchemaResourceNames.PAGE_ROOT_NAME, SchemaResourceNames.MACROS_ROOT_NAME );

    // icons exist for content types, form fragments, mixins, parts and macros
    private static final String ICON_ROOTS =
        String.join( "|", SchemaResourceNames.CONTENT_TYPE_ROOT_NAME, SchemaResourceNames.FORM_FRAGMENTS_ROOT_NAME,
                     SchemaResourceNames.MIXINS_ROOT_NAME, SchemaResourceNames.PART_ROOT_NAME, SchemaResourceNames.MACROS_ROOT_NAME );

    // a schema is either flat (<kind>/<name>.<ext>) or in the legacy folder structure (<kind>/<name>/<name>.<ext>)
    private static final String DESCRIPTORS =
        "(?<" + KIND_GROUP + ">" + DESCRIPTOR_ROOTS + ")/(?:(?<" + NAME_GROUP + ">[^/]+)/\\k<" + NAME_GROUP + ">|(?<" + FLAT_NAME_GROUP +
            ">[^/]+))\\.(?<" + EXTENSION_GROUP + ">yaml|yml)";

    private static final String ICONS =
        "(?<" + ICON_KIND_GROUP + ">" + ICON_ROOTS + ")/(?:(?<" + ICON_NAME_GROUP + ">[^/]+)/\\k<" + ICON_NAME_GROUP + ">|(?<" +
            FLAT_ICON_NAME_GROUP + ">[^/]+))\\.(?<" + ICON_EXTENSION_GROUP + ">" + SVG_EXTENSION + "|" + PNG_EXTENSION + ")";

    private static final String CMS_DESCRIPTORS =
        "(?<" + CMS_DESCRIPTOR_GROUP + ">" + SchemaResourceNames.CMS_ROOT_NAME + "|" + STYLE_DESCRIPTOR + ")\\.(?<" +
            CMS_DESCRIPTOR_EXTENSION_GROUP + ">yaml|yml)";

    private static final String PHRASES =
        "(?<" + PHRASES_PATH_GROUP + ">" + SchemaResourceNames.I18N_ROOT_NAME + "/" + SchemaResourceNames.PHRASES_ROOT_NAME +
            "/[^/]+\\.properties)";

    private static final String CMS_RESOURCES =
        SchemaResourceNames.CMS_ROOT_NAME + "/(?:" + DESCRIPTORS + "|" + ICONS + "|" + CMS_DESCRIPTORS + "|" + PHRASES + ")";

    private static final String APP_RESOURCE_NAMES = "enonic|" + LEGACY_APP_RESOURCE_NAME;

    private static final String ROOT_RESOURCES =
        "(?<" + APP_DESCRIPTOR_GROUP + ">" + APP_RESOURCE_NAMES + ")\\.(?<" + APP_DESCRIPTOR_EXTENSION_GROUP + ">yaml|yml)|(?<" +
            APP_ICON_GROUP + ">" + APP_RESOURCE_NAMES + ")\\." + SVG_EXTENSION;

    public static final Pattern SCHEMA_RESOURCE_PATTERN = Pattern.compile( "^(?:" + CMS_RESOURCES + "|" + ROOT_RESOURCES + ")$" );

    // kind a descriptor must declare, by the folder it is persisted in
    private static final Map<String, String> KINDS_BY_ROOT =
        Map.of( SchemaResourceNames.CONTENT_TYPE_ROOT_NAME, "ContentType", SchemaResourceNames.FORM_FRAGMENTS_ROOT_NAME, "FormFragment",
                SchemaResourceNames.MIXINS_ROOT_NAME, "Mixin", SchemaResourceNames.PART_ROOT_NAME, "Part",
                SchemaResourceNames.PAGE_ROOT_NAME, "Page", SchemaResourceNames.LAYOUT_ROOT_NAME, "Layout",
                SchemaResourceNames.MACROS_ROOT_NAME, "Macro" );

    private static final Pattern PERSISTED_SCHEMA_DESCRIPTOR =
        Pattern.compile( "^" + SchemaResourceNames.CMS_ROOT_NAME + "/(?<root>[^/]+)/[^/]+\\.yaml$" );

    private SchemaResourcePaths()
    {
    }

    /**
     * The {@code kind} the descriptor persisted at {@code path} (relative to the application node, as returned by
     * {@link AppSchemaResolver}) must declare: {@code enonic.yaml} is an {@code Application}, the reserved {@code cms/cms.yaml}
     * and {@code cms/style/style.yaml} are {@code CMS} and {@code Style}, a schema declares the kind of its folder
     * ({@code cms/content-types/<name>.yaml} is a {@code ContentType}, ...). {@code null} for resources that are not descriptors.
     */
    public static String expectedKind( final String path )
    {
        final String normalized = normalize( path );
        if ( APP_DESCRIPTOR_NAME.equals( normalized ) )
        {
            return "Application";
        }
        if ( CMS_DESCRIPTOR_PATHS.get( 0 ).equals( normalized ) )
        {
            return "CMS";
        }
        if ( ( SchemaResourceNames.CMS_ROOT_NAME + "/" + STYLE_DESCRIPTOR + ".yaml" ).equals( normalized ) )
        {
            return "Style";
        }
        final Matcher matcher = PERSISTED_SCHEMA_DESCRIPTOR.matcher( normalized );
        return matcher.matches() ? KINDS_BY_ROOT.get( matcher.group( "root" ) ) : null;
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