package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.impl.server.rest.IconHash;

public class ApplicationInfoJson
{
    private final String key;

    private final String version;

    private final String title;

    private final String maxSystemVersion;

    private final String minSystemVersion;

    private final String url;

    private final String vendorName;

    private final String vendorUrl;

    private final Instant modifiedTime;

    private final String state;

    private final boolean local;

    private final boolean system;

    private final @Nullable String iconHash;

    private ApplicationInfoJson( final String key, final String version, final String title, final String maxSystemVersion,
                                 final String minSystemVersion, final String url, final String vendorName, final String vendorUrl,
                                 final Instant modifiedTime, final String state, final boolean local,
                                 final boolean system, final @Nullable String iconHash )
    {
        this.key = key;
        this.version = version;
        this.title = title;
        this.maxSystemVersion = maxSystemVersion;
        this.minSystemVersion = minSystemVersion;
        this.url = url;
        this.vendorName = vendorName;
        this.vendorUrl = vendorUrl;
        this.modifiedTime = modifiedTime;
        this.state = state;
        this.local = local;
        this.system = system;
        this.iconHash = iconHash;
    }

    public static ApplicationInfoJson create( final Application application, final ApplicationDescriptor descriptor, final boolean local )
    {
        final Icon icon = descriptor != null ? descriptor.getIcon() : null;
        return new ApplicationInfoJson( application.getKey().toString(), application.getVersion().toString(),
                                        descriptor != null ? descriptor.getTitle() : null, application.getMaxSystemVersion(),
                                        application.getMinSystemVersion(), descriptor != null ? descriptor.getUrl() : null,
                                        descriptor != null ? descriptor.getVendorName() : null,
                                        descriptor != null ? descriptor.getVendorUrl() : null, application.getModifiedTime(),
                                        application.isStarted() ? "started" : "stopped", local,
                                        application.isSystem(), icon != null ? IconHash.of( icon ) : null );
    }

    public String getKey()
    {
        return key;
    }

    public String getVersion()
    {
        return version;
    }

    public String getTitle()
    {
        return title;
    }

    public String getMaxSystemVersion()
    {
        return maxSystemVersion;
    }

    public String getMinSystemVersion()
    {
        return minSystemVersion;
    }

    public String getUrl()
    {
        return url;
    }

    public String getVendorName()
    {
        return vendorName;
    }

    public String getVendorUrl()
    {
        return vendorUrl;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public String getState()
    {
        return state;
    }

    public boolean getLocal()
    {
        return local;
    }

    public boolean getSystem()
    {
        return system;
    }

    public @Nullable String getIconHash()
    {
        return iconHash;
    }
}
