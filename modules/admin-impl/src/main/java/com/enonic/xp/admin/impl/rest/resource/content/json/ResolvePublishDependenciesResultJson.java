package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ResolvedContent.ResolvedRequestedContent;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.PushedContentIdWithInitialReason;
import com.enonic.xp.content.PushedContentIdsWithInitialReason;
import com.enonic.xp.content.ResolvePublishDependenciesResult;

public class ResolvePublishDependenciesResultJson
{
    private final List<ResolvedRequestedContent> pushRequestedContents;

    private ResolvePublishDependenciesResultJson( final Builder builder )
    {
        pushRequestedContents = builder.pushRequestedContents;
    }

    @SuppressWarnings("unused")
    public List<ResolvedRequestedContent> getPushRequestedContents()
    {
        return pushRequestedContents;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private ResolvePublishDependenciesResult resolvedPublishDependencies;

        private List<ResolvedRequestedContent> pushRequestedContents = new ArrayList<>();

        private ContentIconUrlResolver iconUrlResolver;

        private Builder()
        {
        }

        public Builder resolvedPublishDependencies( final ResolvePublishDependenciesResult resolvedPublishDependencies )
        {
            this.resolvedPublishDependencies = resolvedPublishDependencies;
            return this;
        }

        public Builder iconUrlResolver( final ContentIconUrlResolver iconUrlResolver )
        {
            this.iconUrlResolver = iconUrlResolver;
            return this;
        }

        private void populateResultList( final List<ResolvedRequestedContent> list,
                                         final ResolvePublishDependenciesResult resolvedPublishDependencies )
        {
            for ( PushedContentIdWithInitialReason pushed : resolvedPublishDependencies.getPushRequestedIds() )
            {
                int children = countReasonedContents( pushed.getPushedContentId(), resolvedPublishDependencies.getChildrenContentsIds() );
                int dependants =
                    countReasonedContents( pushed.getPushedContentId(), resolvedPublishDependencies.getDependantsContentIds() );
                addPublishResolvedContent( list, pushed, resolvedPublishDependencies.getCompareContentResults(),
                                           resolvedPublishDependencies.getResolvedContent(), iconUrlResolver, children, dependants );
            }
        }

        /**
         * Returns number of items of passed set which reason is equal to passed contentId.
         *
         * @param pushedContentId
         * @param reasoned
         * @return
         */
        private int countReasonedContents( final ContentId pushedContentId, final PushedContentIdsWithInitialReason reasoned )
        {
            int result = 0;
            for ( PushedContentIdWithInitialReason contentIdWithReason : reasoned )
            {
                if ( pushedContentId.equals( contentIdWithReason.getInitialReasonPushedId() ) )
                {
                    result++;
                }
            }
            return result;
        }

        private void sortResults()
        {
            pushRequestedContents.sort( ( o1, o2 ) -> o1.getPath().compareTo( o2.getPath() ) );
        }

        private void addPublishResolvedContent( final List<ResolvedRequestedContent> list, PushedContentIdWithInitialReason pushed,
                                                CompareContentResults compareContentResults, Contents resolvedContents,
                                                final ContentIconUrlResolver iconUrlResolver, int children, int dependants )
        {
            Content resolvedContent = resolvedContents.getContentById( pushed.getPushedContentId() );
            list.add( ResolvedRequestedContent.create().
                childrenCount( children ).
                dependantsCount( dependants ).
                resolvedContent( resolvedContent ).
                compareStatus( fetchStatus( pushed.getPushedContentId(), compareContentResults ) ).
                iconUrl( iconUrlResolver.resolve( resolvedContent ) ).
                build() );
        }

        private String fetchStatus( ContentId id, CompareContentResults compareContentResults )
        {
            CompareContentResult compareResult = compareContentResults.getCompareContentResultsMap().get( id );
            if ( compareResult != null )
            {
                return compareResult.getCompareStatus().name();
            }
            return null;
        }

        public ResolvePublishDependenciesResultJson build()
        {
            populateResultList( pushRequestedContents, resolvedPublishDependencies );

            sortResults();

            return new ResolvePublishDependenciesResultJson( this );
        }
    }
}