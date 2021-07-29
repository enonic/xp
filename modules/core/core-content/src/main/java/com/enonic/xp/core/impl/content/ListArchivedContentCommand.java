package com.enonic.xp.core.impl.content;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.ArchivedContainer;
import com.enonic.xp.archive.ArchivedContainerId;
import com.enonic.xp.archive.ListContentsParams;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
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

    private final ListContentsParams params;

    private ListArchivedContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    List<ArchivedContainer> execute()
    {
        final Map<String, Set<ContentId>> archived =
            params.getParent() != null ? fetchContainer( params.getParent() ) : fetchAllContainers();

        return archived.entrySet().stream().map( entry -> {
            final Node container = nodeService.getById( NodeId.from( entry.getKey() ) );

            return ArchivedContainer.create()
                .id( ArchivedContainerId.from( entry.getKey() ) )
                .addContentIds( entry.getValue() )
                .archiveTime( container.getTimestamp() )
                .parent( params.getParent() != null ? params.getParent() : null )
                .build();
        } ).collect( Collectors.toList() );
    }

    private Map<String, Set<ContentId>> fetchAllContainers()
    {
        final Map<String, Set<ContentId>> archived = new HashMap<>();

        final FindNodesByQueryResult result = nodeService.findByQuery( NodeQuery.create()
                                                                           .query( QueryExpr.from(
                                                                               CompareExpr.like( FieldExpr.from( "_parentPath" ),
                                                                                                 ValueExpr.string( "/" +
                                                                                                                       ArchiveConstants.ARCHIVE_ROOT_NAME +
                                                                                                                       "/*" ) ) ) )
                                                                           .withPath( true )
                                                                           .size( -1 )
                                                                           .build() );

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
        return archived;
    }

    private Map<String, Set<ContentId>> fetchContainer( final ContentId parent )
    {
        final Map<String, Set<ContentId>> archived = new HashMap<>();

        final String parentPath = nodeService.getById( NodeId.from( parent ) ).path().asRelative().toString();

        final FindNodesByQueryResult result = nodeService.findByQuery( NodeQuery.create()
                                                                           .query( QueryExpr.from(
                                                                               CompareExpr.eq( FieldExpr.from( "_parentPath" ),
                                                                                               ValueExpr.string( "/" + parentPath ) ) ) )
                                                                           .withPath( true )
                                                                           .size( -1 )
                                                                           .build() );

        for ( final NodeHit hit : result.getNodeHits() )
        {
            final String containerId = hit.getNodePath().getElementAsString( 1 );

            final Set<ContentId> contentsInContainer = archived.computeIfAbsent( containerId, id -> new HashSet() );
            contentsInContainer.add( ContentId.from( hit.getNodeId().toString() ) );
        }

        return archived;
    }

    public static class Builder
        extends AbstractArchiveCommand.Builder<Builder>
    {
        private ListContentsParams params;

        private Builder()
        {
        }

        public Builder params( final ListContentsParams params )
        {
            this.params = params;
            return this;
        }

        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params, "Params must be set" );
        }

        public ListArchivedContentCommand build()
        {
            validate();
            return new ListArchivedContentCommand( this );
        }
    }
}
