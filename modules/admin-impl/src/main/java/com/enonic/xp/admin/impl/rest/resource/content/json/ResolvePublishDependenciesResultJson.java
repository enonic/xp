package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
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
    private final List<DependantContent> dependantsResolved;

    private final List<DependantContent> childrenResolved;

    private final List<DependantContent> pushRequestedContents;

    private ResolvePublishDependenciesResultJson( final Builder builder )
    {
        dependantsResolved = builder.dependantsResolved;
        childrenResolved = builder.childrenResolved;
        pushRequestedContents = builder.pushRequestedContents;
    }

    @SuppressWarnings("unused")
    public List<DependantContent> getDependantsResolved()
    {
        return dependantsResolved;
    }

    @SuppressWarnings("unused")
    public List<DependantContent> getChildrenResolved()
    {
        return childrenResolved;
    }

    @SuppressWarnings("unused")
    public List<DependantContent> getPushRequestedContents()
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

        private List<DependantContent> dependantsResolved = new ArrayList<>();

        private List<DependantContent> childrenResolved = new ArrayList<>();

        private List<DependantContent> pushRequestedContents = new ArrayList<>();

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

        private void populateResultList( final List<DependantContent> list, final PushedContentIdsWithInitialReason pushedContentIds )
        {
            for ( PushedContentIdWithInitialReason pushed : pushedContentIds )
            {
                addDependant( list, pushed, resolvedPublishDependencies.getCompareContentResults(),
                              resolvedPublishDependencies.getResolvedContent(), iconUrlResolver );
            }
        }

        private void sortResults()
        {
            dependantsResolved.sort( ( o1, o2 ) -> o1.getPath().compareTo( o2.getPath() ) );
            pushRequestedContents.sort( ( o1, o2 ) -> o1.getPath().compareTo( o2.getPath() ) );
        }

        private void addDependant( final List<DependantContent> list, PushedContentIdWithInitialReason pushed,
                                   CompareContentResults compareContentResults, Contents resolvedContents,
                                   final ContentIconUrlResolver iconUrlResolver )
        {
            Content resolvedContent = resolvedContents.getContentById( pushed.getPushedContentId() );
            list.add( DependantContent.create().resolvedContent( resolvedContent ).
                compareStatus( fetchStatus( pushed.getPushedContentId(), compareContentResults ) ).
                iconUrl( iconUrlResolver.resolve( resolvedContent ) ).
                reason( pushed ).build() );
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
            populateResultList( dependantsResolved, resolvedPublishDependencies.getDependantsContentIds() );
            populateResultList( childrenResolved, resolvedPublishDependencies.getChildrenContentsIds() );
            populateResultList( pushRequestedContents, resolvedPublishDependencies.getPushRequestedIds() );

            sortResults();

            return new ResolvePublishDependenciesResultJson( this );
        }
    }

    private static class DependantContent
    {
        private final String id;

        private final String initialReasonId;

        private final String path;

        private final String iconUrl;

        private final String displayName;

        private final String compareStatus;

        private final String name;

        private final String type;

        private final boolean isValid;

        public DependantContent( Builder builder )
        {
            this.id = builder.id;
            this.path = builder.path;
            this.compareStatus = builder.compareStatus;
            this.displayName = builder.displayName;
            this.iconUrl = builder.iconUrl;
            this.name = builder.name;
            this.type = builder.type;
            this.isValid = builder.isValid;
            this.initialReasonId = builder.initialReasonId;
        }

        public static Builder create()
        {
            return new Builder();
        }

        @SuppressWarnings("unused")
        public String getId()
        {
            return id;
        }

        @SuppressWarnings("unused")
        public String getInitialReasonId()
        {
            return initialReasonId;
        }

        @SuppressWarnings("unused")
        public String getPath()
        {
            return path;
        }

        @SuppressWarnings("unused")
        public String getCompareStatus()
        {
            return compareStatus;
        }

        @SuppressWarnings("unused")
        public String getIconUrl()
        {
            return iconUrl;
        }

        @SuppressWarnings("unused")
        public String getDisplayName()
        {
            return displayName;
        }

        @SuppressWarnings("unused")
        public String getName()
        {
            return name;
        }

        @SuppressWarnings("unused")
        public String getType()
        {
            return type;
        }

        @SuppressWarnings("unused")
        public boolean isValid()
        {
            return isValid;
        }

        public static final class Builder
        {

            private String id;

            private String initialReasonId;

            private String path;

            private String displayName;

            private String name;

            private String type;

            private boolean isValid;

            private Content resolvedContent;

            private String compareStatus;

            private String iconUrl;

            private PushedContentIdWithInitialReason reason;

            public Builder resolvedContent( final Content resolvedContent )
            {
                this.resolvedContent = resolvedContent;
                return this;
            }

            public Builder compareStatus( final String compareStatus )
            {
                this.compareStatus = compareStatus;
                return this;
            }

            public Builder iconUrl( final String iconUrl )
            {
                this.iconUrl = iconUrl;
                return this;
            }

            public Builder reason( final PushedContentIdWithInitialReason reason )
            {
                this.reason = reason;
                return this;
            }

            public DependantContent build()
            {
                this.id = resolvedContent.getId().toString();
                this.path = resolvedContent.getPath().toString();
                this.displayName = resolvedContent.getDisplayName();
                this.name = resolvedContent.getName().toString();
                this.type = resolvedContent.getType().toString();
                this.isValid = resolvedContent.isValid();
                this.initialReasonId = reason.getInitialReasonPushedId() == null ? null : reason.getInitialReasonPushedId().toString();

                return new DependantContent( this );
            }
        }
    }

}
