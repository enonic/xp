package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.acl.AccessControlList;

import static java.util.Objects.requireNonNull;

@PublicApi
public final class ApplyContentPermissionsParams
{
    private final ContentId contentId;

    private final AccessControlList permissions;

    private final boolean overwriteChildPermissions;

    private final ApplyPermissionsListener listener;

    private final boolean immediate;

    private ApplyContentPermissionsParams( Builder builder )
    {
        contentId = requireNonNull( builder.contentId );
        overwriteChildPermissions = builder.overwriteChildPermissions;
        permissions = builder.permissions;
        listener = builder.listener;
        immediate = builder.immediate;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public boolean isOverwriteChildPermissions()
    {
        return overwriteChildPermissions;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public ApplyPermissionsListener getListener()
    {
        return listener;
    }

    public boolean isImmediate()
    {
        return immediate;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private AccessControlList permissions;

        private boolean overwriteChildPermissions;

        private ApplyPermissionsListener listener;

        private boolean immediate;

        private Builder()
        {
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder overwriteChildPermissions( final boolean overwriteChildPermissions )
        {
            this.overwriteChildPermissions = overwriteChildPermissions;
            return this;
        }

        public Builder applyContentPermissionsListener( final ApplyPermissionsListener listener )
        {
            this.listener = listener;
            return this;
        }

        public Builder permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder immediate( final boolean immediate )
        {
            this.immediate = immediate;
            return this;
        }

        public ApplyContentPermissionsParams build()
        {
            return new ApplyContentPermissionsParams( this );
        }
    }

}
