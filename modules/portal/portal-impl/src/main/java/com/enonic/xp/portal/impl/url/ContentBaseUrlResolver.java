package com.enonic.xp.portal.impl.url;

import java.util.function.Function;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.impl.handler.PathMatchers;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.project.ProjectService;

final class ContentBaseUrlResolver
{
    private final ContentService contentService;

    private final ProjectService projectService;

    private final BaseUrlParams params;

    private final String baseUrl;

    ContentBaseUrlResolver( final ContentService contentService, final ProjectService projectService, final BaseUrlParams params )
    {
        this( contentService, projectService, params, null );
    }

    ContentBaseUrlResolver( final ContentService contentService, final ProjectService projectService, final BaseUrlParams params,
                            final String baseUrl )
    {
        this.contentService = contentService;
        this.projectService = projectService;
        this.params = params;
        this.baseUrl = baseUrl;
    }

    public String resolve( final Function<BaseUrlMetadata, String> pathResolver )
    {
        final BaseUrlMetadata baseUrlMetadata = new BaseUrlExtractor( contentService, projectService ).extract( params, baseUrl );

        final String resolvedBaseUrl = resolveBaseUrl( baseUrlMetadata );

        final StringBuilder result = new StringBuilder( resolvedBaseUrl );

        final String path = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( baseUrlMetadata.getProjectName().getRepoId() )
            .branch( baseUrlMetadata.getBranch() )
            .build()
            .callWith( () -> pathResolver.apply( baseUrlMetadata ) );

        UrlBuilderHelper.appendAndEncodePathParts( result, path );

        return result.toString();
    }

    private String resolveBaseUrl( final BaseUrlMetadata baseUrlMetadata )
    {
        if ( baseUrl != null )
        {
            return baseUrl;
        }

        final String resolvedBaseUrl = baseUrlMetadata.getBaseUrl();

        if ( resolvedBaseUrl == null )
        {
            return PathMatchers.SITE_PREFIX + baseUrlMetadata.getProjectName() + "/" + baseUrlMetadata.getBranch();
        }

        // the configured Base URL is used verbatim and itself determines the URL form:
        // the urlType flag only applies to request-anchored URLs
        return resolvedBaseUrl.endsWith( "/" ) ? resolvedBaseUrl.substring( 0, resolvedBaseUrl.length() - 1 ) : resolvedBaseUrl;
    }
}
