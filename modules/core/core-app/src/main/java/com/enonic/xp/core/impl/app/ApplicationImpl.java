package com.enonic.xp.core.impl.app;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.VersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.util.Version;

import static com.google.common.base.Strings.emptyToNull;

public final class ApplicationImpl
    implements ApplicationAdaptor
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationImpl.class );

    private final ApplicationKey key;

    private final VersionRange systemVersion;

    private final boolean persisted;

    private final BooleanSupplier locked;

    private final BooleanSupplier schemaModified;

    private volatile DescriptorVersions descriptorVersions;

    private final Bundle bundle;

    private final ApplicationUrlResolver urlResolver;

    private final ClassLoader classLoader;

    private volatile Configuration config;

    private final Set<String> capabilities;

    private volatile ServiceRegistration<Application> reference;

    /**
     * An application without a node in the application repository: its versions are the ones of the manifest of its bundle.
     */
    public ApplicationImpl( final Bundle bundle, final ApplicationUrlResolver urlResolver, final ClassLoader classLoader )
    {
        this( bundle, urlResolver, classLoader, false );
    }

    /**
     * @param persisted whether the application has a node in the application repository, holding its descriptor: its versions
     *                  are then the ones the persisted descriptor declares
     */
    public ApplicationImpl( final Bundle bundle, final ApplicationUrlResolver urlResolver, final ClassLoader classLoader,
                            final boolean persisted )
    {
        this( bundle, urlResolver, classLoader, persisted, () -> false, () -> false );
    }

    /**
     * @param persisted        whether the application has a node in the application repository, holding its descriptor: its
     *                         versions are then the ones the persisted descriptor declares
     * @param locked reads the flag kept on the node of the application, see {@link Application#isLocked()}
     * @param schemaModified   reads from the node of the application whether its definition was changed since its install, see
     *                         {@link Application#isSchemaModified()}
     */
    public ApplicationImpl( final Bundle bundle, final ApplicationUrlResolver urlResolver, final ClassLoader classLoader,
                            final boolean persisted, final BooleanSupplier locked, final BooleanSupplier schemaModified )
    {
        this.bundle = bundle;
        this.key = ApplicationHelper.getApplicationKey( bundle );
        this.systemVersion = ApplicationHelper.parseVersionRange( getHeader( ApplicationManifestConstants.X_SYSTEM_VERSION, null ) );
        this.urlResolver = urlResolver;
        this.persisted = persisted;
        this.locked = locked;
        this.schemaModified = schemaModified;
        this.classLoader = classLoader != null ? classLoader : new BundleClassLoader( this.bundle );
        this.capabilities = ApplicationHelper.getCapabilities( bundle );
    }

    @Override
    public ApplicationKey getKey()
    {
        return this.key;
    }

    @Override
    public Version getVersion()
    {
        if ( !this.persisted )
        {
            return Version.parseVersion( this.bundle.getVersion().toString() );
        }
        final DescriptorVersions declared = descriptorVersions();
        return declared != null ? Version.parseVersion( declared.version() ) : Version.emptyVersion;
    }

    @Override
    public String getSystemVersion()
    {
        final VersionRange range = systemVersionRange();
        return range != null ? range.toString() : null;
    }

    @Override
    public String getMaxSystemVersion()
    {
        final VersionRange range = systemVersionRange();
        return range != null && range.getRight() != null ? range.getRight().toString() : null;
    }

    @Override
    public String getMinSystemVersion()
    {
        final VersionRange range = systemVersionRange();
        return range != null ? range.getLeft().toString() : null;
    }

    private VersionRange systemVersionRange()
    {
        if ( !this.persisted )
        {
            return this.systemVersion;
        }
        final DescriptorVersions declared = descriptorVersions();
        return declared != null ? declared.systemVersion() : null;
    }

    /**
     * The version and system version range declared by the descriptor ({@code enonic.yaml}) persisted on the node of the
     * application: an application with a node reads them from there and nowhere else, the manifest of its bundle is not
     * consulted. A local or a system application has no node, the manifest is all it has. The versions are read again once the
     * node changes, see {@link ApplicationUrlResolver#revision()}.
     */
    private DescriptorVersions descriptorVersions()
    {
        if ( this.urlResolver == null )
        {
            return null;
        }
        try
        {
            final Object revision = this.urlResolver.revision();
            final DescriptorVersions cached = this.descriptorVersions;
            if ( cached != null && this.urlResolver.isStable() && Objects.equals( cached.revision(), revision ) )
            {
                return cached;
            }
            final ApplicationDescriptor descriptor = ApplicationHelper.loadDescriptor( this.key, this.urlResolver );
            final DescriptorVersions loaded = descriptor == null
                ? new DescriptorVersions( revision, null, null )
                : new DescriptorVersions( revision, emptyToNull( descriptor.getVersion() ),
                                          ApplicationHelper.toSystemVersionRange( descriptor.getMinSystemVersion(),
                                                                                  descriptor.getMaxSystemVersion() ) );
            this.descriptorVersions = loaded;
            return loaded;
        }
        catch ( final Exception e )
        {
            // a descriptor that cannot be read or parsed declares nothing: not cached, the next call reads it again
            LOG.debug( "Unable to read the versions of application {} from its descriptor", this.key, e );
            return null;
        }
    }

    private record DescriptorVersions(Object revision, String version, VersionRange systemVersion)
    {
    }

    @Override
    public Bundle getBundle()
    {
        return this.bundle;
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return this.classLoader;
    }

    @Override
    public Instant getModifiedTime()
    {
        return Instant.ofEpochMilli( this.bundle.getLastModified() );
    }

    @Override
    public boolean isStarted()
    {
        return this.bundle.getState() == Bundle.ACTIVE;
    }

    private String getHeader( final String name, final String defValue )
    {
        return ApplicationHelper.getHeader( this.bundle, name, defValue );
    }

    @Override
    public ApplicationUrlResolver getUrlResolver()
    {
        return urlResolver;
    }

    @Override
    public Configuration getConfig()
    {
        return this.config;
    }

    @Override
    public void setConfig( final Configuration config )
    {
        this.config = config;
    }

    @Override
    public Set<String> getCapabilities()
    {
        return this.capabilities;
    }

    @Override
    public boolean isSystem()
    {
        return ApplicationBundleUtils.isSystemApplication( this.bundle );
    }

    @Override
    public boolean isCodeless()
    {
        return ApplicationHelper.isCodelessApplication( this.bundle );
    }

    @Override
    public boolean isLocal()
    {
        return ApplicationHelper.isLocalApplication( this.bundle );
    }

    @Override
    public boolean isLocked()
    {
        return this.locked.getAsBoolean();
    }

    @Override
    public boolean isSchemaModified()
    {
        return this.schemaModified.getAsBoolean();
    }

    @Override
    public ServiceRegistration<Application> getRegistration()
    {
        return reference;
    }

    @Override
    public void setRegistration( final ServiceRegistration<Application> registration )
    {
        this.reference = registration;
    }
}
