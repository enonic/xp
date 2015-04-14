package com.enonic.xp.content;

import java.time.Instant;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.PrincipalKey;

@Beta
public class ContentVersion
    implements Comparable<ContentVersion>
{
    private final ContentVersionId id;

    private final PrincipalKey modifier;

    private final String displayName;

    private final Instant modified;

    private final String comment;

    private ContentVersion( Builder builder )
    {
        this.modifier = builder.modifier;
        this.displayName = builder.displayName;
        this.modified = builder.modified;
        this.comment = builder.comment;
        this.id = builder.id;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Instant getModified()
    {
        return modified;
    }

    public String getComment()
    {
        return comment;
    }

    public ContentVersionId getId()
    {
        return id;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public int compareTo( final ContentVersion o )
    {
        if ( this.modified == o.modified )
        {
            return 0;
        }

        if ( this.modified.isBefore( o.modified ) )
        {
            return 1;
        }

        return -1;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentVersion ) )
        {
            return false;
        }

        final ContentVersion that = (ContentVersion) o;

        if ( comment != null ? !comment.equals( that.comment ) : that.comment != null )
        {
            return false;
        }
        if ( displayName != null ? !displayName.equals( that.displayName ) : that.displayName != null )
        {
            return false;
        }
        if ( modified != null ? !modified.equals( that.modified ) : that.modified != null )
        {
            return false;
        }
        if ( modifier != null ? !modifier.equals( that.modifier ) : that.modifier != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = modifier != null ? modifier.hashCode() : 0;
        result = 31 * result + ( displayName != null ? displayName.hashCode() : 0 );
        result = 31 * result + ( modified != null ? modified.hashCode() : 0 );
        result = 31 * result + ( comment != null ? comment.hashCode() : 0 );
        return result;
    }

    public static final class Builder
    {
        private PrincipalKey modifier;

        private String displayName;

        private Instant modified;

        private String comment;

        private ContentVersionId id;

        private Builder()
        {
        }

        public Builder id( final ContentVersionId id )
        {
            this.id = id;
            return this;
        }

        public Builder modifier( PrincipalKey modifier )
        {
            this.modifier = modifier;
            return this;
        }

        public Builder displayName( String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder modified( Instant modified )
        {
            this.modified = modified;
            return this;
        }

        public Builder comment( String comment )
        {
            this.comment = comment;
            return this;
        }

        public ContentVersion build()
        {
            return new ContentVersion( this );
        }
    }
}
