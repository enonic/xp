package com.enonic.xp.core.impl.app;

import java.time.Instant;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.FakeCmsYmlUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.server.VersionInfo;

public class VirtualAppFactory
{
    public static ApplicationAdaptor create( final ApplicationKey applicationKey, final NodeService nodeService )
    {
        return new ApplicationAdaptor()
        {
            @Override
            public ApplicationUrlResolver getUrlResolver()
            {
                return new MultiApplicationUrlResolver( new NodeResourceApplicationUrlResolver( applicationKey, nodeService ),
                                                        new FakeCmsYmlUrlResolver( applicationKey, nodeService) );
            }

            @Override
            public void setConfig( final Configuration config )
            {

            }

            @Override
            public ApplicationKey getKey()
            {
                return applicationKey;
            }

            @Override
            public Version getVersion()
            {
                return Version.emptyVersion;
            }

            @Override
            public String getDisplayName()
            {
                return applicationKey.toString();
            }

            @Override
            public String getSystemVersion()
            {
                return VersionInfo.get().getVersion();
            }

            @Override
            public String getMaxSystemVersion()
            {
                return VersionInfo.get().getVersion();
            }

            @Override
            public String getMinSystemVersion()
            {
                return VersionInfo.get().getVersion();
            }

            @Override
            public boolean includesSystemVersion( final Version version )
            {
                return true;
            }

            @Override
            public String getUrl()
            {
                return null;
            }

            @Override
            public String getVendorName()
            {
                return null;
            }

            @Override
            public String getVendorUrl()
            {
                return null;
            }

            @Override
            public Bundle getBundle()
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
                return Instant.now();
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
                return ConfigBuilder.create().build();
            }

            @Override
            public boolean isSystem()
            {
                return false;
            }
        };
    }
}
