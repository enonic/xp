package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public class FormOptionSetOption
    extends FormItem
    implements Iterable<FormItem>
{

    private final String name;

    private final String label;

    private final boolean defaultOption;

    private final String helpText;

    private final FormItems formItems;

    private FormOptionSetOption( Builder builder )
    {
        super();

        Preconditions.checkNotNull( builder.name, "a name is required for a FormItemSet" );
        Preconditions.checkArgument( StringUtils.isNotBlank( builder.name ), "a name is required for a FormItemSet" );
        Preconditions.checkArgument( !builder.name.contains( "." ), "name cannot contain punctations: " + builder.name );

        this.name = builder.name;
        this.label = builder.label;
        this.defaultOption = builder.defaultOption;
        this.helpText = builder.helpText;
        this.formItems = new FormItems( this );
        for ( final FormItem formItem : builder.formItemsList )
        {
            this.formItems.add( formItem );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof FormOptionSetOption ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final FormOptionSetOption that = (FormOptionSetOption) o;
        return super.equals( o ) &&
            Objects.equals( name, that.name ) &&
            Objects.equals( label, that.label ) &&
            Objects.equals( helpText, that.helpText ) &&
            Objects.equals( defaultOption, that.defaultOption ) &&
            Objects.equals( formItems, that.formItems );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), name, label, defaultOption, formItems, helpText );
    }

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

    public String getHelpText()
    {
        return helpText;
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    @Override
    public FormItemType getType()
    {
        return FormItemType.FORM_OPTION_SET_OPTION;
    }

    @Override
    public FormOptionSetOption copy()
    {
        return create( this ).build();
    }

    @Override
    public Iterator<FormItem> iterator()
    {
        return formItems.iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final FormOptionSetOption formOptionSetOption )
    {
        return new Builder( formOptionSetOption );
    }

    public static class Builder
    {
        private String name;

        private String label;

        private boolean defaultOption;

        private String helpText;

        private List<FormItem> formItemsList = new ArrayList<>();

        private Builder()
        {
        }

        public Builder( final FormOptionSetOption source )
        {
            this.name = source.name;
            this.label = source.label;
            this.defaultOption = source.defaultOption;
            this.helpText = source.helpText;

            for ( final FormItem formItemSource : source.formItems )
            {
                formItemsList.add( formItemSource.copy() );
            }
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

        public Builder clearFormItems()
        {
            formItemsList.clear();
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder defaultOption( boolean value )
        {
            this.defaultOption = value;
            return this;
        }

        public Builder label( final String label )
        {
            this.label = label;
            return this;
        }

        public Builder helpText( final String helpText )
        {
            this.helpText = helpText;
            return this;
        }

        public FormOptionSetOption build()
        {
            return new FormOptionSetOption( this );
        }
    }
}
