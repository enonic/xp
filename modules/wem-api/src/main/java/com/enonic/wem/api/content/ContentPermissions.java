package com.enonic.wem.api.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.security.acl.AccessControlList;

public final class ContentPermissions
{
    private final AccessControlList contentPermissions;

    private final AccessControlList parentContentPermissions;

    private ContentPermissions( final Builder builder )
    {
        this.contentPermissions = Preconditions.checkNotNull( builder.contentPermissions );
        this.parentContentPermissions = Preconditions.checkNotNull( builder.parentContentPermissions );
    }

    public AccessControlList getPermissions()
    {
        return contentPermissions;
    }

    public AccessControlList getInheritedPermissions()
    {
        return parentContentPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private AccessControlList contentPermissions;

        private AccessControlList parentContentPermissions;

        private Builder()
        {
        }

        public Builder permissions( final AccessControlList value )
        {
            this.contentPermissions = value;
            return this;
        }

        public Builder inheritedPermissions( final AccessControlList value )
        {
            this.parentContentPermissions = value;
            return this;
        }

        public ContentPermissions build()
        {
            return new ContentPermissions( this );
        }
    }

}
