package com.enonic.wem.core.content.type.configitem.fieldtype;

import java.util.ArrayList;
import java.util.List;

public class RadioButtonsConfig
    implements FieldTypeConfig
{
    private List<Option> options = new ArrayList<Option>();

    public void add( Option option )
    {
        this.options.add( option );
    }

    public List<Option> getOptions()
    {
        return options;
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
        private RadioButtonsConfig config = new RadioButtonsConfig();

        public Builder addOption( String label, String value )
        {
            config.add( new Option( label, value ) );
            return this;
        }

        public RadioButtonsConfig build()
        {
            return config;
        }
    }
}
