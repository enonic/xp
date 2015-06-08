package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.json.ResolvePublishRequestedContentsResultJson;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.PushContentRequests;
import com.enonic.xp.content.ResolvePublishDependenciesResult;

public class ResolvePublishRequestedContentsResultJsonFactory
{

    private final Contents resolvedContents;

    private final CompareContentResults compareContentResults;

    private final ResolvePublishDependenciesResult resolvedPublishDependencies;

    private final ContentIconUrlResolver iconUrlResolver;

    private ResolvePublishRequestedContentsResultJsonFactory( final Builder builder )
    {
        this.resolvedPublishDependencies = builder.resolvedPublishDependencies;
        this.resolvedContents = builder.resolvedContents;
        this.compareContentResults = builder.compareContentResults;
        this.iconUrlResolver = builder.iconUrlResolver;
    }

    public ResolvePublishRequestedContentsResultJson createJson()
    {
        final List<ResolvedContent.ResolvedRequestedContent> resolvedRequestedContents = populateResultList();

        sortResults( resolvedRequestedContents );

        return new ResolvePublishRequestedContentsResultJson( resolvedRequestedContents );
    }

    private List<ResolvedContent.ResolvedRequestedContent> populateResultList()
    {
        final List<ResolvedContent.ResolvedRequestedContent> resolvedContentList = new ArrayList<>();

        final PushContentRequests pushContentRequests = resolvedPublishDependencies.getPushContentRequests();

        final ContentIds requestedContentIds = pushContentRequests.getRequestedContentIds( true );

        for ( final ContentId requestedContentId : requestedContentIds )
        {
            int numberOfChildren = getNumberOfChildren( pushContentRequests, requestedContentId );

            int dependants = getNumberOfDependants( pushContentRequests, requestedContentId );

            final Content content = getResolvedContent( requestedContentId );

            resolvedContentList.add( ResolvedContent.ResolvedRequestedContent.create().
                childrenCount( numberOfChildren ).
                dependantsCount( dependants ).
                content( content ).
                compareStatus( getCompareStatus( requestedContentId ) ).
                iconUrl( iconUrlResolver.resolve( content ) ).
                build() );
        }

        return resolvedContentList;
    }

    private Content getResolvedContent( final ContentId contentId )
    {
        final Content content = resolvedContents.getContentById( contentId );

        if ( content == null )
        {
            throw new IllegalArgumentException( "Content was not resolved for id " + contentId );
        }

        return content;
    }

    private int getNumberOfDependants( final PushContentRequests pushContentRequests, final ContentId resolvedPublishContentId )
    {
        final ContentIds dependantsContentIds = pushContentRequests.getDependantsContentIds( true, true );

        return countTriggeredContents( resolvedPublishContentId, dependantsContentIds );
    }

    private int getNumberOfChildren( final PushContentRequests pushContentRequests, final ContentId resolvedPublishContentId )
    {
        final ContentIds pushedBecauseChildOfContentIds = pushContentRequests.getChildrenContentIds( true, true );

        return countTriggeredContents( resolvedPublishContentId, pushedBecauseChildOfContentIds );
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

    private String getCompareStatus( final ContentId id )
    {
        CompareContentResult compareResult = this.compareContentResults.getCompareContentResultsMap().get( id );
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

        public ResolvePublishRequestedContentsResultJsonFactory build()
        {
            return new ResolvePublishRequestedContentsResultJsonFactory( this );
        }
    }
}
