package com.enonic.xp.core.impl.content;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.ArchivedContainer;
import com.enonic.xp.archive.ArchivedContainerId;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

final class ListArchivedContentCommand
    extends AbstractArchiveCommand
{
    private static final Pattern ARCHIVED_CONTENT_PATTERN =
        Pattern.compile( "^(?:/" + ArchiveConstants.ARCHIVE_ROOT_NAME + "/)([a-zA-Z0-9_\\-.:]+)/(?:[^/]+)$" );

    private ListArchivedContentCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    List<ArchivedContainer> execute()
    {
        final FindNodesByQueryResult result = nodeService.findByQuery( NodeQuery.
            create().
            query( QueryExpr.from( CompareExpr.
                like( FieldExpr.from( "_parentPath" ), ValueExpr.string( "/" + ArchiveConstants.ARCHIVE_ROOT_NAME + "/*" ) ) ) ).
            withPath( true ).
            size( -1 ).
            build() );

        final Map<String, Set<ContentId>> archived = new HashMap<>();

        for ( final NodeHit hit : result.getNodeHits() )
        {
            final Matcher matcher = ARCHIVED_CONTENT_PATTERN.matcher( hit.getNodePath().toString() );
            if ( matcher.matches() )
            {
                final String containerId = matcher.group( 1 );

                final Set<ContentId> contentsInContainer = archived.computeIfAbsent( containerId, id -> new HashSet() );
                contentsInContainer.add( ContentId.from( hit.getNodeId().toString() ) );
            }
        }

        return archived.entrySet()
            .stream()
            .map( entry -> ArchivedContainer.create()
                .id( ArchivedContainerId.from( entry.getKey() ) )
                .addContentIds( entry.getValue() )
                .build() )
            .collect( Collectors.toList() );
    }

    public static class Builder
        extends AbstractArchiveCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        public ListArchivedContentCommand build()
        {
            return new ListArchivedContentCommand( this );
        }
    }
}
