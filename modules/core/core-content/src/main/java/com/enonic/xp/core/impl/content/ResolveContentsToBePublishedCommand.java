package com.enonic.xp.core.impl.content;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.ValueFilter;

public class ResolveContentsToBePublishedCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeChildrenIds;

    private final Branch target;

    private final CompareContentResults.Builder resultBuilder;

    private final boolean includeDependencies;

    private final boolean excludeInvalid;

    private final EnumSet<WorkflowState> excludeWorkflowStates;

    private ResolveContentsToBePublishedCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludedContentIds = builder.excludedContentIds;
        this.target = builder.target;
        this.resultBuilder = CompareContentResults.create();
        this.excludeChildrenIds = builder.excludeChildrenIds;
        this.includeDependencies = builder.includeDependencies;
        this.excludeInvalid = builder.excludeInvalid;
        this.excludeWorkflowStates = builder.excludeWorkflowStates;
    }

    public static Builder create()
    {
        return new Builder();
    }

    CompareContentResults execute()
    {
        resolveDependencies();

        return resultBuilder.build();
    }

    private void resolveDependencies()
    {
        for ( final ContentId contentId : this.contentIds )
        {
            final ResolveSyncWorkResult syncWorkResult = getWorkResult( contentId );

            this.resultBuilder.addAll( CompareResultTranslator.translate( syncWorkResult.getNodeComparisons() ) );
        }
    }

    private ResolveSyncWorkResult getWorkResult( final ContentId contentId )
    {
        final NodeIds nodeIds = excludedContentIds != null ? NodeIds.from( excludedContentIds.
            stream().
            map( id -> NodeId.from( id.toString() ) ).
            collect( Collectors.toList() ) ) : NodeIds.empty();

        final boolean includeChildren = excludeChildrenIds == null || !this.excludeChildrenIds.contains( contentId );

        return nodeService.resolveSyncWork( SyncWorkResolverParams.create().
            includeChildren( includeChildren ).
            includeDependencies( this.includeDependencies ).
            nodeId( NodeId.from( contentId.toString() ) ).
            excludedNodeIds( nodeIds ).
            excludeFilter( createExcludeFilter() ).
            branch( this.target ).
            statusesToStopDependenciesSearch( Set.of( CompareStatus.EQUAL ) ).
            build() );
    }

    private Filter createExcludeFilter()
    {
        final BooleanFilter.Builder filterBuilder = BooleanFilter.create();

        if ( this.excludeInvalid )
        {
            filterBuilder.mustNot( ValueFilter.create().
                fieldName( "valid" ).
                addValue( ValueFactory.newBoolean( false ) ).
                build() ).
                build();
        }

        if ( !this.excludeWorkflowStates.isEmpty() )
        {
            this.excludeWorkflowStates.stream().map( Enum::name ).forEach( state -> {
                filterBuilder.mustNot( ValueFilter.create().
                    fieldName( "workflow.state" ).
                    addValue( ValueFactory.newString( state ) ).
                    build() );
            } );
        }

        final BooleanFilter filter = filterBuilder.build();

        return !filter.getMustNot().isEmpty() ? filter : null;

    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeChildrenIds;

        private Branch target;

        private boolean includeDependencies = true;

        private boolean excludeInvalid = false;

        private EnumSet<WorkflowState> excludeWorkflowStates = EnumSet.noneOf( WorkflowState.class );

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder excludedContentIds( final ContentIds excludedContentIds )
        {
            this.excludedContentIds = excludedContentIds;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder excludeChildrenIds( final ContentIds excludeChildrenIds )
        {
            this.excludeChildrenIds = excludeChildrenIds;
            return this;
        }

        public Builder includeDependencies( final boolean includeDependencies )
        {
            this.includeDependencies = includeDependencies;
            return this;
        }

        public Builder excludeInvalid( boolean excludeInvalid )
        {
            this.excludeInvalid = excludeInvalid;
            return this;
        }

        public Builder excludeWorkflowStates( EnumSet<WorkflowState> workflowStates )
        {
            this.excludeWorkflowStates = workflowStates;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( contentIds );
        }

        public ResolveContentsToBePublishedCommand build()
        {
            validate();
            return new ResolveContentsToBePublishedCommand( this );
        }

    }
}
