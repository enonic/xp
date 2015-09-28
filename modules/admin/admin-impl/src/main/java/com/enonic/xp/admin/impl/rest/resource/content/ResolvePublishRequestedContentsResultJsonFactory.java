package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.json.ResolvePublishRequestedContentsResultJson;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.ResolvePublishDependenciesResult;

public class ResolvePublishRequestedContentsResultJsonFactory
{
    private final Contents resolvedContents;

    private final CompareContentResults compareContentResults;

    private final ContentIconUrlResolver iconUrlResolver;

    private ResolvePublishRequestedContentsResultJsonFactory( final Builder builder )
    {
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

        for ( final Content requestedContent : resolvedContents )
        {
            int numberOfChildren = this.resolvedContents.getSize();

            int dependants = this.resolvedContents.getSize();

            // final Content content = getResolvedContent( requestedContentId );
/*
            resolvedContentList.add( ResolvedContent.ResolvedRequestedContent.create().
                childrenCount( numberOfChildren ).
                dependantsCount( dependants ).
                content( content ).
                compareStatus( getCompareStatus( requestedContentId ) ).
                iconUrl( iconUrlResolver.resolve( content ) ).
                build() );
  */
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
