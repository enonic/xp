package com.enonic.xp.form;


import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.mixin.FormFragmentDescriptor;
import com.enonic.xp.schema.mixin.FormFragmentName;

@PublicApi
public final class FormFragment
    extends FormItem
{
    private final FormFragmentName formFragmentName;

    private FormFragment( Builder builder )
    {
        super();

        Objects.requireNonNull( builder.formFragmentName, "mixinName is required" );
        this.formFragmentName = builder.formFragmentName;
    }

    @Override
    public FormItemType getType()
    {
        return FormItemType.MIXIN_REFERENCE;
    }

    public FormFragmentName getFormFragmentName()
    {
        return formFragmentName;
    }

    @Override
    public String getName()
    {
        return formFragmentName.getLocalName();
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

        final FormFragment that = (FormFragment) o;
        return super.equals( o ) && Objects.equals( this.formFragmentName, that.formFragmentName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), this.formFragmentName );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public FormFragment copy()
    {
        return create( this ).build();
    }

    public static Builder create( final FormFragmentDescriptor mixin )
    {
        return new Builder( mixin );
    }

    public static Builder create( final FormFragment inline )
    {
        return new Builder( inline );
    }

    public static final class Builder
    {
        private FormFragmentName formFragmentName;

        private Builder()
        {
        }

        private Builder( FormFragment source )
        {
            this.formFragmentName = source.formFragmentName;
        }

        private Builder( final FormFragmentDescriptor descriptor )
        {
            this.formFragmentName = descriptor.getName();
        }

        public Builder formFragment( final FormFragmentDescriptor descriptor )
        {
            this.formFragmentName = descriptor.getName();
            return this;
        }

        public Builder formFragment( final String name )
        {
            this.formFragmentName = FormFragmentName.from( name );
            return this;
        }

        public Builder formFragment( final FormFragmentName mixinName )
        {
            this.formFragmentName = mixinName;
            return this;
        }

        public FormFragment build()
        {
            return new FormFragment( this );
        }
    }


}
