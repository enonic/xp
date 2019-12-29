package com.enonic.xp.page;

import java.util.function.Predicate;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.content.ContentTypeName;

@PublicApi
public final class PageTemplateFilter
    implements Predicate<PageTemplate>
{
    private final ContentTypeName canRender;

    private PageTemplateFilter( final ContentTypeName canRender )
    {
        this.canRender = canRender;
    }

    @Override
    public boolean test( final PageTemplate pageTemplate )
    {
        return pageTemplate.getCanRender().contains( canRender ) && pageTemplate.getController() != null;
    }

    public static Predicate<PageTemplate> canRender( final ContentTypeName contentTypeName )
    {
        return new PageTemplateFilter( contentTypeName );
    }
}
