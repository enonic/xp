package com.enonic.xp.lib.portal.url;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ImageUrlHandler
    implements ScriptBean
{
    private PortalUrlService urlService;

    private String id;

    private String path;

    private String urlType;

    private String background;

    private Integer quality;

    private String filter;

    private String format;

    private String scale;

    private String projectName;

    private String branch;

    private String baseUrlKey;

    private boolean offline;

    private Map<String, Collection<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlService = context.getService( PortalUrlService.class ).get();
    }

    public void setId( final String id )
    {
        this.id = id;
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    public void setUrlType( final String urlType )
    {
        this.urlType = urlType;
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

    public void setBaseUrlKey( final String baseUrlKey )
    {
        this.baseUrlKey = baseUrlKey;
    }

    public void setOffline( final Boolean offline )
    {
        this.offline = Objects.requireNonNullElse( offline, false );
    }

    public void addQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public String createUrl()
    {
        final ImageUrlParams params = new ImageUrlParams().id( this.id )
            .path( this.path )
            .type( this.urlType )
            .background( this.background )
            .quality( this.quality )
            .filter( this.filter )
            .format( this.format )
            .scale( this.scale )
            .projectName( this.projectName )
            .branch( this.branch )
            .baseUrlKey( this.baseUrlKey )
            .offline( this.offline );

        if ( this.queryParams != null )
        {
            this.queryParams.forEach( ( key, values ) -> values.forEach( value -> params.param( key, value ) ) );
        }

        return this.urlService.imageUrl( params );
    }

}
