package com.enonic.xp.lib.portal.url;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class PageUrlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String id;

    private String path;

    private String type;

    private String projectName;

    private String branch;

    private Map<String, Collection<String>> queryParams;

    public void setId( final String id )
    {
        this.id = id;
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    public void setUrlType( final String type )
    {
        this.type = type;
    }

    public void setProjectName( final String projectName )
    {
        this.projectName = projectName;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public void setQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public String createUrl()
    {
        final PageUrlParams params =
            new PageUrlParams().id( this.id ).path( this.path ).type( this.type ).projectName( this.projectName ).branch( this.branch );

        if ( this.queryParams != null )
        {
            this.queryParams.forEach( ( key, values ) -> values.forEach( value -> params.param( key, value ) ) );
        }

        return urlServiceSupplier.get().pageUrl( params );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }
}
