package com.enonic.xp.schema.content;

import java.util.Iterator;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ContentTypeFilter
    implements Iterable<ContentTypeName>
{
    public enum AccessType
    {
        ALLOW( true ), DENY( false );

        private final boolean allowed;

        AccessType( final boolean allowed )
        {
            this.allowed = allowed;
        }

        private boolean isAllowed()
        {
            return allowed;
        }
    }

    private final AccessType defaultAccess;

    private final ImmutableMap<ContentTypeName, AccessType> accessTable;

    public ContentTypeFilter( final Builder builder )
    {
        this.defaultAccess = builder.defaultAccess;
        this.accessTable = builder.accessTable.build();
    }

    public AccessType getDefaultAccess()
    {
        return defaultAccess;
    }

    public boolean isContentTypeAllowed( final ContentTypeName contentType )
    {
        final AccessType accessType = this.accessTable.get( contentType );
        return accessType != null ? accessType.isAllowed() : this.defaultAccess.isAllowed();
    }

    @Override
    public Iterator<ContentTypeName> iterator()
    {
        return this.accessTable.keySet().iterator();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentTypeFilter ) )
        {
            return false;
        }
        final ContentTypeFilter that = (ContentTypeFilter) o;
        return Objects.equals( this.defaultAccess, that.defaultAccess ) && Objects.equals( this.accessTable, that.accessTable );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( defaultAccess, accessTable );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private AccessType defaultAccess;

        private final ImmutableMap.Builder<ContentTypeName, AccessType> accessTable;

        private Builder()
        {
            this.accessTable = ImmutableMap.builder();
            this.defaultAccess = AccessType.DENY;
        }

        public Builder defaultAllow()
        {
            this.defaultAccess = AccessType.ALLOW;
            return this;
        }

        public Builder defaultDeny()
        {
            this.defaultAccess = AccessType.DENY;
            return this;
        }

        public Builder allowContentType( final ContentTypeName contentType )
        {
            accessTable.put( contentType, AccessType.ALLOW );
            return this;
        }

        public Builder allowContentType( final String contentTypeName )
        {
            accessTable.put( ContentTypeName.from( contentTypeName ), AccessType.ALLOW );
            return this;
        }

        public Builder allowContentTypes( final ContentTypeNames contentTypes )
        {
            for ( ContentTypeName contentType : contentTypes )
            {
                accessTable.put( contentType, AccessType.ALLOW );
            }
            return this;
        }

        public Builder denyContentType( final ContentTypeName contentType )
        {
            accessTable.put( contentType, AccessType.DENY );
            return this;
        }

        public Builder denyContentType( final String contentTypeName )
        {
            accessTable.put( ContentTypeName.from( contentTypeName ), AccessType.DENY );
            return this;
        }

        public Builder denyContentTypes( final ContentTypeNames contentTypes )
        {
            for ( ContentTypeName contentType : contentTypes )
            {
                accessTable.put( contentType, AccessType.DENY );
            }
            return this;
        }

        public ContentTypeFilter build()
        {
            return new ContentTypeFilter( this );
        }
    }
}
