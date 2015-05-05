package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.ResolvePublishDependenciesResult;

public class ResolvePublishDependenciesResultJson
{
    private final List<DependantContent> dependantsResolvedWithChildrenIncluded = new ArrayList<>();

    private final List<DependantContent> dependantsResolvedWithoutChildrenIncluded = new ArrayList<>();

    private final List<DependantContent> childrenResolved = new ArrayList<>();

    private final List<DependantContent> deletedDependantsResolvedWithChildrenIncluded = new ArrayList<>();

    private final List<DependantContent> deletedDependantsResolvedWithoutChildrenIncluded = new ArrayList<>();

    private final List<DependantContent> deletedChildrenResolved = new ArrayList<>();

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

    public List<DependantContent> getDeletedDependantsResolvedWithChildrenIncluded()
    {
        return deletedDependantsResolvedWithChildrenIncluded;
    }

    public List<DependantContent> getDeletedDependantsResolvedWithoutChildrenIncluded()
    {
        return deletedDependantsResolvedWithoutChildrenIncluded;
    }

    public List<DependantContent> getDeletedChildrenResolved()
    {
        return deletedChildrenResolved;
    }

    public static ResolvePublishDependenciesResultJson from( ResolvePublishDependenciesResult dependantsResult )
    {
        final ResolvePublishDependenciesResultJson json = new ResolvePublishDependenciesResultJson();
        for ( ContentId id : dependantsResult.getDependantsIdsResolvedWithChildrenIncluded() )
        {
            addDependant( json.dependantsResolvedWithChildrenIncluded, id, dependantsResult.getCompareContentResults(),
                          dependantsResult.getResolvedContent() );
        }

        for ( ContentId id : dependantsResult.getDependantsIdsResolvedWithoutChildrenIncluded() )
        {
            addDependant( json.dependantsResolvedWithoutChildrenIncluded, id, dependantsResult.getCompareContentResults(),
                          dependantsResult.getResolvedContent() );
        }

        for ( ContentId id : dependantsResult.getChildrenContentsIds() )
        {
            addDependant( json.childrenResolved, id, dependantsResult.getCompareContentResults(), dependantsResult.getResolvedContent() );
        }

        for ( ContentId id : dependantsResult.getDeletedDependantsIdsResolvedWithChildrenIncluded() )
        {
            addDependant( json.deletedDependantsResolvedWithChildrenIncluded, id, dependantsResult.getCompareContentResults(),
                          dependantsResult.getResolvedContent() );
        }

        for ( ContentId id : dependantsResult.getDeletedDependantsIdsResolvedWithoutChildrenIncluded() )
        {
            addDependant( json.deletedDependantsResolvedWithoutChildrenIncluded, id, dependantsResult.getCompareContentResults(),
                          dependantsResult.getResolvedContent() );
        }

        for ( ContentId id : dependantsResult.getDeletedChildrenContentsIds() )
        {
            addDependant( json.deletedChildrenResolved, id, dependantsResult.getCompareContentResults(),
                          dependantsResult.getResolvedContent() );
        }

        return json;
    }

    private static void addDependant( final List<DependantContent> list, ContentId id, CompareContentResults compareContentResults,
                                      Contents resolvedContents )
    {
        String status = compareContentResults.getCompareContentResultsMap().get( id ).getCompareStatus().name();
        Content resolvedContent = resolvedContents.getContentById( id );
        list.add( new DependantContent( id, resolvedContent.getPath(), status ) );
    }

    public static class DependantContent
    {
        private final String id;

        private final String path;

        private final String compareStatus;

        public DependantContent( final ContentId contentId, final ContentPath path, final String compareStatus )
        {
            this.id = contentId.toString();
            this.path = path.toString();
            this.compareStatus = compareStatus;
        }

        public String getId()
        {
            return id;
        }

        public String getPath()
        {
            return path;
        }

        public String getCompareStatus()
        {
            return compareStatus;
        }
    }

}
