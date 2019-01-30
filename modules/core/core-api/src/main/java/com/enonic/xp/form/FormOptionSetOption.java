package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FormOptionSetOption
    extends NamedFormItem
    implements Iterable<FormItem>
{
    private final String label;

    private final String labelI18nKey;

    private final boolean defaultOption;

    private final String helpText;

    private final String helpTextI18nKey;

    private final FormItems formItems;

    private FormOptionSetOption( Builder builder )
    {
        super( builder.name );

        this.label = builder.label;
        this.defaultOption = builder.defaultOption;
        this.helpText = builder.helpText;
        this.labelI18nKey = builder.labelI18nKey;
        this.helpTextI18nKey = builder.helpTextI18nKey;

        this.formItems = new FormItems( this );
        for ( final FormItem formItem : builder.formItemsList )
        {
            this.formItems.add( formItem );
        }
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

    public String getLabelI18nKey()
    {
        return labelI18nKey;
    }

    public String getHelpTextI18nKey()
    {
        return helpTextI18nKey;
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
        if ( !super.equals( o ) )
        {
            return false;
        }
        final FormOptionSetOption formItems1 = (FormOptionSetOption) o;
        return defaultOption == formItems1.defaultOption &&
            com.google.common.base.Objects.equal( label, formItems1.label ) &&
            com.google.common.base.Objects.equal( labelI18nKey, formItems1.labelI18nKey ) &&
            com.google.common.base.Objects.equal( helpText, formItems1.helpText ) &&
            com.google.common.base.Objects.equal( helpTextI18nKey, formItems1.helpTextI18nKey ) &&
            com.google.common.base.Objects.equal( formItems, formItems1.formItems );
    }

    @Override
    public int hashCode()
    {
        return com.google.common.base.Objects.hashCode( super.hashCode(), label, labelI18nKey, defaultOption, helpText,
                                                        helpTextI18nKey, formItems );
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
        extends NamedFormItem.Builder<Builder>
    {
        private String label;

        private String labelI18nKey;

        private boolean defaultOption;

        private String helpText;

        private String helpTextI18nKey;

        private List<FormItem> formItemsList = new ArrayList<>();

        private Builder()
        {
        }

        public Builder( final FormOptionSetOption source )
        {
            super( source );
            this.label = source.label;
            this.defaultOption = source.defaultOption;
            this.helpText = source.helpText;
            this.labelI18nKey = source.labelI18nKey;
            this.helpTextI18nKey = source.helpTextI18nKey;

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

        public Builder labelI18nKey( String value )
        {
            labelI18nKey = value;
            return this;
        }

        public Builder helpText( final String helpText )
        {
            this.helpText = helpText;
            return this;
        }


        public Builder helpTextI18nKey( String value )
        {
            helpTextI18nKey = value;
            return this;
        }

        public FormOptionSetOption build()
        {
            return new FormOptionSetOption( this );
        }
    }
}
