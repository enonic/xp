package com.enonic.xp.lib.portal.url;

import java.util.function.Supplier;

import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class BaseUrlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String urlType;

    private String projectName;

    private String branch;

    private String id;

    private String path;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public BaseUrlHandler setUrlType( final String urlType )
    {
        this.urlType = urlType;
        return this;
    }

    public BaseUrlHandler setProjectName( final String projectName )
    {
        this.projectName = projectName;
        return this;
    }

    public BaseUrlHandler setBranch( final String branch )
    {
        this.branch = branch;
        return this;
    }

    public BaseUrlHandler setId( final String id )
    {
        this.id = id;
        return this;
    }

    public BaseUrlHandler setPath( final String path )
    {
        this.path = path;
        return this;
    }

    public String createUrl()
    {
        return this.urlServiceSupplier.get()
            .baseUrl( BaseUrlParams.create()
                          .setUrlType( this.urlType )
                          .setProjectName( this.projectName )
                          .setBranch( this.branch )
                          .setId( this.id )
                          .setPath( this.path )
                          .build() );
    }
}
