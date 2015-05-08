package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.PushedContentIdWithReason;
import com.enonic.xp.content.ResolvePublishDependenciesResult;

public class ResolvePublishDependenciesResultJson
{
    private final List<DependantContent> dependantsResolvedWithChildrenIncluded = new ArrayList<>();

    private final List<DependantContent> dependantsResolvedWithoutChildrenIncluded = new ArrayList<>();

    private final List<DependantContent> childrenResolved = new ArrayList<>();

    private final List<DependantContent> pushRequestedContents = new ArrayList<>();

    public List<DependantContent> getDependantsResolvedWithChildrenIncluded()
    {
        return dependantsResolvedWithChildrenIncluded;
    }

    public List<DependantContent> getDependantsResolvedWithoutChildrenIncluded()
    {
        return dependantsResolvedWithoutChildrenIncluded;
    }

    public List<DependantContent> getChildrenResolved()
    {
        return childrenResolved;
    }

    public List<DependantContent> getPushRequestedContents()
    {
        return pushRequestedContents;
    }

    public static ResolvePublishDependenciesResultJson from( ResolvePublishDependenciesResult dependantsResult,
                                                             final ContentIconUrlResolver iconUrlResolver )
    {
        final ResolvePublishDependenciesResultJson json = new ResolvePublishDependenciesResultJson();
        for ( PushedContentIdWithReason pushed : dependantsResult.getDependantsIdsResolvedWithChildrenIncluded() )
        {
            addDependant( json.dependantsResolvedWithChildrenIncluded, pushed, dependantsResult.getCompareContentResults(),
                          dependantsResult.getResolvedContent(), iconUrlResolver );
        }

        for ( PushedContentIdWithReason pushed : dependantsResult.getDependantsIdsResolvedWithoutChildrenIncluded() )
        {
            addDependant( json.dependantsResolvedWithoutChildrenIncluded, pushed, dependantsResult.getCompareContentResults(),
                          dependantsResult.getResolvedContent(), iconUrlResolver );
        }

        for ( PushedContentIdWithReason pushed : dependantsResult.getChildrenContentsIds() )
        {
            addDependant( json.childrenResolved, pushed, dependantsResult.getCompareContentResults(), dependantsResult.getResolvedContent(),
                          iconUrlResolver );
        }

        for ( PushedContentIdWithReason pushed : dependantsResult.getPushRequestedIds() )
        {
            addDependant( json.pushRequestedContents, pushed, dependantsResult.getCompareContentResults(),
                          dependantsResult.getResolvedContent(), iconUrlResolver );
        }

        sortResult( json );

        return json;
    }

    private static void sortResult( ResolvePublishDependenciesResultJson json )
    {
        json.dependantsResolvedWithChildrenIncluded.sort( ( o1, o2 ) -> o1.getPath().compareTo( o2.getPath() ) );
        json.dependantsResolvedWithoutChildrenIncluded.sort( ( o1, o2 ) -> o1.getPath().compareTo( o2.getPath() ) );
        json.pushRequestedContents.sort( ( o1, o2 ) -> o1.getPath().compareTo( o2.getPath() ) );
    }

    private static void addDependant( final List<DependantContent> list, PushedContentIdWithReason pushed,
                                      CompareContentResults compareContentResults, Contents resolvedContents,
                                      final ContentIconUrlResolver iconUrlResolver )
    {
        String status = compareContentResults.getCompareContentResultsMap().get( pushed.getPushedContentId() ).getCompareStatus().name();
        Content resolvedContent = resolvedContents.getContentById( pushed.getPushedContentId() );
        list.add( new DependantContent( resolvedContent, status, iconUrlResolver, pushed ) );
    }

    public static class DependantContent
    {
        private final String id;

        private final String reasonId;

        private final String path;

        private final String iconUrl;

        private final String displayName;

        private final String compareStatus;

        private final String name;

        private final String type;

        private final boolean isValid;

        public DependantContent( final Content resolvedContent, final String compareStatus, final ContentIconUrlResolver iconUrlResolver,
                                 PushedContentIdWithReason reason )
        {
            this.id = resolvedContent.getId().toString();
            this.path = resolvedContent.getPath().toString();
            this.compareStatus = compareStatus;
            this.displayName = resolvedContent.getDisplayName();
            this.iconUrl = iconUrlResolver.resolve( resolvedContent );
            this.name = resolvedContent.getName().toString();
            this.type = resolvedContent.getType().toString();
            this.isValid = resolvedContent.isValid();
            this.reasonId = reason.getReasonPushedId() == null ? null : reason.getReasonPushedId().toString();
        }

        public String getId()
        {
            return id;
        }

        public String getReasonId()
        {
            return reasonId;
        }

        public String getPath()
        {
            return path;
        }

        public String getCompareStatus()
        {
            return compareStatus;
        }

        public String getIconUrl()
        {
            return iconUrl;
        }

        public String getDisplayName()
        {
            return displayName;
        }

        public String getName()
        {
            return name;
        }

        public String getType()
        {
            return type;
        }

        public boolean isValid()
        {
            return isValid;
        }
    }

}
