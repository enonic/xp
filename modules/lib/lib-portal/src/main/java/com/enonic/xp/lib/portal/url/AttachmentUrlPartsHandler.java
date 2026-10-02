package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.url.AttachmentUrlPartsParams;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.serializer.MapSerializable;

/**
 * Backs {@code attachmentUrlParts} of {@code /lib/xp/portal}.
 */
public final class AttachmentUrlPartsHandler
    implements ScriptBean
{
    private Supplier<PortalUrlGeneratorService> urlGeneratorServiceSupplier;

    private String id;

    private String path;

    private String projectName;

    private String branch;

    private String name;

    private String label;

    private boolean download;

    private Map<String, List<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlGeneratorServiceSupplier = context.getService( PortalUrlGeneratorService.class );
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
        this.download = Boolean.TRUE.equals( download );
    }

    public void setQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public MapSerializable createParts()
    {
        final AttachmentUrlPartsParams.Builder params = AttachmentUrlPartsParams.create()
            .setId( this.id )
            .setPath( this.path )
            .setName( this.name )
            .setLabel( this.label )
            .setDownload( this.download );

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

        return UrlPartsMapper.of( urlGeneratorServiceSupplier.get().attachmentUrlParts( params.build() ) );
    }
}
