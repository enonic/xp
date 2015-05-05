package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ResolvePublishDependenciesResult;

public class ResolvePublishDependenciesResultJson
{
    private final List<ContentId> dependantsResolvedWithChildrenIncluded = new ArrayList<>();

    private final List<ContentId> dependantsResolvedWithoutChildrenIncluded = new ArrayList<>();

    private int childrenCount;

    public List<ContentId> getDependantsResolvedWithChildrenIncluded()
    {
        return dependantsResolvedWithChildrenIncluded;
    }

    public List<ContentId> getDependantsResolvedWithoutChildrenIncluded()
    {
        return dependantsResolvedWithoutChildrenIncluded;
    }

    public int getChildrenCount()
    {
        return childrenCount;
    }

    public static ResolvePublishDependenciesResultJson from( ResolvePublishDependenciesResult dependantsResult )
    {
        final ResolvePublishDependenciesResultJson json = new ResolvePublishDependenciesResultJson();
        json.dependantsResolvedWithChildrenIncluded.addAll( dependantsResult.getDependantsIdsResolvedWithChildrenIncluded().getSet() );
        json.dependantsResolvedWithoutChildrenIncluded.addAll(
            dependantsResult.getDependantsIdsResolvedWithoutChildrenIncluded().getSet() );
        json.childrenCount = dependantsResult.getChildrenContentsIds().getSize();
        return json;
    }

}
