package com.enonic.xp.core.impl.app;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;

/**
 * An application bundle read before anything is written for it: its manifest and the schema resources to persist, validated.
 * The global installation and the migration of the application nodes go through this, so what they persist is the same.
 *
 * @param appInfo         the manifest of the bundle
 * @param schemaResources the resources to persist below the application node: the application descriptor and icon, and the
 *                        {@code cms} schema when the bundle ships {@code cms/cms.yaml}
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

        return new PreparedApplication( appInfo, schemaResources );
    }

    ApplicationKey key()
    {
        return ApplicationKey.from( appInfo.name );
    }
}
