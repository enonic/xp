package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public class FormOptionSet
    extends FormItem
    implements Iterable<FormOptionSetOption>
{

    private final String name;

    private final String label;

    private final boolean expanded;

    private final List<FormOptionSetOption> optionSetOptions;

    private final Occurrences occurrences;

    private FormOptionSet( Builder builder )
    {
        super();

        Preconditions.checkNotNull( builder.name, "a name is required for a FormItemSet" );
        Preconditions.checkArgument( StringUtils.isNotBlank( builder.name ), "a name is required for a FormOptionSet" );
        Preconditions.checkArgument( !builder.name.contains( "." ), "name cannot contain punctuations: " + builder.name );

        this.name = builder.name;
        this.label = builder.label;
        this.expanded = builder.expanded;
        this.occurrences = builder.occurrences;
        this.optionSetOptions = builder.setOptionsList.stream().collect( Collectors.toList() );
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public FormItemType getType()
    {
        return FormItemType.FORM_OPTION_SET;
    }

    public List<FormOptionSetOption> getOptions()
    {
        return this.optionSetOptions;
    }

    public String getLabel()
    {
        return label;
    }

    public Occurrences getOccurrences()
    {
        return occurrences;
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    @Override
    public FormItem copy()
    {
        return create( this ).build();
    }

    @Override
    public Iterator<FormOptionSetOption> iterator()
    {
        return optionSetOptions.iterator();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof FormOptionSet ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final FormOptionSet that = (FormOptionSet) o;
        return super.equals( o ) &&
            Objects.equals( expanded, that.expanded ) &&
            Objects.equals( name, that.name ) &&
            Objects.equals( label, that.label ) &&
            Objects.equals( optionSetOptions, that.optionSetOptions ) &&
            Objects.equals( occurrences, that.occurrences );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), name, label, expanded, optionSetOptions, occurrences );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final FormOptionSet formOptionSet )
    {
        return new Builder( formOptionSet );
    }

    public static class Builder
    {
        private String name;

        private String label;

        private boolean expanded = false;

        private List<FormOptionSetOption> setOptionsList;

        private Occurrences occurrences = Occurrences.create( 0, 1 );

        private Builder()
        {
            this.setOptionsList = new ArrayList<>();
        }

        private Builder( final FormOptionSet source )
        {
            this.setOptionsList = source.optionSetOptions;
            this.name = source.name;
            this.label = source.label;
            this.expanded = source.expanded;
            this.occurrences = source.occurrences;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder label( final String label )
        {
            this.label = label;
            return this;
        }

        public Builder expanded( boolean value )
        {
            this.expanded = value;
            return this;
        }

        public Builder occurrences( final Occurrences value )
        {
            occurrences = value;
            return this;
        }

        public Builder addOptionSetOption( final FormOptionSetOption setOption )
        {
            this.setOptionsList.add( setOption );
            return this;
        }

        public Builder addOptionSetOptions( final Iterable<FormOptionSetOption> setOptions )
        {
            for ( final FormOptionSetOption setOption : setOptions )
            {
                addOptionSetOption( setOption );
            }
            return this;
        }

        public FormOptionSet build()
        {
            return new FormOptionSet( this );
        }
    }
}
