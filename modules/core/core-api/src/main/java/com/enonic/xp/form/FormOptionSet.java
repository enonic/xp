package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public class FormOptionSet
    extends FormItem
    implements Iterable<FormOptionSetOption>
{
    private final String name;

    private final String label;

    private final String labelI18nKey;

    private final boolean expanded;

    private final FormItems optionSetOptions;

    private final Occurrences occurrences;

    private final Occurrences multiselection;

    private final String helpText;

    private final String helpTextI18nKey;

    private FormOptionSet( Builder builder )
    {
        super();

        Preconditions.checkNotNull( builder.name, "a name is required for a FormItemSet" );
        Preconditions.checkArgument( StringUtils.isNotBlank( builder.name ), "a name is required for a FormOptionSet" );
        Preconditions.checkArgument( !builder.name.contains( "." ), "name cannot contain punctuations: " + builder.name );

        this.name = builder.name;
        this.label = builder.label;
        this.labelI18nKey = builder.labelI18nKey;
        this.helpText = builder.helpText;
        this.helpTextI18nKey = builder.helpTextI18nKey;
        this.expanded = builder.expanded;
        this.occurrences = builder.occurrences;
        this.multiselection = builder.multiselection;
        this.optionSetOptions = new FormItems( this );
        for ( final FormItem formItem : builder.setOptionsList )
        {
            this.optionSetOptions.add( formItem );
        }
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

    public FormItems getFormItems()
    {
        return optionSetOptions;
    }

    public String getLabel()
    {
        return label;
    }

    public String getLabelI18nKey()
    {
        return labelI18nKey;
    }

    public Occurrences getOccurrences()
    {
        return occurrences;
    }

    public Occurrences getMultiselection()
    {
        return multiselection;
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    public String getHelpText()
    {
        return helpText;
    }

    public String getHelpTextI18nKey()
    {
        return helpTextI18nKey;
    }

    public boolean isRequired()
    {
        return occurrences.impliesRequired();
    }

    @Override
    public FormItem copy()
    {
        return create( this ).build();
    }

    @Override
    public Iterator<FormOptionSetOption> iterator()
    {
        return StreamSupport.stream( this.optionSetOptions.spliterator(), false ).map( FormItem::toFormOptionSetOption ).iterator();
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
        return super.equals( o ) && Objects.equals( expanded, that.expanded ) && Objects.equals( name, that.name ) &&
            Objects.equals( label, that.label ) && Objects.equals( helpText, that.helpText ) &&
            Objects.equals( optionSetOptions, that.optionSetOptions ) && Objects.equals( occurrences, that.occurrences ) &&
            Objects.equals( multiselection, that.multiselection ) && Objects.equals( helpText, that.helpText ) &&
            Objects.equals( labelI18nKey, that.labelI18nKey ) && Objects.equals( helpTextI18nKey, that.helpTextI18nKey );
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
    {
        private String name;

        private String label;

        private String labelI18nKey;

        private boolean expanded = false;

        private List<FormOptionSetOption> setOptionsList = new ArrayList<>();

        private Occurrences occurrences = Occurrences.create( 0, 1 );

        private Occurrences multiselection = Occurrences.create( 0, 1 );

        private String helpText;

        private String helpTextI18nKey;

        private Builder()
        {
        }

        private Builder( final FormOptionSet source )
        {
            this.name = source.name;
            this.label = source.label;
            this.labelI18nKey = source.labelI18nKey;
            this.expanded = source.expanded;
            this.occurrences = source.occurrences;
            this.multiselection = source.multiselection;
            this.helpText = source.helpText;
            this.helpTextI18nKey = source.helpTextI18nKey;

            for ( final FormOptionSetOption formItemSource : source )
            {
                setOptionsList.add( formItemSource.copy() );
            }
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

        public Builder labelI18nKey( final String labelI18nKey )
        {
            this.labelI18nKey = labelI18nKey;
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

        public Builder required( boolean value )
        {
            if ( value && !occurrences.impliesRequired() )
            {
                occurrences = Occurrences.create( 1, occurrences.getMaximum() );
            }
            else if ( !value && occurrences.impliesRequired() )
            {
                occurrences = Occurrences.create( 0, occurrences.getMaximum() );
            }
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                occurrences = Occurrences.create( occurrences.getMinimum(), 0 );
            }
            else
            {
                occurrences = Occurrences.create( occurrences.getMinimum(), 1 );
            }
            return this;
        }

        public Builder multiselection( final Occurrences value )
        {
            multiselection = value;
            return this;
        }

        public Builder helpText( String value )
        {
            helpText = value;
            return this;
        }

        public Builder helpTextI18nKey( String value )
        {
            helpTextI18nKey = value;
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

        public Builder clearOptions()
        {
            setOptionsList.clear();
            return this;
        }

        public FormOptionSet build()
        {
            return new FormOptionSet( this );
        }
    }
}
