package com.enonic.xp.lib.content;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigsDataSerializer;

public final class GetSiteConfigHandler
    extends BaseContextHandler
{
    private String key;

    private String applicationKey;

    @Override
    protected Object doExecute()
    {
        validate();
        final Supplier<Site> siteSupplier;
        if ( key.startsWith( "/" ) )
        {
            siteSupplier = () -> contentService.findNearestSiteByPath( ContentPath.from( key ) );
        }
        else
        {
            siteSupplier = () -> contentService.getNearestSite( ContentId.from( key ) );
        }
        return Optional.ofNullable( callAsContentAdmin( siteSupplier::get ) )
            .flatMap( site -> Optional.ofNullable( new SiteConfigsDataSerializer().fromProperties( site.getData().getRoot() )
                                                       .build()
                                                       .get( ApplicationKey.from( applicationKey ) ) ) )
            .map( SiteConfig::getConfig )
            .map( PropertyTreeMapper::new )
            .orElse( null );
    }

    private void validate()
    {
        if ( key == null || key.isEmpty() )
        {
            throw new IllegalArgumentException( "Parameter 'key' is required" );
        }
        if ( applicationKey == null )
        {
            throw new IllegalArgumentException( "Parameter 'applicationKey' is required" );
        }
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setApplicationKey( final String applicationKey )
    {
        this.applicationKey = applicationKey;
    }

    private <T> T callAsContentAdmin( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        return ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.CONTENT_MANAGER_ADMIN ).build() )
            .build()
            .callWith( callable );
    }
}
