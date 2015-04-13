package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.form.InvalidValueException;

public class TimezoneConfig
    implements InputTypeConfig
{
    private final boolean withTimezone;

    TimezoneConfig( final Builder builder )
    {
        this.withTimezone = builder.withTimezone;
    }

    public boolean isWithTimezone()
    {
        return withTimezone;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueException
    {

    }

    public static Builder newTimezoneConfig()
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

        public TimezoneConfig build()
        {
            return new TimezoneConfig( this );
        }
    }
}
