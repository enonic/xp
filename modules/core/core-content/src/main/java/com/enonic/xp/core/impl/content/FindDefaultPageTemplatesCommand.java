package com.enonic.xp.core.impl.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.schema.content.ContentTypeName;

import static java.util.Objects.requireNonNull;

/**
 * Finds the page templates that the given contents render with without pointing at them. A content that has no page of its own falls back
 * to the default template of its site, resolved by content type, and that fallback is never written to the content - which leaves it
 * invisible to reference based dependency resolution. Publishing such a content without its template renders a 404, so the template has to
 * be looked up here the same way the renderer looks it up.
 */
final class FindDefaultPageTemplatesCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final Map<ContentPath, ContentPath> sitePaths = new HashMap<>();

    private final Map<TemplateLookup, ContentId> templates = new HashMap<>();

    private FindDefaultPageTemplatesCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    ContentIds execute()
    {
        return getContents().stream()
            .filter( FindDefaultPageTemplatesCommand::rendersWithDefaultPageTemplate )
            .map( this::findDefaultPageTemplate )
            .filter( Objects::nonNull )
            .collect( ContentIds.collector() );
    }

    private Contents getContents()
    {
        return GetContentByIdsCommand.create( GetContentByIdsParams.create().contentIds( this.contentIds ).build() )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
    }

    /**
     * Mirrors the fallback in {@code PageResolver}: a content that brings its own page, and one that is never rendered through a template
     * to begin with, resolves without the default template.
     */
    private static boolean rendersWithDefaultPageTemplate( final Content content )
    {
        final ContentTypeName type = content.getType();
        return content.getPage() == null && !type.isPageTemplate() && !type.isTemplateFolder() && !type.isFragment() &&
            !type.isShortcut();
    }

    private ContentId findDefaultPageTemplate( final Content content )
    {
        final ContentPath sitePath = resolveSitePath( content );
        if ( sitePath == null )
        {
            return null;
        }

        // contents of one type within one site all share a default template, and a publish tends to hold many of them
        final TemplateLookup lookup = new TemplateLookup( sitePath, content.getType() );
        if ( templates.containsKey( lookup ) )
        {
            return templates.get( lookup );
        }

        final ContentId templateId = queryDefaultPageTemplate( lookup );
        templates.put( lookup, templateId );
        return templateId;
    }

    private ContentId queryDefaultPageTemplate( final TemplateLookup lookup )
    {
        return FindContentIdsByQueryCommand.create()
            .query( DefaultPageTemplateQuery.create( lookup.sitePath(), lookup.contentType() ) )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute()
            .getContentIds()
            .first();
    }

    /**
     * Path of the site the content belongs to, or {@code null} when it is not part of one. Every ancestor walked past on the way up shares
     * the answer, so each is remembered to keep a publish spanning one site down to a single walk.
     */
    private ContentPath resolveSitePath( final Content content )
    {
        if ( content.isSite() )
        {
            return content.getPath();
        }

        final List<ContentPath> walked = new ArrayList<>();
        ContentPath sitePath = null;
        ContentPath path = content.getParentPath();

        while ( path != null && !path.isRoot() )
        {
            if ( sitePaths.containsKey( path ) )
            {
                sitePath = sitePaths.get( path );
                break;
            }

            walked.add( path );

            final Content ancestor = GetContentByPathCommand.create( path, this ).build().execute();
            if ( ancestor == null )
            {
                break;
            }
            if ( ancestor.isSite() )
            {
                sitePath = ancestor.getPath();
                break;
            }

            path = path.getParentPath();
        }

        final ContentPath resolved = sitePath;
        walked.forEach( walkedPath -> sitePaths.put( walkedPath, resolved ) );

        return resolved;
    }

    private record TemplateLookup(ContentPath sitePath, ContentTypeName contentType)
    {
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( contentIds, "contentIds is required" );
        }

        public FindDefaultPageTemplatesCommand build()
        {
            validate();
            return new FindDefaultPageTemplatesCommand( this );
        }
    }
}
