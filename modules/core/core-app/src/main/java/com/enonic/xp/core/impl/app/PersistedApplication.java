package com.enonic.xp.core.impl.app;

import java.time.Instant;
import java.util.Set;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.node.Node;
import com.enonic.xp.util.Version;

/**
 * An application that exists as nodes only, created through {@link com.enonic.xp.app.ApplicationDescriptorService#createOrUpdate}:
 * it ships a persisted descriptor, and a schema once its cms descriptor is persisted, but no bundle, hence no code, class loader or
 * configuration. It is a schema application as soon as it owns a schema. There is nothing to start or stop, it is always started.
 */
final class PersistedApplication
    implements Application
{
    private static final Configuration EMPTY_CONFIG = ConfigBuilder.create().build();

    private final ApplicationKey key;

    private final Instant modifiedTime;

    private final boolean schema;

    /**
     * @param schema whether the cms descriptor ({@code cms/cms.yaml}) is persisted for the application
     */
    PersistedApplication( final Node applicationNode, final boolean schema )
    {
        this.key = ApplicationKey.from( applicationNode.name().toString() );
        final Instant modified = applicationNode.data().getInstant( ApplicationPropertyNames.MODIFIED_TIME );
        this.modifiedTime = modified != null ? modified : applicationNode.getTimestamp();
        this.schema = schema;
    }

    @Override
    public ApplicationKey getKey()
    {
        return this.key;
    }

    @Override
    public Version getVersion()
    {
        return Version.emptyVersion;
    }

    @Override
    public String getSystemVersion()
    {
        return null;
    }

    @Override
    public String getMaxSystemVersion()
    {
        return null;
    }

    @Override
    public String getMinSystemVersion()
    {
        return null;
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
    public boolean isSchema()
    {
        return this.schema;
    }
}
