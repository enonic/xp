package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.schema.content.ContentTypeName;

/**
 * Locates the page template a content renders with when it carries no template of its own. The link is never stored on the content, so
 * every caller that needs it - rendering as well as publishing - has to look it up the same way.
 */
public final class DefaultPageTemplateQuery
{
    private DefaultPageTemplateQuery()
    {
    }

    // no order expressions: the default template is the first one in the child order of the templates folder
    public static ContentQuery create( final ContentPath sitePath, final ContentTypeName contentType )
    {
        return ContentQuery.create()
            .parentPath( ContentPath.from( sitePath, ContentServiceImpl.TEMPLATES_FOLDER_NAME ) )
            .addContentTypeName( ContentTypeName.pageTemplate() )
            .queryFilter( ValueFilter.create().fieldName( "data.supports" ).addValues( contentType.toString() ).build() )
            .size( 1 )
            .build();
    }
}
