package com.enonic.xp.portal.impl.url;

import java.net.URI;
import java.util.function.Function;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.impl.handler.PathMatchers;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectService;

final class ContentBaseUrlResolver
{
    private final ContentService contentService;

    private final ProjectService projectService;

    private final BaseUrlParams params;

    ContentBaseUrlResolver( final ContentService contentService, final ProjectService projectService, final BaseUrlParams params )
    {
        this.contentService = contentService;
        this.projectService = projectService;
        this.params = params;
    }

    public String resolve( final Function<BaseUrlMetadata, String> pathResolver )
    {
        final BaseUrlMetadata baseUrlMetadata = new BaseUrlExtractor( contentService, projectService ).extract( params );

        final String baseUrl = resolveBaseUrl( baseUrlMetadata, params.getUrlType() );

        final StringBuilder result = new StringBuilder( baseUrl );

        final String path = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( baseUrlMetadata.getProjectName().getRepoId() )
            .branch( baseUrlMetadata.getBranch() )
            .build()
            .callWith( () -> pathResolver.apply( baseUrlMetadata ) );

        UrlBuilderHelper.appendAndEncodePathParts( result, path );

        return result.toString();
    }

    private String resolveBaseUrl( final BaseUrlMetadata baseUrlMetadata, final String urlType )
    {
        final String resolvedBaseUrl = baseUrlMetadata.getBaseUrl();

        String result;
        if ( resolvedBaseUrl == null )
        {
            result = PathMatchers.SITE_PREFIX + baseUrlMetadata.getProjectName() + "/" + baseUrlMetadata.getBranch();
        }
        else
        {
            final String normalizedBaseUrl =
                UrlTypeConstants.SERVER_RELATIVE.equals( urlType ) ? URI.create( resolvedBaseUrl ).getPath() : resolvedBaseUrl;

            result =
                normalizedBaseUrl.endsWith( "/" ) ? normalizedBaseUrl.substring( 0, normalizedBaseUrl.length() - 1 ) : normalizedBaseUrl;
        }

        return result;
    }
}
