package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FormOptionSet
    extends GenericFormItem
    implements Iterable<FormOptionSetOption>
{
    private final boolean expanded;

    private final List<FormOptionSetOption> optionSetOptions;

    private final Occurrences multiselection;

    private FormOptionSet( Builder builder )
    {
        super( builder );

        this.expanded = builder.expanded;
        this.multiselection = builder.multiselection;
        this.optionSetOptions = builder.setOptionsList.stream().collect( Collectors.toList() );
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

    public Occurrences getMultiselection()
    {
        return multiselection;
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    public boolean isRequired()
    {
        return occurrences.impliesRequired();
    }

    public int getNumberOfDefaultOptions()
    {
        return (int) this.optionSetOptions.stream().filter( option -> option.isDefaultOption() ).count();
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
            Objects.equals( optionSetOptions, that.optionSetOptions ) &&
            Objects.equals( multiselection, that.multiselection );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), name, label, expanded, optionSetOptions, occurrences, helpText );
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
        extends GenericFormItem.Builder
    {

        private boolean expanded = false;

        private List<FormOptionSetOption> setOptionsList;

        private Occurrences multiselection = Occurrences.create( 0, 1 );

        private Builder()
        {
            this.setOptionsList = new ArrayList<>();
        }

        private Builder( final FormOptionSet source )
        {
            super( source );

            this.setOptionsList = source.optionSetOptions;
            this.expanded = source.expanded;
            this.multiselection = source.multiselection;
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
            this.occurrences = value;
            return this;
        }

        public Builder multiselection( final Occurrences value )
        {
            multiselection = value;
            return this;
        }

        public Builder helpText( String value )
        {
            this.helpText = value;
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
