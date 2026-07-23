package com.enonic.xp.core.impl.app;

import java.util.regex.Pattern;

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

    public static final Pattern SCHEMA_RESOURCE_PATTERN = Pattern.compile( "^" + NamespaceAppConstants.CMS_ROOT_NAME + "/(?:(?<" +
                                                                               DESCRIPTOR_PATH_GROUP + ">(?:" +
                                                                               String.join( "|",
                                                                                            NamespaceAppConstants.CONTENT_TYPE_ROOT_NAME,
                                                                                            NamespaceAppConstants.FORM_FRAGMENTS_ROOT_NAME,
                                                                                            NamespaceAppConstants.MIXINS_ROOT_NAME,
                                                                                            NamespaceAppConstants.PART_ROOT_NAME,
                                                                                            NamespaceAppConstants.LAYOUT_ROOT_NAME,
                                                                                            NamespaceAppConstants.PAGE_ROOT_NAME,
                                                                                            NamespaceAppConstants.MACROS_ROOT_NAME ) +
                                                                               ")/(?<" + SCHEMA_NAME_GROUP + ">[^/]+)/\\k<" +
                                                                               SCHEMA_NAME_GROUP + ">|" +
                                                                               NamespaceAppConstants.CMS_ROOT_NAME + "|" +
                                                                               NamespaceAppConstants.STYLE_ROOT_NAME + "/" +
                                                                               NamespaceAppConstants.STYLE_NAME + ")\\.(?<" +
                                                                               EXTENSION_GROUP + ">yaml|yml)|(?<" + PHRASES_PATH_GROUP +
                                                                               ">" + NamespaceAppConstants.I18N_ROOT_NAME + "/" +
                                                                               NamespaceAppConstants.PHRASES_ROOT_NAME +
                                                                               "/[^/]+\\.properties))$" );

    private SchemaResourcePaths()
    {
    }

    public static boolean isSchemaDescriptorPath( final String path )
    {
        final String normalized = path.startsWith( "/" ) ? path.substring( 1 ) : path;
        return SCHEMA_RESOURCE_PATTERN.matcher( normalized ).matches();
    }
}
