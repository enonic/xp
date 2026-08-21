package com.enonic.xp.core.impl.app;

import java.util.regex.Pattern;

import com.enonic.xp.core.impl.schema.NamespaceConstants;

public final class SchemaResourcePaths
{
    // descriptor path relative to the cms root, without extension
    public static final String DESCRIPTOR_PATH_GROUP = "descriptorPath";

    // schema name (folder and file name of a descriptor)
    public static final String SCHEMA_NAME_GROUP = "schemaName";

    // descriptor extension: yaml or yml
    public static final String EXTENSION_GROUP = "extension";

    // phrases .properties path relative to the cms root, with extension
    public static final String PHRASES_PATH_GROUP = "phrasesPath";

    public static final Pattern SCHEMA_RESOURCE_PATTERN = Pattern.compile( "^" + NamespaceConstants.CMS_ROOT_NAME + "/(?:(?<" +
                                                                               DESCRIPTOR_PATH_GROUP + ">(?:" +
                                                                               String.join( "|",
                                                                                            NamespaceConstants.CONTENT_TYPE_ROOT_NAME,
                                                                                            NamespaceConstants.FORM_FRAGMENTS_ROOT_NAME,
                                                                                            NamespaceConstants.MIXINS_ROOT_NAME,
                                                                                            NamespaceConstants.PART_ROOT_NAME,
                                                                                            NamespaceConstants.LAYOUT_ROOT_NAME,
                                                                                            NamespaceConstants.PAGE_ROOT_NAME,
                                                                                            NamespaceConstants.MACROS_ROOT_NAME ) +
                                                                               ")/(?<" + SCHEMA_NAME_GROUP + ">[^/]+)/\\k<" +
                                                                               SCHEMA_NAME_GROUP + ">|" +
                                                                               NamespaceConstants.CMS_ROOT_NAME + "|" +
                                                                               NamespaceConstants.STYLE_ROOT_NAME + "/" +
                                                                               NamespaceConstants.STYLE_NAME + ")\\.(?<" +
                                                                               EXTENSION_GROUP + ">yaml|yml)|(?<" + PHRASES_PATH_GROUP +
                                                                               ">" + NamespaceConstants.I18N_ROOT_NAME + "/" +
                                                                               NamespaceConstants.PHRASES_ROOT_NAME +
                                                                               "/[^/]+\\.properties))$" );

    private SchemaResourcePaths()
    {
    }

    public static boolean isSchemaResourcePath( final String path )
    {
        final String normalized = path.startsWith( "/" ) ? path.substring( 1 ) : path;
        return SCHEMA_RESOURCE_PATTERN.matcher( normalized ).matches();
    }
}
