package com.enonic.wem.api.content.schema.type.form.inputtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.schema.type.form.InvalidValueException;

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
    public void checkValidity( final Data data )
        throws InvalidValueException
    {
        final String valueAsString = data.asString();
        if ( !optionsAsMap.containsKey( valueAsString ) )
        {
            throw new InvalidValueException( data,
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

    public static class Option
    {
        private String label;

        private String value;

        Option( final String label, final String value )
        {
            this.label = label;
            this.value = value;
        }

        public String getLabel()
        {
            return label;
        }

        public String getValue()
        {
            return value;
        }
    }

    public static Builder newSingleSelectorConfig()
    {
        return new Builder();
    }

    public static class Builder
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
        DROPDOWN, RADIO;
    }
}
