package com.enonic.xp.form.inputtype;


public final class DateTimeConfig
    extends TimezoneConfig
{

    DateTimeConfig( final Builder builder )
    {
        super(builder);
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder extends TimezoneConfig.Builder
    {
        Builder()
        {
            // protection
        }

        public DateTimeConfig build()
        {
            return new DateTimeConfig( this );
        }
    }
}
