package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public class FormOptionSetOption
{

    private final String name;

    private final String label;

    private final boolean defaultOption;

    private final List<FormItem> items;

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public boolean isDefaultOption()
    {
        return defaultOption;
    }

    public List<FormItem> getFormItems()
    {
        return items;
    }

    private FormOptionSetOption( Builder builder )
    {
        super();

        Preconditions.checkNotNull( builder.name, "a name is required for a FormItemSet" );
        Preconditions.checkArgument( StringUtils.isNotBlank( builder.name ), "a name is required for a FormItemSet" );
        Preconditions.checkArgument( !builder.name.contains( "." ), "name cannot contain punctations: " + builder.name );

        this.name = builder.name;
        this.label = builder.label;
        this.defaultOption = builder.defaultOption;
        this.items = builder.formItemsList.stream().collect( Collectors.toList() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String name;

        private String label;

        private boolean defaultOption;

        private List<FormItem> formItemsList;

        private Builder()
        {
            this.formItemsList = new ArrayList<>();
        }

        public Builder addFormItem( final FormItem formItem )
        {
            this.formItemsList.add( formItem );
            return this;
        }

        public Builder addFormItems( final Iterable<FormItem> formItems )
        {
            for ( FormItem formItem : formItems )
            {
                addFormItem( formItem );
            }
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder isDefaultOption( boolean value )
        {
            this.defaultOption = value;
            return this;
        }

        public Builder label( final String label )
        {
            this.label = label;
            return this;
        }

        public FormOptionSetOption build()
        {
            return new FormOptionSetOption( this );
        }
    }
}
