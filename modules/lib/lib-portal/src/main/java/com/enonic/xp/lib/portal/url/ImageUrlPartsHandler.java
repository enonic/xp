package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.url.ImageUrlPartsParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.UrlBase;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.serializer.MapSerializable;

/**
 * Backs {@code imageUrlParts} of {@code /lib/xp/portal}.
 */
public final class ImageUrlPartsHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String id;

    private String path;

    private String projectName;

    private String branch;

    private UrlBase base;

    private String scale;

    private Integer quality;

    private String background;

    private String format;

    private String filter;

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

    public void setProjectName( final String projectName )
    {
        this.projectName = projectName;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public void setBase( final UrlBase base )
    {
        this.base = base;
    }

    public void setScale( final String scale )
    {
        this.scale = scale;
    }

    public void setQuality( final Integer quality )
    {
        this.quality = quality;
    }

    public void setBackground( final String background )
    {
        this.background = background;
    }

    public void setFormat( final String format )
    {
        this.format = format;
    }

    public void setFilter( final String filter )
    {
        this.filter = filter;
    }

    public void setQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public MapSerializable createParts()
    {
        final ImageUrlPartsParams.Builder params = ImageUrlPartsParams.create()
            .setId( this.id )
            .setPath( this.path )
            .setScale( this.scale )
            .setQuality( this.quality )
            .setBackground( this.background )
            .setFormat( this.format )
            .setFilter( this.filter );

        params.setBase( this.base );
        if ( this.projectName != null )
        {
            final ProjectName projectName = ProjectName.from( this.projectName );
            params.setProjectName( () -> projectName );
        }
        if ( this.branch != null )
        {
            final Branch branch = Branch.from( this.branch );
            params.setBranch( () -> branch );
        }
        if ( this.queryParams != null )
        {
            params.setQueryParams( this.queryParams );
        }

        return UrlPartsMapper.of( urlServiceSupplier.get().imageUrlParts( params.build() ) );
    }
}
