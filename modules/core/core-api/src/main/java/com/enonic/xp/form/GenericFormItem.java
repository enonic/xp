package com.enonic.xp.form;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public abstract class GenericFormItem
    extends FormItem
{

    protected final String name;

    protected final String label;

    protected final String helpText;

    protected final Occurrences occurrences;

    public GenericFormItem( final Builder builder )
    {
        super();

        Preconditions.checkNotNull( builder.name, "a name is required for a FormItemSet" );
        Preconditions.checkArgument( StringUtils.isNotBlank( builder.name ), "a name is required for a FormItemSet" );
        Preconditions.checkArgument( !builder.name.contains( "." ), "name cannot contain punctations: " + builder.name );

        this.name = builder.name;
        this.label = builder.label;
        this.occurrences = builder.occurrences;
        this.helpText = builder.helpText;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public Occurrences getOccurrences()
    {
        return occurrences;
    }

    public boolean isRequired()
    {
        return occurrences.impliesRequired();
    }

    public String getHelpText()
    {
        return helpText;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final GenericFormItem that = (GenericFormItem) o;
        return super.equals( o ) &&
            Objects.equals( this.name, that.name ) &&
            Objects.equals( this.label, that.label ) &&
            Objects.equals( this.occurrences, that.occurrences ) &&
            Objects.equals( this.helpText, that.helpText );
    }

    public abstract static class Builder
    {
        protected String name;

        protected String label;

        protected String helpText;

        protected Occurrences occurrences = Occurrences.create( 0, 1 );

        public Builder()
        {
            // default
        }

        public Builder( final GenericFormItem source )
        {
            this.name = source.name;
            this.label = source.label;
            this.occurrences = source.occurrences;
            this.helpText = source.helpText;
        }

        public abstract GenericFormItem build();
    }
}
