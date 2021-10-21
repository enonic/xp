package com.enonic.xp.lib.content;

import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.lib.content.mapper.SiteMapper;
import com.enonic.xp.site.Site;

public final class GetSiteHandler
    extends BaseContextHandler
{
    private String key;

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
            siteSupplier = () -> this.contentService.getNearestSite( ContentId.from( key ) );
        }
        return Optional.ofNullable( siteSupplier.get() ).map( SiteMapper::new ).orElse( null );
    }

    private void validate()
    {
        if ( key == null || key.isEmpty() )
        {
            throw new IllegalArgumentException( "Parameter 'key' is required" );
        }
    }

    public void setKey( final String key )
    {
        this.key = key;
    }
}
