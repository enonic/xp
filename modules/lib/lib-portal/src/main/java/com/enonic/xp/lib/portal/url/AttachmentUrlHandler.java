package com.enonic.xp.lib.portal.url;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class AttachmentUrlHandler
    implements ScriptBean
{
    private PortalUrlService urlService;

    private String id;

    private String path;

    private String urlType;

    private String name;

    private String label;

    private String projectName;

    private String branch;

    private String baseUrl;

    private boolean download;

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

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setLabel( final String label )
    {
        this.label = label;
    }

    public void setProjectName( final String projectName )
    {
        this.projectName = projectName;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public void setBaseUrl( final String baseUrl )
    {
        this.baseUrl = baseUrl;
    }

    public void setDownload( final Boolean download )
    {
        this.download = Objects.requireNonNullElse( download, false );
    }

    public void addQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public String createUrl()
    {
        final AttachmentUrlParams params = new AttachmentUrlParams().id( this.id )
            .path( this.path )
            .type( this.urlType )
            .name( this.name )
            .label( this.label )
            .download( this.download )
            .projectName( this.projectName )
            .branch( this.branch )
            .baseUrl( this.baseUrl );

        if ( this.queryParams != null )
        {
            this.queryParams.forEach( ( key, values ) -> values.forEach( value -> params.param( key, value ) ) );
        }

        return urlService.attachmentUrl( params );
    }
}
