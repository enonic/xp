package com.enonic.xp.portal.impl.url;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.UrlGeneratorParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(immediate = true)
public class PortalUrlGeneratorServiceImpl
    implements PortalUrlGeneratorService
{
    private static final DescriptorKey MEDIA_IMAGE_API_DESCRIPTOR_KEY = DescriptorKey.from( ApplicationKey.from( "media" ), "image" );

    private static final DescriptorKey MEDIA_ATTACHMENT_API_DESCRIPTOR_KEY =
        DescriptorKey.from( ApplicationKey.from( "media" ), "attachment" );

    @Override
    public String imageUrl( final ImageUrlGeneratorParams params )
    {
        final ApiUrlGeneratorParams.Builder builder = ApiUrlGeneratorParams.create()
            .setUrlType( params.getUrlType() )
            .setBaseUrl( params.getBaseUrl() )
            .setDescriptorKey( MEDIA_IMAGE_API_DESCRIPTOR_KEY )
            .setPath( ImageMediaPathSupplier.create()
                          .setMedia( params.getMedia() )
                          .setProjectName( params.getProjectName() )
                          .setBranch( params.getBranch() )
                          .setScale( params.getScale() )
                          .setFormat( params.getFormat() )
                          .build() );

        if ( params.getQuality() != null )
        {
            builder.addQueryParam( "quality", params.getQuality().toString() );
        }
        if ( params.getBackground() != null )
        {
            builder.addQueryParam( "background", params.getBackground() );
        }
        if ( params.getFilter() != null )
        {
            builder.addQueryParam( "filter", params.getFilter() );
        }

        builder.addQueryParams( params.getQueryParams() );

        return apiUrl( builder.build() );
    }

    @Override
    public String attachmentUrl( final AttachmentUrlGeneratorParams params )
    {
        final AttachmentMediaPathSupplier pathStrategy = AttachmentMediaPathSupplier.create()
            .setContent( params.getContentSupplier() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setName( params.getName() )
            .setLabel( params.getLabel() )
            .build();

        final ApiUrlGeneratorParams.Builder builder = ApiUrlGeneratorParams.create()
            .setBaseUrl( params.getBaseUrl() )
            .setUrlType( params.getUrlType() )
            .setDescriptorKey( MEDIA_ATTACHMENT_API_DESCRIPTOR_KEY )
            .setPath( pathStrategy )
            .addQueryParams( params.getQueryParams() );

        if ( params.isDownload() )
        {
            builder.addQueryParam( "download", null );
        }

        return apiUrl( builder.build() );
    }

    @Override
    public String apiUrl( final ApiUrlGeneratorParams params )
    {
        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        params.getQueryParams().forEach( queryParamsStrategy::putAll );

        final UrlGeneratorParams generatorParams = UrlGeneratorParams.create()
            .setBaseUrl( ApiUrlBaseUrlResolver.create()
                             .setBaseUrl( params.getBaseUrl() )
                             .setDescriptorKey( params.getDescriptorKey() )
                             .setUrlType( params.getUrlType() )
                             .build() )
            .setPath( params.getPath() )
            .setQueryString( queryParamsStrategy )
            .build();

        return generateUrl( generatorParams );
    }

    @Override
    public String generateUrl( final UrlGeneratorParams params )
    {
        return runWithAdminRole( () -> UrlGenerator.generateUrl( params ) );
    }

    private <T> T runWithAdminRole( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authenticationInfo =
            AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build();
        return ContextBuilder.from( context ).authInfo( authenticationInfo ).build().callWith( callable );
    }
}
