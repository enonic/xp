package com.enonic.xp.form.inputtype;


public final class DateTimeConfig
    implements InputTypeConfig
{
    private final boolean withTimezone;

    DateTimeConfig( final Builder builder )
    {
        this.withTimezone = builder.withTimezone;
    }

    public boolean isWithTimezone()
    {
        return withTimezone;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private boolean withTimezone = false;

        Builder()
        {
            // protection
        }

        public Builder withTimezone( final boolean value )
        {
            withTimezone = value;
            return this;
        }

        public DateTimeConfig build()
        {
            return new DateTimeConfig( this );
        }
    }
}
