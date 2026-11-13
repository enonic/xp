package com.enonic.xp.core.impl.app;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * An application bundle read before anything is written for it: its manifest and the schema resources to persist, validated.
 * The global installation and the migration of the application nodes go through this, so what they persist is the same.
 *
 * @param appInfo         the manifest of the bundle
 * @param schemaResources the resources to persist below the application node: the application descriptor and icon, and the
 *                        {@code cms} schema when the bundle ships {@code cms/cms.yaml}. The descriptor always declares the
 *                        version and the system version range of the application: what the bundle's own descriptor leaves out
 *                        is taken from the manifest, an application with a node reads them from its persisted descriptor only
 */
record PreparedApplication(AppInfo appInfo, Map<String, ByteSource> schemaResources)
{
    private static final Logger LOG = LoggerFactory.getLogger( PreparedApplication.class );

    /**
     * @throws ApplicationBundleException when the bundle cannot be read, is a system application, or a descriptor it ships is
     *                                    not of the kind its path reserves
     */
    static PreparedApplication prepare( final ByteSource byteSource )
    {
        final AppInfo appInfo;
        final Map<String, ByteSource> schemaResources;
        try
        {
            appInfo = AppInfoResolver.resolve( byteSource );
            // the application descriptor and icon of every application are persisted in nodes and served from there; an application
            // shipping cms/cms.yaml owns its schema, which is persisted the same way
            schemaResources = new LinkedHashMap<>( AppSchemaResolver.resolve( byteSource ) );
        }
        catch ( Exception e )
        {
            throw new ApplicationBundleException( "Cannot install application", e );
        }

        if ( appInfo.system )
        {
            throw new ApplicationBundleException( "Cannot install system application globally" );
        }

        // without cms/cms.yaml the bundle does not own a schema: whatever it ships below cms/ is neither persisted nor served
        if ( !appInfo.hasCmsDescriptor )
        {
            final boolean dropped = schemaResources.keySet().removeIf( path -> path.startsWith( SchemaResourceNames.CMS_ROOT_NAME + "/" ) );
            if ( dropped )
            {
                LOG.warn( "Application [{}] ships schema resources below cms/ but no cms/cms.yaml: they are ignored", appInfo.name );
            }
        }

        // a descriptor declaring another kind than its path reserves rejects the application before anything is written
        AppSchemaValidator.validate( ApplicationKey.from( appInfo.name ), schemaResources );

        declareManifestVersions( appInfo, schemaResources );

        return new PreparedApplication( appInfo, schemaResources );
    }

    /**
     * Completes the application descriptor with the version ({@code Bundle-Version}) and the system version range
     * ({@code X-System-Version}) of the manifest, where the descriptor declares none of its own. A bundle shipping no descriptor
     * gets one holding them.
     */
    private static void declareManifestVersions( final AppInfo appInfo, final Map<String, ByteSource> schemaResources )
    {
        final ApplicationKey key = ApplicationKey.from( appInfo.name );
        final ByteSource shipped = schemaResources.get( SchemaResourcePaths.APP_DESCRIPTOR_NAME );
        final ApplicationDescriptor descriptor;
        try
        {
            descriptor = shipped != null
                ? YmlApplicationDescriptorParser.parse( shipped.asCharSource( StandardCharsets.UTF_8 ).read(), key ).build()
                : ApplicationDescriptor.create().key( key ).build();
        }
        catch ( Exception e )
        {
            throw new ApplicationBundleException( "Cannot install application", e );
        }

        // what both the descriptor and the manifest declare must be the same: the application is rejected otherwise
        requireSameVersion( key, "version", descriptor.getVersion(), "Bundle-Version", appInfo.version );
        requireSameVersion( key, "minSystemVersion", descriptor.getMinSystemVersion(), "X-System-Version", appInfo.minSystemVersion );
        requireSameVersion( key, "maxSystemVersion", descriptor.getMaxSystemVersion(), "X-System-Version", appInfo.maxSystemVersion );

        final ApplicationDescriptor.Builder completed = ApplicationDescriptor.copyOf( descriptor );
        boolean changed = false;
        // the descriptor holds versions as the manifest writes them, its file declares them as semantic versions: 1.2.3.SNAPSHOT
        // is written as 1.2.3-SNAPSHOT
        final String manifestVersion = manifestVersion( "Bundle-Version", appInfo.version );
        if ( isNullOrEmpty( descriptor.getVersion() ) && manifestVersion != null )
        {
            completed.version( manifestVersion );
            changed = true;
        }
        // a range declared by the descriptor, even by one end, is its own: the manifest does not complete it
        if ( isNullOrEmpty( descriptor.getMinSystemVersion() ) && isNullOrEmpty( descriptor.getMaxSystemVersion() ) &&
            ( !isNullOrEmpty( appInfo.minSystemVersion ) || !isNullOrEmpty( appInfo.maxSystemVersion ) ) )
        {
            completed.minSystemVersion( manifestVersion( "X-System-Version", appInfo.minSystemVersion ) );
            completed.maxSystemVersion( manifestVersion( "X-System-Version", appInfo.maxSystemVersion ) );
            changed = true;
        }
        if ( changed )
        {
            schemaResources.put( SchemaResourcePaths.APP_DESCRIPTOR_NAME, ByteSource.wrap(
                YmlApplicationDescriptorSerializer.serialize( completed.build() ).getBytes( StandardCharsets.UTF_8 ) ) );
        }
    }

    // a version of the manifest that is not a version (the framework tolerates one) is not declared: the descriptor would not
    // parse with it
    private static String manifestVersion( final String header, final String value )
    {
        if ( isNullOrEmpty( value ) )
        {
            return null;
        }
        final Version version = ApplicationHelper.toVersion( value );
        if ( version == null )
        {
            LOG.warn( "{} [{}] is not a valid version, it is not declared by the application descriptor", header, value );
            return null;
        }
        return version.toString();
    }

    private static void requireSameVersion( final ApplicationKey key, final String field, final String declared, final String header,
                                            final String manifestValue )
    {
        final String mismatch = ApplicationHelper.versionMismatch( key, field, declared, header, manifestValue );
        if ( mismatch != null )
        {
            throw new ApplicationBundleException( mismatch );
        }
    }

    ApplicationKey key()
    {
        return ApplicationKey.from( appInfo.name );
    }
}
