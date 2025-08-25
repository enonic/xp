package com.enonic.xp.security;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class UpdateGroupParams
{
    private final PrincipalKey key;

    private final String displayName;

    private final GroupEditor editor;

    private final String description;

    private UpdateGroupParams( final Builder builder )
    {
        this.key = Objects.requireNonNull( builder.principalKey, "groupKey is required for a group" );
        this.displayName = builder.displayName;
        this.editor = builder.editor;
        this.description = builder.description;
    }

    public PrincipalKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public GroupEditor getEditor()
    {
        return editor;
    }

    public String getDescription()
    {
        return description;
    }

    public Group update( final Group source )
    {
        if ( this.editor != null )
        {
            final EditableGroup editableGroup = new EditableGroup( source );
            editor.edit( editableGroup );
            return editableGroup.build();
        }

        Group.Builder result = Group.create( source );
        if ( this.displayName != null )
        {
            result.displayName( this.getDisplayName() );
        }

        if ( this.description != null )
        {
            result.description( this.getDescription() );
        }

        return result.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Group group )
    {
        return new Builder( group );
    }

    public static final class Builder
    {
        private PrincipalKey principalKey;

        private String displayName;

        private GroupEditor editor;

        private String description;

        private Builder()
        {
        }

        private Builder( final Group group )
        {
            this.principalKey = group.getKey();
            this.displayName = group.getDisplayName();
            this.description = group.getDescription();
        }

        public Builder groupKey( final PrincipalKey value )
        {
            Preconditions.checkArgument( value.isGroup(), "Invalid PrincipalType for group key: %s", value.getType() );
            this.principalKey = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder editor( final GroupEditor value )
        {
            this.editor = value;
            return this;
        }

        public Builder description( final String value )
        {
            this.description = value;
            return this;
        }

        public UpdateGroupParams build()
        {
            return new UpdateGroupParams( this );
        }
    }
}
