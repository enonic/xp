package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

import com.enonic.xp.admin.impl.json.content.ContentIdJson;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.HasUnpublishedChildrenParams;

public class HasUnpublishedChildrenResultJson
{
    private List<HasUnpublishedChildrenJson> contents;

    public HasUnpublishedChildrenResultJson( final Builder builder) {
        this.contents = builder.contents;
    }

    public List<HasUnpublishedChildrenJson> getContents()
    {
        return contents;
    }

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder
    {

        private List<HasUnpublishedChildrenJson> contents = Lists.newArrayList();


        private Builder()
        {
        }

        public Builder addHasChildren( final ContentId id, final Boolean hasChildren )
        {
            this.contents.add( new HasUnpublishedChildrenJson( id, hasChildren ) ) ;
            return this;
        }

        public HasUnpublishedChildrenResultJson build()
        {
            return new HasUnpublishedChildrenResultJson( this );
        }
    }

    public static class HasUnpublishedChildrenJson
    {
        private ContentIdJson id;

        private Boolean hasChildren;

        public HasUnpublishedChildrenJson( final ContentId id, final Boolean hasChildren) {
            this.id = new ContentIdJson(id);
            this.hasChildren = hasChildren;
        }

        public ContentIdJson getId()
        {
            return id;
        }

        public void setId( final ContentIdJson id )
        {
            this.id = id;
        }

        public Boolean getHasChildren()
        {
            return hasChildren;
        }

        public void setHasChildren( final Boolean hasChildren )
        {
            this.hasChildren = hasChildren;
        }

        @Override
        public boolean equals( final Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( !( o instanceof HasUnpublishedChildrenJson ) )
            {
                return false;
            }

            final HasUnpublishedChildrenJson that = (HasUnpublishedChildrenJson) o;

            if ( !id.equals( that.id ) )
            {
                return false;
            }

            if( !hasChildren.equals( that.hasChildren )) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( id, hasChildren );
        }
    }



}