package com.enonic.xp.form.inputtype;


public final class DateConfig extends TimezoneConfig
{

    DateConfig( final Builder builder )
    {
        super(builder);
    }

    public static Builder newDateConfig()
    {
        return new Builder();
    }

    public static final class Builder extends TimezoneConfig.Builder
    {
        Builder()
        {
            // protection
        }

        public DateConfig build()
        {
            return new DateConfig( this );
        }
    }
}
