package com.enonic.xp.form.inputtype;

import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.data.Property;
import com.enonic.xp.form.InvalidValueException;

@Beta
public class ComboBoxConfig
    implements InputTypeConfig
{
    private final ImmutableList<Option> optionsAsList;

    private final ImmutableMap<String, Option> optionsAsMap;


    private ComboBoxConfig( Builder builder )
    {
        this.optionsAsList = builder.listBuilder.build();
        this.optionsAsMap = builder.mapBuilder.build();
        Preconditions.checkArgument( this.optionsAsList.size() > 0, "No options given" );
    }

    public List<Option> getOptions()
    {
        return optionsAsList;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueException
    {
        final String valueAsString = property.getString();
        if ( valueAsString != null && !optionsAsMap.containsKey( valueAsString ) )
        {
            throw new InvalidValueException( property,
                                             "Value can only be of one the following strings: " + optionValuesAsCommaSeparatedString() );
        }
    }

    private String optionValuesAsCommaSeparatedString()
    {
        StringBuilder s = new StringBuilder();
        for ( int i = 0; i < optionsAsList.size(); i++ )
        {
            s.append( optionsAsList.get( i ).getValue() );
            if ( i < optionsAsList.size() - 1 )
            {
                s.append( ", " );
            }
        }
        return s.toString();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        implements OptionBuilder
    {
        private ImmutableList.Builder<Option> listBuilder = new ImmutableList.Builder<>();

        private ImmutableMap.Builder<String, Option> mapBuilder = new ImmutableMap.Builder<>();

        Builder()
        {
            // protection
        }

        @Override
        public Builder addOption( String label, String value )
        {
            listBuilder.add( new Option( label, value ) );
            mapBuilder.put( value, new Option( label, value ) );
            return this;
        }

        public ComboBoxConfig build()
        {
            return new ComboBoxConfig( this );
        }
    }
}