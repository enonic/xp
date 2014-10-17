package com.enonic.wem.api.identity;

public abstract class Account
    extends Identity
{
    private final String login;

    protected Account( final Builder builder )
    {
        super( builder.identity );
        this.login = builder.login;
    }

    public String getLogin()
    {
        return login;
    }

    public static class Builder
    {
        protected Identity.Builder identity;

        private String login;

        protected Builder()
        {
            identity = new Identity.Builder();

        }

        protected Builder( final Account account )
        {
            identity = new Identity.Builder( account );
            this.login = account.login;
        }

        public Builder login( final String value )
        {
            this.login = value;
            return this;
        }

    }
}
