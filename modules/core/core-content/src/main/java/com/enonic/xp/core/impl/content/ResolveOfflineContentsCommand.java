package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.query.parser.QueryParser;

public class ResolveOfflineContentsCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final Branch target;

    private final ContentIds.Builder resultBuilder;

    private final ContentService contentService;

    private ResolveOfflineContentsCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.target = builder.target;
        this.contentService = builder.contentService;
        this.resultBuilder = ContentIds.create();
    }

    public static Builder create()
    {
        return new Builder();
    }

    ContentIds execute()
    {
        final NodeComparisons nodeComparisons = nodeService.compare( NodeIds.from( contentIds.asStrings() ), target );

        final NodeComparisons offline = NodeComparisons.create().addAll( nodeComparisons.getWithStatus( CompareStatus.NEW ) ).build();

        if(offline.getSize() == 0) {
            return ContentIds.empty();
        }

        final ContentQuery query = ContentQuery.create().queryExpr( QueryParser.parse( "publish.first like '*'" ) ).filterContentIds(
            ContentIds.from( offline.getNodeIds().getAsStrings() ) ).build();

        final FindContentIdsByQueryResult result = this.contentService.find( query );

        return result.getContentIds();
    }


    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private Branch target;

        private ContentService contentService;

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( contentIds );
        }

        public ResolveOfflineContentsCommand build()
        {
            validate();
            return new ResolveOfflineContentsCommand( this );
        }

    }
}
