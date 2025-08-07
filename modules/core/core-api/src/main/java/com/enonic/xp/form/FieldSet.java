package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.nullToEmpty;


@PublicApi
public final class FieldSet
    extends FormItem
    implements Iterable<FormItem>
{
    private final String name;

    private final String label;

    private final String labelI18nKey;

    private final FormItems formItems;

    private FieldSet( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "a name is required for a Layout" );
        Preconditions.checkArgument( !nullToEmpty( builder.name ).isBlank(), "a name is required for a Layout" );
        Preconditions.checkArgument( !builder.name.contains( "." ), "name cannot contain punctuations: " + builder.name );
        this.name = builder.name;

        this.label = Preconditions.checkNotNull( builder.label, "label is required" );
        this.labelI18nKey = builder.labelI18nKey;
        this.formItems = new FormItems( this, builder.formItems );
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public FormItemType getType()
    {
        return FormItemType.LAYOUT;
    }

    public String getLabel()
    {
        return label;
    }

    public String getLabelI18nKey()
    {
        return labelI18nKey;
    }

    @Override
    public Iterator<FormItem> iterator()
    {
        return formItems.iterator();
    }

    @Override
    public FieldSet copy()
    {
        return create( this ).build();
    }

    @Override
    FormItemPath resolvePath()
    {
        return resolveParentPath();
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

        final FieldSet that = (FieldSet) o;
        return super.equals( o ) && Objects.equals( this.label, that.label ) && Objects.equals( this.labelI18nKey, that.labelI18nKey ) &&
            Objects.equals( this.formItems, that.formItems );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), this.label, this.labelI18nKey, this.formItems );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final FieldSet fieldSet )
    {
        return new Builder( fieldSet );
    }

    FormItem getFormItem( final String path )
    {
        return formItems.getFormItem( FormItemPath.from(  path ) );
    }

    public static final class Builder
    {
        private String label;

        private String labelI18nKey;

        private String name;

        private final List<FormItem> formItems = new ArrayList<>();

        private Builder()
        {
        }

        private Builder( final FieldSet source )
        {
            this.label = source.label;
            this.labelI18nKey = source.labelI18nKey;
            this.name = source.getName();

            for ( final FormItem formItemSource : source.formItems )
            {
                formItems.add( formItemSource.copy() );
            }
        }

        public Builder label( String value )
        {
            this.label = value;
            return this;
        }

        public Builder labelI18nKey( String value )
        {
            this.labelI18nKey = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder addFormItem( FormItem formItem )
        {
            this.formItems.add( formItem );
            return this;
        }

        public Builder addFormItems( Iterable<FormItem> iterable )
        {
            for ( FormItem formItem : iterable )
            {
                formItems.add( formItem );
            }
            return this;
        }

        public Builder clearFormItems()
        {
            formItems.clear();
            return this;
        }

        public FieldSet build()
        {
            return new FieldSet( this );
        }
    }
}
