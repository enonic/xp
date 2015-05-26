package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ResolvedContent.ResolvedDependantContent;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.PushedContentIdWithInitialReason;
import com.enonic.xp.content.PushedContentIdsWithInitialReason;
import com.enonic.xp.content.ResolvePublishDependenciesResult;

public class ResolveDependantsResultJson
{
    private final List<ResolvedDependantContent> dependantContents;

    private ResolveDependantsResultJson( final Builder builder )
    {
        dependantContents = builder.dependantContents;
    }

    @SuppressWarnings("unused")
    public List<ResolvedDependantContent> getDependantContents()
    {
        return dependantContents;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private ResolvePublishDependenciesResult resolvedPublishDependencies;

        private List<ResolvedDependantContent> dependantContents = new ArrayList<>();

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

        private void populateResultList( final List<ResolvedDependantContent> list,
                                         final PushedContentIdsWithInitialReason dependantsContentIds )
        {
            for ( PushedContentIdWithInitialReason pushed : dependantsContentIds )
            {
                addPublishResolvedContent( list, pushed, resolvedPublishDependencies.getCompareContentResults(),
                                           resolvedPublishDependencies.getResolvedContent(), iconUrlResolver );
            }
        }

        private void sortResults()
        {
            dependantContents.sort( ( o1, o2 ) -> o1.getPath().compareTo( o2.getPath() ) );
        }

        private void addPublishResolvedContent( final List<ResolvedDependantContent> list, PushedContentIdWithInitialReason pushed,
                                                CompareContentResults compareContentResults, Contents resolvedContents,
                                                final ContentIconUrlResolver iconUrlResolver )
        {
            Content resolvedContent = resolvedContents.getContentById( pushed.getPushedContentId() );
            list.add( ResolvedDependantContent.create().
                resolvedContent( resolvedContent ).
                compareStatus( fetchStatus( pushed.getPushedContentId(), compareContentResults ) ).
                iconUrl( iconUrlResolver.resolve( resolvedContent ) ).
                dependsOnContentId( pushed.getInitialReasonPushedId() ).
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

        public ResolveDependantsResultJson build()
        {
            populateResultList( dependantContents, resolvedPublishDependencies.getDependantsContentIds() );

            sortResults();

            return new ResolveDependantsResultJson( this );
        }
    }
}
