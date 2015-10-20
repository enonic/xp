package com.enonic.xp.content;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;

import static java.util.Objects.requireNonNull;

@Beta
public final class ApplyContentPermissionsParams
{
    private final ContentId contentId;

    private final boolean overwriteChildPermissions;

    private ApplyContentPermissionsParams( Builder builder )
    {
        contentId = requireNonNull( builder.contentId );
        overwriteChildPermissions = builder.overwriteChildPermissions;
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
        return Objects.equal( this.contentId, that.contentId ) &&
            Objects.equal( this.overwriteChildPermissions, that.overwriteChildPermissions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentId, this.overwriteChildPermissions );
    }

    public static final class Builder
    {
        private ContentId contentId;

        private boolean overwriteChildPermissions;

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

        public ApplyContentPermissionsParams build()
        {
            return new ApplyContentPermissionsParams( this );
        }
    }

}
