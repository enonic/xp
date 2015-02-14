package com.enonic.xp.core.impl.module;

import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import org.osgi.framework.Bundle;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;

import com.enonic.xp.form.Form;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleVersion;
import com.enonic.xp.schema.mixin.MixinNames;

final class ModuleImpl
    implements Module
{
    protected ModuleKey moduleKey;

    protected ModuleVersion moduleVersion;

    // TODO: Use Bundle-Name header.
    protected String displayName;

    // TODO: Use X-Module-Url header.
    protected String url;

    // TODO: Use X-Vendor-Name header.
    protected String vendorName;

    // TODO: Use X-Vendor-Url header.
    protected String vendorUrl;

    protected Form config;

    protected Bundle bundle;

    protected MixinNames metaSteps;

    public ModuleKey getKey()
    {
        return this.moduleKey;
    }

    public ModuleVersion getVersion()
    {
        return moduleVersion;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String getMaxSystemVersion()
    {
        // TODO: Use X-System-Version header. VersionRange.
        return "5.1";
    }

    @Override
    public String getMinSystemVersion()
    {
        // TODO: Use X-System-Version header. VersionRange.
        return "5.0";
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

    public Form getConfig()
    {
        return config;
    }

    @Override
    public Bundle getBundle()
    {
        return this.bundle;
    }

    @Override
    public MixinNames getMetaSteps()
    {
        return metaSteps;
    }

    @Override
    public URL getResource( final String path )
    {
        if ( this.bundle.getState() != Bundle.ACTIVE )
        {
            return null;
        }
        return this.bundle.getResource( path );
    }

    @Override
    public Set<String> getResourcePaths()
    {
        if ( this.bundle.getState() != Bundle.ACTIVE )
        {
            return Sets.newHashSet();
        }
        final Set<String> set = Sets.newHashSet();
        findResourcePaths( set, this.bundle, "/" );
        return set;
    }

    private void findResourcePaths( final Set<String> set, final Bundle bundle, final String parentPath )
    {
        final Enumeration<URL> paths = bundle.findEntries( parentPath, "*", true );
        if ( paths == null )
        {
            return;
        }
        while ( paths.hasMoreElements() )
        {
            final URL path = paths.nextElement();
            set.add( path.getPath().replaceFirst( "^/", "" ) );
        }
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "moduleKey", moduleKey ).
            add( "displayName", displayName ).
            add( "url", url ).
            add( "vendorName", vendorName ).
            add( "vendorUrl", vendorUrl ).
            add( "metaSteps", metaSteps ).
            add( "config", config ).
            omitNullValues().
            toString();
    }
}
