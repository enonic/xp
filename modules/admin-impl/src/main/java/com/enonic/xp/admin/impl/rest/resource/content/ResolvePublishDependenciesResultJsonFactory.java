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
import com.enonic.xp.content.PushContentRequests;
import com.enonic.xp.content.ResolvePublishDependenciesResult;

public class ResolvePublishDependenciesResultJsonFactory
{

    private final ResolvePublishDependenciesResult resolvedPublishDependencies;

    private final ContentIconUrlResolver iconUrlResolver;

    private final CompareContentResults compareContentResults;

    private final Contents resolvedContents;

    private ResolvePublishDependenciesResultJsonFactory( final Builder builder )
    {

        this.resolvedPublishDependencies = builder.resolvedPublishDependencies;
        this.iconUrlResolver = builder.iconUrlResolver;
        this.resolvedContents = builder.resolvedContents;
        this.compareContentResults = builder.compareContentResults;
    }

    public ResolvePublishDependenciesResultJson createJson()
    {

        final PushContentRequests pushContentRequests = resolvedPublishDependencies.getPushContentRequests();

        final ContentIds dependantsContentIds = pushContentRequests.getDependantsContentIds( true, true );

        final ContentIds childrenContentIds = pushContentRequests.getChildrenContentIds( true, true );

        final List<ResolvedContent> dependantContents = populateResultList( dependantsContentIds );

        final List<ResolvedContent> childrenContents = populateResultList( childrenContentIds );

        sortResults( dependantContents );

        sortResults( childrenContents );

        return new ResolvePublishDependenciesResultJson( dependantContents, childrenContents );
    }

    private List<ResolvedContent> populateResultList( final ContentIds contentIds )
    {

        final List<ResolvedContent> resolvedContentList = new ArrayList<>();

        for ( final ContentId dependantContentId : contentIds )
        {
            Content resolvedContent = getResolvedContent( dependantContentId );

            resolvedContentList.add( ResolvedContent.create().
                content( resolvedContent ).
                compareStatus( fetchStatus( dependantContentId, compareContentResults ) ).
                iconUrl( iconUrlResolver.resolve( resolvedContent ) ).
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

    private void sortResults( final List<ResolvedContent> dependantContents )
    {
        dependantContents.sort( ( o1, o2 ) -> o1.getPath().compareTo( o2.getPath() ) );
    }

    private String fetchStatus( final ContentId id, final CompareContentResults compareContentResults )
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
