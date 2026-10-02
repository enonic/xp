package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.PageUrlPartsParams;
import com.enonic.xp.portal.url.PortalUrlService;
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

    private String baseId;

    private String basePath;

    private String baseProjectName;

    private String baseBranch;

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

    public void setBaseId( final String baseId )
    {
        this.baseId = baseId;
    }

    public void setBasePath( final String basePath )
    {
        this.basePath = basePath;
    }

    public void setBaseProjectName( final String baseProjectName )
    {
        this.baseProjectName = baseProjectName;
    }

    public void setBaseBranch( final String baseBranch )
    {
        this.baseBranch = baseBranch;
    }

    public void setQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public MapSerializable createParts()
    {
        final BaseUrlParams base = BaseUrlParams.create()
            .setId( this.baseId )
            .setPath( this.basePath )
            .setProjectName( this.baseProjectName )
            .setBranch( this.baseBranch )
            .build();

        final PageUrlPartsParams.Builder params = PageUrlPartsParams.create().setId( this.id ).setPath( this.path ).setBase( base );

        if ( this.queryParams != null )
        {
            params.setQueryParams( this.queryParams );
        }

        return UrlPartsMapper.of( urlServiceSupplier.get().pageUrlParts( params.build() ) );
    }
}
