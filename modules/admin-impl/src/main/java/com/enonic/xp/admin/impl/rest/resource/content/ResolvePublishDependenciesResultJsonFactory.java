package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.json.ResolvePublishDependenciesResultJson;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.ResolvePublishDependenciesResult;

public class ResolvePublishDependenciesResultJsonFactory
{

    private final Contents resolvedContents;

    private final CompareContentResults compareContentResults;

    private final ResolvePublishDependenciesResult resolvedPublishDependencies;

    private final ContentIconUrlResolver iconUrlResolver;

    private ResolvePublishDependenciesResultJsonFactory( final Builder builder )
    {

        this.resolvedPublishDependencies = builder.resolvedPublishDependencies;
        this.resolvedContents = builder.resolvedContents;
        this.compareContentResults = builder.compareContentResults;
        this.iconUrlResolver = builder.iconUrlResolver;
    }

    public ResolvePublishDependenciesResultJson createJson()
    {

        final List<ResolvedContent.ResolvedRequestedContent> pushRequestedContents = new ArrayList<>();

        populateResultList( pushRequestedContents );

        sortResults( pushRequestedContents );

        return new ResolvePublishDependenciesResultJson( pushRequestedContents );
    }

    private void populateResultList( final List<ResolvedContent.ResolvedRequestedContent> list )
    {

        for ( final ContentId resolvedPublishContentId : resolvedPublishDependencies.getPushContentRequests().getPushedBecauseRequestedContentIds(
            true ) )
        {
            int children = countTriggeredContents( resolvedPublishContentId,
                                                   resolvedPublishDependencies.getPushContentRequests().getPushedBecauseChildOfContentIds(
                                                       true, true ) );
            int dependants = countTriggeredContents( resolvedPublishContentId,
                                                     resolvedPublishDependencies.getPushContentRequests().getDependantsContentIds( true,
                                                                                                                                   true ) );

            Content resolvedContent = resolvedContents.getContentById( resolvedPublishContentId );

            list.add( ResolvedContent.ResolvedRequestedContent.create().
                childrenCount( children ).
                dependantsCount( dependants ).
                resolvedContent( resolvedContent ).
                compareStatus( fetchStatus( resolvedPublishContentId, compareContentResults ) ).
                iconUrl( iconUrlResolver.resolve( resolvedContent ) ).
                build() );
        }
    }

    /**
     * Count number of contents that were resolved due to given initial contentId.
     *
     * @param initialContentId
     * @param resolvedDependantsOrChildren
     * @return
     */
    private int countTriggeredContents( final ContentId initialContentId, final ContentIds resolvedDependantsOrChildren )
    {
        int result = 0;
        for ( final ContentId contentIdWithReason : resolvedDependantsOrChildren )
        {
            if ( initialContentId.equals(
                resolvedPublishDependencies.getPushContentRequests().findContentIdThatInitiallyTriggeredPublish( contentIdWithReason ) ) )
            {
                result++;
            }
        }
        return result;
    }

    private void sortResults( final List<ResolvedContent.ResolvedRequestedContent> listToSort )
    {
        listToSort.sort( ( o1, o2 ) -> o1.getPath().compareTo( o2.getPath() ) );
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

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private ResolvePublishDependenciesResult resolvedPublishDependencies;

        private ContentIconUrlResolver iconUrlResolver;

        private Contents resolvedContents;

        private CompareContentResults compareContentResults;

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

        public Builder resolvedContents( final Contents resolvedContents )
        {
            this.resolvedContents = resolvedContents;
            return this;
        }

        public Builder compareContentResults( final CompareContentResults compareContentResults )
        {
            this.compareContentResults = compareContentResults;
            return this;
        }

        public ResolvePublishDependenciesResultJsonFactory build()
        {
            return new ResolvePublishDependenciesResultJsonFactory( this );
        }
    }
}
