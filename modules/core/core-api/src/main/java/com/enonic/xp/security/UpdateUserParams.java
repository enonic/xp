package com.enonic.xp.security;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.mail.EmailValidator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@PublicApi
public final class UpdateUserParams
{
    private final PrincipalKey key;

    private final String displayName;

    private final String email;

    private final String login;

    private final UserEditor editor;

    private UpdateUserParams( final Builder builder )
    {
        this.key = checkNotNull( builder.principalKey, "userKey is required for a user" );
        this.displayName = builder.displayName;
        if ( builder.email != null )
        {
            checkArgument( EmailValidator.isValid( builder.email ), "Email [" + builder.email + "] is not valid" );
        }
        this.email = builder.email;
        this.login = builder.login;
        this.editor = builder.editor;
    }

    public PrincipalKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getEmail()
    {
        return email;
    }

    public String getLogin()
    {
        return login;
    }

    public UserEditor getEditor()
    {
        return editor;
    }

    public User update( final User source )
    {
        if ( this.editor != null )
        {
            final EditableUser editableUser = new EditableUser( source );
            editor.edit( editableUser );
            return editableUser.build();
        }

        User.Builder result = User.create( source );
        if ( this.displayName != null )
        {
            result.displayName( this.getDisplayName() );
        }
        if ( this.email != null )
        {
            result.email( this.getEmail() );
        }
        if ( this.login != null )
        {
            result.login( this.getLogin() );
        }
        return result.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final User user )
    {
        return new Builder( user );
    }

    public static class Builder
    {
        private PrincipalKey principalKey;

        private String displayName;

        private String email;

        private String login;

        private UserEditor editor;

        private Builder()
        {
        }

        private Builder( final User user )
        {
            this.principalKey = user.getKey();
            this.displayName = user.getDisplayName();
            this.email = user.getEmail();
            this.login = user.getLogin();
        }

        public Builder userKey( final PrincipalKey value )
        {
            Preconditions.checkArgument( value.isUser(), "Invalid PrincipalType for user key: " + value.getType() );
            this.principalKey = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder login( final String value )
        {
            this.login = value;
            return this;
        }

        public Builder email( final String value )
        {
            this.email = value;
            return this;
        }

        public Builder editor( final UserEditor value )
        {
            this.editor = value;
            return this;
        }

        public UpdateUserParams build()
        {
            return new UpdateUserParams( this );
        }
    }
}
