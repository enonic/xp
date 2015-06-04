package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.json.ResolveDependantsResultJson;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.ResolvePublishDependenciesResult;

public class ResolveDependantsResultJsonFactory
{

    private final ResolvePublishDependenciesResult resolvedPublishDependencies;

    private final ContentIconUrlResolver iconUrlResolver;

    private final CompareContentResults compareContentResults;

    private final Contents resolvedContents;

    private ResolveDependantsResultJsonFactory( final Builder builder )
    {

        this.resolvedPublishDependencies = builder.resolvedPublishDependencies;
        this.iconUrlResolver = builder.iconUrlResolver;
        this.resolvedContents = builder.resolvedContents;
        this.compareContentResults = builder.compareContentResults;
    }

    public ResolveDependantsResultJson createJson()
    {

        final List<ResolvedContent> dependantContents = new ArrayList<>();

        populateResultList( dependantContents, resolvedPublishDependencies.getPushContentRequests().getDependantsContentIds( true, true ) );

        sortResults( dependantContents );

        return new ResolveDependantsResultJson( dependantContents );
    }

    private void populateResultList( final List<ResolvedContent> list, final ContentIds dependantsContentIds )
    {
        for ( final ContentId dependantContentId : dependantsContentIds )
        {
            Content resolvedContent = resolvedContents.getContentById( dependantContentId );

            list.add( ResolvedContent.create().
                content( resolvedContent ).
                compareStatus( fetchStatus( dependantContentId, compareContentResults ) ).
                iconUrl( iconUrlResolver.resolve( resolvedContent ) ).
                build() );
        }
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

        public ResolveDependantsResultJsonFactory build()
        {

            return new ResolveDependantsResultJsonFactory( this );
        }
    }
}
