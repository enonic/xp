package com.enonic.xp.lib.portal.url;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.AttachmentMediaUrlParams;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class AttachmentMediaUrlHandler
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

    private String name;

    private String label;

    private Boolean download;

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

    public void setContextPathType( final ContextPathType contextPathType )
    {
        this.contextPathType = contextPathType;
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

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setLabel( final String label )
    {
        this.label = label;
    }

    public void setDownload( final Boolean download )
    {
        this.download = download;
    }

    public String createUrl()
    {
        final AttachmentMediaUrlParams params = AttachmentMediaUrlParams.create()
            .contentId( this.contentId )
            .contentPath( this.contentPath )
            .urlType( this.urlType )
            .contextPathType( this.contextPathType )
            .addQueryParams( this.queryParams )
            .projectName( this.projectName )
            .branch( this.branch )
            .name( this.name )
            .label( this.label )
            .download( this.download )
            .webRequest( this.requestSupplier.get() )
            .build();

        return this.urlServiceSupplier.get().attachmentMediaUrl( params );
    }
}
