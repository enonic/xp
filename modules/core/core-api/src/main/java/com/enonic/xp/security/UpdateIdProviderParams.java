package com.enonic.xp.security;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.acl.IdProviderAccessControlList;

import static com.google.common.base.Preconditions.checkNotNull;

@PublicApi
public final class UpdateIdProviderParams
{

    private final IdProviderKey idProviderKey;

    private final String displayName;

    private final String description;

    private final IdProviderConfig idProviderConfig;

    private final IdProviderEditor editor;

    private final IdProviderAccessControlList idProviderPermissions;

    private UpdateIdProviderParams( final Builder builder )
    {
        this.idProviderKey = checkNotNull( builder.idProviderKey, "idProviderKey is required" );
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.idProviderConfig = builder.idProviderConfig;
        this.editor = builder.editor;
        this.idProviderPermissions = builder.idProviderPermissions;
    }

    public static Builder create( final IdProvider idProvider )
    {
        return new Builder( idProvider );
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public IdProviderConfig getIdProviderConfig()
    {
        return idProviderConfig;
    }

    public IdProviderKey getKey()
    {
        return idProviderKey;
    }

    public IdProviderEditor getEditor()
    {
        return editor;
    }

    public IdProviderAccessControlList getIdProviderPermissions()
    {
        return idProviderPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IdProvider update( final IdProvider source )
    {
        if ( this.editor != null )
        {
            final EditableIdProvider editableIdProvider = new EditableIdProvider( source );
            editor.edit( editableIdProvider );
            return editableIdProvider.build();
        }

        IdProvider.Builder result = IdProvider.create( source );
        if ( this.displayName != null )
        {
            result.displayName( this.getDisplayName() );
        }

        if ( this.description != null )
        {
            result.description( this.getDescription() );
        }

        if ( this.idProviderConfig != null )
        {
            result.idProviderConfig( this.getIdProviderConfig() );
        }

        return result.build();
    }

    public static class Builder
    {
        private IdProviderKey idProviderKey;

        private String displayName;

        private String description;

        private IdProviderConfig idProviderConfig;

        private IdProviderEditor editor;

        private IdProviderAccessControlList idProviderPermissions;

        private Builder()
        {
        }

        private Builder( final IdProvider idProvider )
        {
            this.idProviderKey = idProvider.getKey();
            this.displayName = idProvider.getDisplayName();
            this.description = idProvider.getDescription();
            this.idProviderConfig = idProvider.getIdProviderConfig();
        }

        public Builder key( final IdProviderKey value )
        {
            this.idProviderKey = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder description( final String value )
        {
            this.description = value;
            return this;
        }

        public Builder idProviderConfig( final IdProviderConfig value )
        {
            this.idProviderConfig = value;
            return this;
        }

        public Builder editor( final IdProviderEditor value )
        {
            this.editor = value;
            return this;
        }

        public Builder permissions( final IdProviderAccessControlList permissions )
        {
            this.idProviderPermissions = permissions;
            return this;
        }

        public UpdateIdProviderParams build()
        {
            return new UpdateIdProviderParams( this );
        }
    }
}
