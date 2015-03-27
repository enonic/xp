package com.enonic.xp.form.inputtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.Property;
import com.enonic.xp.form.InvalidValueException;

public class SingleSelectorConfig
    implements InputTypeConfig
{
    private SelectorType type;

    private List<Option> optionsAsList = new ArrayList<Option>();

    private HashMap<String, Option> optionsAsMap = new HashMap<String, Option>();

    public List<Option> getOptions()
    {
        return optionsAsList;
    }

    public SelectorType getType()
    {
        return type;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueException
    {
        final String valueAsString = property.getString();
        if ( !optionsAsMap.containsKey( valueAsString ) )
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

    public static Builder newSingleSelectorConfig()
    {
        return new Builder();
    }

    public static class Builder
        implements OptionBuilder
    {
        private SingleSelectorConfig config = new SingleSelectorConfig();

        Builder()
        {
            // protection
        }

        public Builder typeDropdown()
        {
            this.config.type = SelectorType.DROPDOWN;
            return this;
        }

        public Builder typeRadio()
        {
            this.config.type = SelectorType.RADIO;
            return this;
        }

        public Builder type( SelectorType value )
        {
            this.config.type = value;
            return this;
        }

        @Override
        public Builder addOption( String label, String value )
        {
            config.optionsAsList.add( new Option( label, value ) );
            config.optionsAsMap.put( value, new Option( label, value ) );
            return this;
        }

        public SingleSelectorConfig build()
        {
            Preconditions.checkNotNull( config.type, "type for SingleSelectorConfig cannot be null" );
            Preconditions.checkArgument( config.optionsAsList.size() > 0, "No options given" );
            return config;
        }
    }

    public enum SelectorType
    {
        DROPDOWN, RADIO, COMBOBOX;
    }
}
