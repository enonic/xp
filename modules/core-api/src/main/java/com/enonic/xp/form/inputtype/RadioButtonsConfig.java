package com.enonic.xp.form.inputtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public class RadioButtonsConfig
    implements InputTypeConfig
{
    private List<Option> optionsAsList = new ArrayList<Option>();

    private HashMap<String, Option> optionsAsMap = new HashMap<String, Option>();

    public List<Option> getOptions()
    {
        return optionsAsList;
    }

    public boolean containsKey( final String name )
    {
        return this.optionsAsMap.containsKey( name );
    }

    public String optionValuesAsCommaSeparatedString()
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
        private RadioButtonsConfig config = new RadioButtonsConfig();

        Builder()
        {
            // protection
        }

        @Override
        public Builder addOption( String label, String value )
        {
            config.optionsAsList.add( new Option( label, value ) );
            config.optionsAsMap.put( value, new Option( label, value ) );
            return this;
        }

        public RadioButtonsConfig build()
        {
            Preconditions.checkArgument( config.optionsAsList.size() > 0, "No options given" );
            return config;
        }
    }
}
