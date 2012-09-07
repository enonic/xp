package com.enonic.wem.core.content.type.formitem.comptype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.formitem.InvalidValueException;

public class DropdownConfig
    implements ComponentTypeConfig
{
    private List<Option> optionsAsList = new ArrayList<Option>();

    private HashMap<String, Option> optionsAsMap = new HashMap<String, Option>();

    public List<Option> getOptions()
    {
        return optionsAsList;
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueException
    {
        final String valueAsString = String.valueOf( data.getValue() );
        if ( !optionsAsMap.containsKey( valueAsString ) )
        {
            throw new InvalidValueException( "Value can only be of one the following strings: " + optionValuesAsCommaSeparatedString() );
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

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private DropdownConfig config = new DropdownConfig();

        Builder()
        {
            // protection
        }

        public Builder addOption( String label, String value )
        {
            config.optionsAsList.add( new Option( label, value ) );
            config.optionsAsMap.put( value, new Option( label, value ) );
            return this;
        }

        public DropdownConfig build()
        {
            return config;
        }
    }
}
