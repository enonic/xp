package com.enonic.wem.portal.url;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.portal.PortalContext;

public final class PortalUrlBuilders
{
    private final PortalContext context;

    public PortalUrlBuilders( final PortalContext context )
    {
        this.context = context;
    }

    // TODO: Hack!
    public String getBaseUrl()
    {
        return this.context.getRequest().getBaseUri();
    }

    private String getMode()
    {
        return this.context.getRequest().getMode().toString();
    }

    private String getWorkspace()
    {
        return this.context.getRequest().getWorkspace().toString();
    }

    private ContentPath getContentPath()
    {
        final Content content = this.context.getContent();
        return content != null ? content.getPath() : null;
    }

    private String getModule()
    {
        final Module module = this.context.getModule();
        return module != null ? module.getKey().toString() : null;
    }

    private <T extends PortalUrlBuilder> T setDefaults( final T builder )
    {
        builder.baseUri( getBaseUrl() );
        builder.mode( getMode() );
        builder.workspace( getWorkspace() );
        builder.contentPath( getContentPath() );
        return builder;
    }

    public GeneralUrlBuilder createUrl( final String path )
    {
        return setDefaults( new GeneralUrlBuilder() ).
            contentPath( path );
    }

    public PublicUrlBuilder createResourceUrl( final String resourcePath )
    {
        return setDefaults( new PublicUrlBuilder() ).
            module( getModule() ).
            resourcePath( resourcePath );
    }

    public ImageUrlBuilder createImageUrl( final String name )
    {
        return setDefaults( new ImageUrlBuilder() ).
            imageName( name );
    }

    public ImageUrlBuilder createImageByIdUrl( final ContentId contentId )
    {
        return setDefaults( new ImageUrlBuilder() ).
            imageId( contentId );
    }

    public ServiceUrlBuilder createServiceUrl( final String name )
    {
        return setDefaults( new ServiceUrlBuilder() ).
            module( getModule() ).
            serviceName( name );
    }
}
