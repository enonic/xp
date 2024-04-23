package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.security.acl.AccessControlList;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@PublicApi
public final class ApplyContentPermissionsParams
{
    private final ContentId contentId;

    private final AccessControlList permissions;

    private final AccessControlList addPermissions;

    private final AccessControlList removePermissions;

    private final ApplyPermissionsScope applyPermissionsScope;

    private final ApplyPermissionsListener listener;

    private ApplyContentPermissionsParams( Builder builder )
    {
        contentId = requireNonNull( builder.contentId );
        applyPermissionsScope = requireNonNullElse( builder.applyPermissionsScope, ApplyPermissionsScope.SINGLE );
        permissions = builder.permissions.build();
        addPermissions = builder.addPermissions.build();
        removePermissions = builder.removePermissions.build();
        listener = builder.listener;

        Preconditions.checkArgument( permissions.isEmpty() || ( addPermissions.isEmpty() && removePermissions.isEmpty() ),
                                     "Permissions cannot be set together with addPermissions or removePermissions" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    @Deprecated
    public boolean isOverwriteChildPermissions()
    {
        return false;
    }

    public ApplyPermissionsScope getScope()
    {
        return applyPermissionsScope;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public AccessControlList getAddPermissions()
    {
        return addPermissions;
    }

    public AccessControlList getRemovePermissions()
    {
        return removePermissions;
    }

    public ApplyPermissionsListener getListener()
    {
        return listener;
    }

    @Deprecated
    public boolean isInheritPermissions()
    {
        return false;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private final AccessControlList.Builder permissions = AccessControlList.create();

        private final AccessControlList.Builder addPermissions = AccessControlList.create();

        private final AccessControlList.Builder removePermissions = AccessControlList.create();

        private ApplyPermissionsScope applyPermissionsScope;

        private ApplyPermissionsListener listener;

        private Builder()
        {
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        @Deprecated
        public Builder overwriteChildPermissions( final boolean overwriteChildPermissions )
        {
            return this;
        }

        public Builder applyPermissionsScope( final ApplyPermissionsScope applyPermissionsScope )
        {
            this.applyPermissionsScope = applyPermissionsScope;
            return this;
        }

        public Builder applyContentPermissionsListener( final ApplyPermissionsListener listener )
        {
            this.listener = listener;
            return this;
        }

        public Builder permissions( final AccessControlList permissions )
        {
            if ( permissions != null )
            {
                this.permissions.addAll( permissions );
            }
            return this;
        }

        public Builder addPermissions( final AccessControlList permissions )
        {
            if ( permissions != null )
            {
                this.addPermissions.addAll( permissions );
            }
            return this;
        }

        public Builder removePermissions( final AccessControlList permissions )
        {
            if ( permissions != null )
            {
                this.removePermissions.addAll( permissions );
            }
            return this;
        }

        @Deprecated
        public Builder inheritPermissions( final boolean inheritPermissions )
        {
            return this;
        }

        public ApplyContentPermissionsParams build()
        {
            return new ApplyContentPermissionsParams( this );
        }
    }

}
