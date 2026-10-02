package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.PageUrlPartsParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.UrlBase;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.serializer.MapSerializable;

/**
 * Backs {@code pageUrlParts} of {@code /lib/xp/portal}.
 */
public final class PageUrlPartsHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String id;

    private String path;

    private UrlBase base;

    private Map<String, List<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public void setId( final String id )
    {
        this.id = id;
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    public void setBase( final UrlBase base )
    {
        this.base = base;
    }

    public void setQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public MapSerializable createParts()
    {
        final PageUrlPartsParams.Builder params = PageUrlPartsParams.create().setId( this.id ).setPath( this.path ).setBase( this.base );

        if ( this.queryParams != null )
        {
            params.setQueryParams( this.queryParams );
        }

        return UrlPartsMapper.of( urlServiceSupplier.get().pageUrlParts( params.build() ) );
    }
}
