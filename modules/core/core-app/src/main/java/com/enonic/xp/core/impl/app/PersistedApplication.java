package com.enonic.xp.core.impl.app;

import java.time.Instant;
import java.util.Set;

import org.osgi.framework.VersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.node.Node;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.util.Version;

/**
 * An application that exists as nodes only, created through {@link com.enonic.xp.app.ApplicationDescriptorService#createOrUpdate}:
 * it ships a persisted descriptor, and a schema once its cms descriptor is persisted, but no bundle, hence no code, class loader or
 * configuration. It is a codeless application as soon as it owns a schema. There is nothing to start or stop, it is always started.
 * Its version and system version range are the ones its persisted descriptor declares, there is no manifest to read them from.
 */
final class PersistedApplication
    implements Application
{
    private static final Logger LOG = LoggerFactory.getLogger( PersistedApplication.class );

    private static final Configuration EMPTY_CONFIG = ConfigBuilder.create().build();

    private final ApplicationKey key;

    private final Instant modifiedTime;

    private final boolean codeless;

    private final Version version;

    private final VersionRange systemVersion;

    private final boolean locked;

    private final boolean schemaModified;

    /**
     * @param codeless whether the cms descriptor ({@code cms/cms.yaml}) is persisted for the application
     */
    PersistedApplication( final Node applicationNode, final boolean codeless )
    {
        this.key = ApplicationKey.from( applicationNode.name().toString() );
        final Instant modified = applicationNode.data().getInstant( ApplicationPropertyNames.MODIFIED_TIME );
        this.modifiedTime = modified != null ? modified : applicationNode.getTimestamp();
        this.codeless = codeless;
        this.locked = ApplicationHelper.isLocked( applicationNode );
        this.schemaModified = ApplicationHelper.isSchemaModified( applicationNode );

        final ApplicationDescriptor descriptor = readDescriptor( applicationNode, this.key );
        this.version = descriptor != null ? Version.parseVersion( descriptor.getVersion() ) : Version.emptyVersion;
        this.systemVersion = descriptor != null ? systemVersionRange( descriptor ) : null;
    }

    // a descriptor that cannot be parsed declares no versions: it is reported where the descriptor itself is read
    private static ApplicationDescriptor readDescriptor( final Node applicationNode, final ApplicationKey key )
    {
        final String yaml = applicationNode.data().getString( SchemaNodePropertyNames.DESCRIPTOR );
        if ( yaml == null )
        {
            return null;
        }
        try
        {
            return YmlApplicationDescriptorParser.parse( yaml, key ).build();
        }
        catch ( final Exception e )
        {
            LOG.debug( "Unable to read the versions of application {} from its persisted descriptor", key, e );
            return null;
        }
    }

    private static VersionRange systemVersionRange( final ApplicationDescriptor descriptor )
    {
        return ApplicationHelper.toSystemVersionRange( descriptor.getMinSystemVersion(), descriptor.getMaxSystemVersion() );
    }

    @Override
    public ApplicationKey getKey()
    {
        return this.key;
    }

    @Override
    public Version getVersion()
    {
        return this.version;
    }

    @Override
    public String getSystemVersion()
    {
        return this.systemVersion != null ? this.systemVersion.toString() : null;
    }

    @Override
    public String getMaxSystemVersion()
    {
        return this.systemVersion != null && this.systemVersion.getRight() != null ? this.systemVersion.getRight().toString() : null;
    }

    @Override
    public String getMinSystemVersion()
    {
        return this.systemVersion != null ? this.systemVersion.getLeft().toString() : null;
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return null;
    }

    @Override
    public Instant getModifiedTime()
    {
        return this.modifiedTime;
    }

    @Override
    public Set<String> getCapabilities()
    {
        return Set.of();
    }

    @Override
    public boolean isStarted()
    {
        return true;
    }

    @Override
    public Configuration getConfig()
    {
        return EMPTY_CONFIG;
    }

    @Override
    public boolean isSystem()
    {
        return false;
    }

    @Override
    public boolean isCodeless()
    {
        return this.codeless;
    }

    @Override
    public boolean isLocal()
    {
        return false;
    }

    @Override
    public boolean isLocked()
    {
        return this.locked;
    }

    @Override
    public boolean isSchemaModified()
    {
        return this.schemaModified;
    }
}
