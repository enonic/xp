package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.project.ProjectService;

final class ComponentBaseUrlSupplier
    implements Supplier<String>
{
    private final ContentService contentService;

    private final ProjectService projectService;

    private final ComponentUrlParams params;

    private final Supplier<String> componentPathSupplier;

    ComponentBaseUrlSupplier( final ContentService contentService, final ProjectService projectService, final ComponentUrlParams params,
                              final Supplier<String> componentPathSupplier )
    {
        this.contentService = contentService;
        this.projectService = projectService;
        this.params = params;
        this.componentPathSupplier = componentPathSupplier;
    }

    @Override
    public String get()
    {
        final PageUrlParams pageUrlParams = new PageUrlParams().type( params.getType() )
            .id( params.getId() )
            .path( params.getPath() )
            .projectName( params.getProjectName() )
            .branch( params.getBranch() );

        final StringBuilder result = new StringBuilder();

        result.append( new PageBaseUrlSupplier( contentService, projectService, pageUrlParams ).get() );

        final String componentPath = componentPathSupplier.get();
        if ( componentPath != null )
        {
            UrlBuilderHelper.appendPart( result, "_" );
        }

        return result.toString();
    }
}
