package com.enonic.xp.content;

import java.util.Objects;

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

    private ApplyContentPermissionsParams( Builder builder )
    {
        contentId = requireNonNull( builder.contentId );
        overwriteChildPermissions = builder.overwriteChildPermissions;
        permissions = builder.permissions;
        listener = builder.listener;
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ApplyContentPermissionsParams ) )
        {
            return false;
        }
        final ApplyContentPermissionsParams that = (ApplyContentPermissionsParams) o;
        return Objects.equals( this.contentId, that.contentId ) && this.overwriteChildPermissions == that.overwriteChildPermissions &&
            Objects.equals( this.permissions, that.permissions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.contentId, this.permissions, this.overwriteChildPermissions );
    }

    public static final class Builder
    {
        private ContentId contentId;

        private AccessControlList permissions;

        private boolean overwriteChildPermissions;

        private ApplyPermissionsListener listener;

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

        public ApplyContentPermissionsParams build()
        {
            return new ApplyContentPermissionsParams( this );
        }
    }

}
