package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.ArchivedContainer;
import com.enonic.xp.archive.ArchivedContainerId;
import com.enonic.xp.archive.ResolveArchivedParams;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

final class ResolveArchivedByContentsCommand
    extends AbstractArchiveCommand
{
    private final ResolveArchivedParams params;

    private ResolveArchivedByContentsCommand( final Builder builder )
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
        final Nodes nodesToRemove =
            nodeService.getByIds( NodeIds.from( params.getContents().stream().map( ContentId::toString ).collect( Collectors.toList() ) ) );

        final List<NodeId> containerIdsToRemove = nodesToRemove.getPaths()
            .stream()
            .map( path -> path.asAbsolute().getElementAsString( 1 ) )
            .distinct()
            .map( NodeId::from )
            .collect( Collectors.toList() );

        return containerIdsToRemove.stream().map( containerId -> {
            final FindNodesByQueryResult result = nodeService.findByQuery( NodeQuery.create()
                                                                               .query( QueryExpr.from(
                                                                                   CompareExpr.like( FieldExpr.from( "_path" ),
                                                                                                   ValueExpr.string( "/" +
                                                                                                                         ArchiveConstants.ARCHIVE_ROOT_NAME +
                                                                                                                         "/" + containerId +
                                                                                                                         "/*" ) ) ) )
                                                                               .withPath( true )
                                                                               .size( -1 )
                                                                               .addOrderBy( FieldOrderExpr.create( "_path",
                                                                                                                   OrderExpr.Direction.ASC ) )
                                                                               .build() );

            return ArchivedContainer.create()
                .id( ArchivedContainerId.from( containerId.toString() ) )
                .addContentIds( result.getNodeIds().getAsStrings().stream().map( ContentId::from ).collect( Collectors.toList() ) )
                .build();
        } ).collect( Collectors.toList() );
    }

    public static class Builder
        extends AbstractArchiveCommand.Builder<Builder>
    {
        private ResolveArchivedParams params;

        private Builder()
        {
        }

        public Builder params( final ResolveArchivedParams params )
        {
            this.params = params;
            return this;
        }

        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params, "Params must be set" );
        }

        public ResolveArchivedByContentsCommand build()
        {
            validate();
            return new ResolveArchivedByContentsCommand( this );
        }
    }
}
