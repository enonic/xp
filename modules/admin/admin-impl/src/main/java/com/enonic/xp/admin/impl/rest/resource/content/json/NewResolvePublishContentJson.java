package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.content.CompareContentResult;

public class NewResolvePublishContentJson
{
    private final int totalNumber;

    private boolean hasChildren;

    private final List<NewContentPublishItem> requestedContents;

    private final List<NewContentPublishItem> dependentContents;

    private NewResolvePublishContentJson( Builder builder )
    {
        totalNumber = builder.totalNumber;
        hasChildren = builder.hasChildren;

        requestedContents = builder.requestedContents;
        dependentContents = builder.dependentContents;
    }

    public int getTotalNumber()
    {
        return totalNumber;
    }

    public boolean isHasChildren()
    {
        return hasChildren;
    }

    public List<NewContentPublishItem> getRequestedContents()
    {
        return requestedContents;
    }

    public List<NewContentPublishItem> getDependentContents()
    {
        return dependentContents;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private int totalNumber;

        private boolean hasChildren;

        private final List<NewContentPublishItem> requestedContents = Lists.newLinkedList();

        private final List<NewContentPublishItem> dependentContents = Lists.newLinkedList();

        private CompareContentResult compareContentResult;

        private Builder()
        {
        }

        public Builder totalNumber( int totalNumber )
        {
            this.totalNumber = totalNumber;
            return this;
        }

        public Builder hasChildren( boolean hasChildren )
        {
            this.hasChildren = hasChildren;
            return this;
        }

        public Builder addRequested( final NewContentPublishItem requested )
        {
            this.requestedContents.add( requested );
            return this;
        }

        public Builder addDependent( final NewContentPublishItem dependent )
        {
            this.dependentContents.add( dependent );
            return this;
        }

        public NewResolvePublishContentJson build()
        {
            return new NewResolvePublishContentJson( this );
        }
    }
}
