package com.enonic.xp.lib.portal.url;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.ImageMediaUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class ImageMediaUrlHandler
    implements ScriptBean
{
    protected Supplier<PortalRequest> requestSupplier;

    protected Supplier<PortalUrlService> urlServiceSupplier;

    private String contentId;

    private String contentPath;

    private String urlType;

    private ContextPathType contextPathType;

    private Multimap<String, String> queryParams;

    private String projectName;

    private String branch;

    private String background;

    private Integer quality;

    private String filter;

    private String format;

    private String scale;

    @Override
    public void initialize( final BeanContext context )
    {
        this.requestSupplier = context.getBinding( PortalRequest.class );
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public void setId( final String contentId )
    {
        this.contentId = contentId;
    }

    public void setPath( final String contentPath )
    {
        this.contentPath = contentPath;
    }

    public void setUrlType( final String urlType )
    {
        this.urlType = urlType;
    }

    public void setContextPathType( final String contextPathType )
    {
        this.contextPathType = ContextPathType.from( contextPathType );
    }

    public void setQueryParams( final ScriptValue params )
    {
        if ( params == null )
        {
            return;
        }

        this.queryParams = HashMultimap.create();

        for ( final Map.Entry<String, Object> param : params.getMap().entrySet() )
        {
            final Object value = param.getValue();
            if ( value instanceof Iterable values )
            {
                for ( final Object v : values )
                {
                    queryParams.put( param.getKey(), v.toString() );
                }
            }
            else
            {
                queryParams.put( param.getKey(), value.toString() );
            }
        }
    }

    public void setProjectName( final String projectName )
    {
        this.projectName = projectName;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
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

    public String createUrl()
    {
        final ImageMediaUrlParams imageMediaUrlParams = ImageMediaUrlParams.create()
            .contentId( this.contentId )
            .contentPath( this.contentPath )
            .contextPathType( this.contextPathType )
            .webRequest( this.requestSupplier.get() )
            .urlType( this.urlType )
            .addQueryParams( this.queryParams )
            .background( this.background )
            .scale( this.scale )
            .filter( this.filter )
            .quality( this.quality )
            .format( this.format )
            .projectName( this.projectName )
            .branch( this.branch )
            .build();

        return null;
//        return urlServiceSupplier.get().imageMediaUrl( imageMediaUrlParams );
    }

}
