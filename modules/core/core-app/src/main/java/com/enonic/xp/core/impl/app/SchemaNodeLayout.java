package com.enonic.xp.core.impl.app;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.BinaryReference;

/**
 * Where the resources of an application are kept below its node, and the paths they are served at.
 * <p>
 * The application descriptor ({@code enonic.yaml}) is the {@code descriptor} property of the application node, the application
 * icon a binary of that node named {@code enonic.svg} or {@code enonic.png}. Everything of the {@code cms} schema is a child of
 * the {@code cms} node: {@code cms/cms} and {@code cms/style} for the descriptors there is one of, {@code cms/<kind>:<name>} for
 * the others, e.g. {@code cms/contenttype:article}. The kind in a node name is the {@code kind} the descriptor declares, in lower
 * case, {@code phrases} for the i18n phrases, which declare none. A schema node holds its descriptor in the {@code resource}
 * property and its icon as a binary named {@code icon.svg} or {@code icon.png}.
 * <p>
 * Resource paths are the ones the application ships and is read by, relative to the application root, with or without the leading
 * slash: {@code cms/content-types/article.yaml}, {@code cms/content-types/article.svg}, {@code cms/i18n/phrases/phrases_en.properties}.
 */
public final class SchemaNodeLayout
{
    public static final String KIND_SEPARATOR = ":";

    public static final NodePath CMS_NODE_PATH = new NodePath( NodePath.ROOT, NodeName.from( SchemaResourceNames.CMS_ROOT_NAME ) );

    private static final String ICON_BINARY_NAME = "icon";

    private static final Map<String, Kind> KINDS_BY_NODE_NAME =
        Arrays.stream( Kind.values() ).collect( Collectors.toUnmodifiableMap( kind -> kind.nodeName, Function.identity() ) );

    private static final Map<String, Kind> KINDS_BY_FOLDER =
        Arrays.stream( Kind.values() ).collect( Collectors.toUnmodifiableMap( kind -> kind.folder, Function.identity() ) );

    /**
     * The kinds of resources below {@code cms}: the kind as the descriptors declare it, the folder the application ships it in
     * (relative to {@code cms}), the extension of the descriptor, whether it has an icon, and whether there is one of it per
     * application. The node name of a kind is the kind in lower case.
     */
    public enum Kind
    {
        CMS( "CMS", "", "yaml", false, true ),
        STYLE( "Style", SchemaResourceNames.STYLE_ROOT_NAME, "yaml", false, true ),
        CONTENT_TYPE( "ContentType", SchemaResourceNames.CONTENT_TYPE_ROOT_NAME, "yaml", true, false ),
        FORM_FRAGMENT( "FormFragment", SchemaResourceNames.FORM_FRAGMENTS_ROOT_NAME, "yaml", true, false ),
        MIXIN( "Mixin", SchemaResourceNames.MIXINS_ROOT_NAME, "yaml", true, false ),
        PART( "Part", SchemaResourceNames.PART_ROOT_NAME, "yaml", true, false ),
        LAYOUT( "Layout", SchemaResourceNames.LAYOUT_ROOT_NAME, "yaml", false, false ),
        PAGE( "Page", SchemaResourceNames.PAGE_ROOT_NAME, "yaml", false, false ),
        MACRO( "Macro", SchemaResourceNames.MACROS_ROOT_NAME, "yaml", true, false ),
        PHRASES( "Phrases", SchemaResourceNames.I18N_ROOT_NAME + "/" + SchemaResourceNames.PHRASES_ROOT_NAME, "properties", false, false );

        private final String kindName;

        private final String nodeName;

        private final String folder;

        private final String extension;

        private final boolean icon;

        private final boolean singleton;

        Kind( final String kindName, final String folder, final String extension, final boolean icon, final boolean singleton )
        {
            this.kindName = kindName;
            this.nodeName = kindName.toLowerCase( Locale.ROOT );
            this.folder = folder;
            this.extension = extension;
            this.icon = icon;
            this.singleton = singleton;
        }

        /**
         * The name of the node of a kind: {@code cms} and {@code style} for the singletons, {@code <kind>:<name>} otherwise.
         */
        public NodeName nodeName( final String name )
        {
            if ( singleton )
            {
                return NodeName.from( nodeName );
            }
            if ( name == null || name.isEmpty() )
            {
                throw new IllegalArgumentException( "name is required for " + kindName );
            }
            return NodeName.from( nodeName + KIND_SEPARATOR + name );
        }

        /**
         * The node of a kind, relative to the application node: {@code cms/<kind>:<name>}.
         */
        public NodePath nodePath( final String name )
        {
            return new NodePath( CMS_NODE_PATH, nodeName( name ) );
        }

        /**
         * The path the descriptor is served at, relative to the application root, with the leading slash:
         * {@code /cms/content-types/<name>.yaml}.
         */
        public String resourcePath( final String name )
        {
            return "/" + folderPath() + fileName( name ) + "." + extension;
        }

        /**
         * The path the icon is served at: {@code /cms/content-types/<name>.svg}, for the kinds that have one.
         */
        public String iconPath( final String name, final String extension )
        {
            if ( !icon )
            {
                throw new IllegalArgumentException( kindName + " has no icon" );
            }
            return "/" + folderPath() + fileName( name ) + "." + extension;
        }

        /**
         * The kind as the descriptors declare it: {@code ContentType}, {@code Part}...
         */
        public String kindName()
        {
            return kindName;
        }

        public boolean hasIcon()
        {
            return icon;
        }

        public boolean isSingleton()
        {
            return singleton;
        }

        private String folderPath()
        {
            return SchemaResourceNames.CMS_ROOT_NAME + "/" + ( folder.isEmpty() ? "" : folder + "/" );
        }

        private String fileName( final String name )
        {
            return singleton ? ( this == CMS ? SchemaResourceNames.CMS_ROOT_NAME : SchemaResourceNames.STYLE_NAME ) : name;
        }
    }

    /**
     * A persisted resource: the node holding it, relative to the application node ({@link NodePath#ROOT} for the application node
     * itself), and whether the resource is the icon of that node rather than its descriptor.
     */
    public record Location(NodePath nodePath, Kind kind, String name, boolean icon)
    {
        public boolean isApplicationNode()
        {
            return nodePath.isRoot();
        }
    }

    private SchemaNodeLayout()
    {
    }

    /**
     * Where the resource at {@code resourcePath} is persisted, {@code null} when such a resource is not persisted at all.
     */
    public static Location locate( final String resourcePath )
    {
        final String path = normalize( resourcePath );

        if ( SchemaResourcePaths.APP_DESCRIPTOR_NAME.equals( path ) )
        {
            return new Location( NodePath.ROOT, null, null, false );
        }
        if ( SchemaResourcePaths.isAppIconPath( path ) )
        {
            return new Location( NodePath.ROOT, null, null, true );
        }

        final String cmsPrefix = SchemaResourceNames.CMS_ROOT_NAME + "/";
        if ( !path.startsWith( cmsPrefix ) )
        {
            return null;
        }
        final String relative = path.substring( cmsPrefix.length() );

        final int slash = relative.lastIndexOf( '/' );
        final String folder = slash == -1 ? "" : relative.substring( 0, slash );
        final String file = relative.substring( slash + 1 );
        final int dot = file.lastIndexOf( '.' );
        if ( dot <= 0 )
        {
            return null;
        }
        final String name = file.substring( 0, dot );
        final String extension = file.substring( dot + 1 );

        final Kind kind = KINDS_BY_FOLDER.get( folder );
        if ( kind == null )
        {
            return null;
        }
        if ( kind.singleton )
        {
            // cms/cms.yaml and cms/style/style.yaml, nothing else in those folders
            return kind.extension.equals( extension ) && kind.fileName( null ).equals( name ) ? new Location( kind.nodePath( null ), kind, null, false ) : null;
        }
        if ( kind.extension.equals( extension ) )
        {
            return new Location( kind.nodePath( name ), kind, name, false );
        }
        if ( kind.icon && SchemaResourcePaths.iconMimeType( file ) != null )
        {
            return new Location( kind.nodePath( name ), kind, name, true );
        }
        return null;
    }

    /**
     * The kind and name of a schema node by its name, {@code null} for a name that is not one of a schema node.
     */
    public static Location locate( final NodeName nodeName )
    {
        final String value = nodeName.toString();
        final int separator = value.indexOf( KIND_SEPARATOR );
        final Kind kind = KINDS_BY_NODE_NAME.get( separator == -1 ? value : value.substring( 0, separator ) );
        if ( kind == null )
        {
            return null;
        }
        if ( kind.singleton )
        {
            return separator == -1 ? new Location( kind.nodePath( null ), kind, null, false ) : null;
        }
        if ( separator == -1 || separator == value.length() - 1 )
        {
            return null;
        }
        final String name = value.substring( separator + 1 );
        return new Location( kind.nodePath( name ), kind, name, false );
    }

    /**
     * The binary an icon of a schema node is attached as, named by its format: {@code icon.svg} or {@code icon.png}; {@code null} for
     * an unsupported mime type.
     */
    public static BinaryReference iconReference( final String mimeType )
    {
        if ( SchemaResourcePaths.SVG_MIME_TYPE.equals( mimeType ) )
        {
            return BinaryReference.from( ICON_BINARY_NAME + "." + SchemaResourcePaths.SVG_EXTENSION );
        }
        if ( SchemaResourcePaths.PNG_MIME_TYPE.equals( mimeType ) )
        {
            return BinaryReference.from( ICON_BINARY_NAME + "." + SchemaResourcePaths.PNG_EXTENSION );
        }
        return null;
    }

    /**
     * The mime type of the icon attached as {@code reference}, {@code null} when it is not an icon binary of a schema node.
     */
    public static String iconMimeType( final BinaryReference reference )
    {
        if ( reference == null )
        {
            return null;
        }
        final String name = reference.toString();
        return name.startsWith( ICON_BINARY_NAME + "." ) ? SchemaResourcePaths.iconMimeType( name ) : null;
    }

    /**
     * The extension of the icon served for the binary {@code reference}: {@code svg} or {@code png}.
     */
    public static String iconExtension( final BinaryReference reference )
    {
        final String name = reference.toString();
        return name.substring( name.lastIndexOf( '.' ) + 1 );
    }

    private static String normalize( final String path )
    {
        return path.startsWith( "/" ) ? path.substring( 1 ) : path;
    }
}
