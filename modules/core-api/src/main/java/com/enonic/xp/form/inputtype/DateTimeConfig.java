package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.form.InvalidValueException;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

public final class DateTimeConfig
    implements InputTypeConfig
{
    private final Boolean withTimezone;

    DateTimeConfig( final Builder builder )
    {
        this.withTimezone = builder.withTimezone != null ? builder.withTimezone : false;
    }

    public Boolean isWithTimezone()
    {
        return withTimezone;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueException
    {

    }

    public static Builder newDateTimeConfig()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Boolean withTimezone;

        Builder()
        {
            // protection
        }

        public Builder withTimezone( final Boolean value )
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
