package com.enonic.xp.lib.portal.url;

import java.util.Objects;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.UrlStrategyFacade;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ImageUrlHandler
    implements ScriptBean
{
    private PortalRequest request;

    private PortalUrlService urlService;

    private UrlStrategyFacade urlStrategyFacade;

    private String id;

    private String path;

    private String background;

    private Integer quality;

    private String filter;

    private String format;

    private String scale;

    private String projectName;

    private String branch;

    private String contentKey;

    private boolean offline;

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = context.getBinding( PortalRequest.class ).get();
        this.urlService = context.getService( PortalUrlService.class ).get();
        this.urlStrategyFacade = context.getService( UrlStrategyFacade.class ).get();
    }

    public void setId( final String id )
    {
        this.id = id;
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    public void setBackground( final String background )
    {
        this.background = background;
    }

    public void setQuality( final Integer quality )
    {
        this.quality = quality;
    }

    public void setFilter( final String filter )
    {
        this.filter = filter;
    }

    public void setFormat( final String format )
    {
        this.format = format;
    }

    public void setScale( final String scale )
    {
        this.scale = scale;
    }

    public void setProjectName( final String projectName )
    {
        this.projectName = projectName;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public void setContentKey( final String contentKey )
    {
        this.contentKey = contentKey;
    }

    public void setOffline( final Boolean offline )
    {
        this.offline = Objects.requireNonNullElse( offline, false );
    }

    public String createUrl()
    {
        final ImageUrlParams params = new ImageUrlParams().id( this.id )
            .path( this.path )
            .background( this.background )
            .quality( this.quality )
            .filter( this.filter )
            .format( this.format )
            .scale( this.scale )
            .projectName( this.projectName )
            .branch( this.branch )
            .contentKey( this.contentKey );

        final ImageUrlGeneratorParams generatorParams = this.offline || this.request == null
            ? this.urlStrategyFacade.offlineImageUrlParams( params )
            : this.urlStrategyFacade.requestImageUrlParams( params );

        return this.urlService.imageUrl( generatorParams );
    }

}
