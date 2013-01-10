package com.enonic.wem.api.command.content.type;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;

public final class CreateMixin
    extends Command<QualifiedMixinName>
{
    private Mixin mixin;

    public CreateMixin mixin( final Mixin mixin )
    {
        this.mixin = mixin;
        return this;
    }

    public Mixin getMixin()
    {
        return mixin;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateMixin ) )
        {
            return false;
        }

        final CreateMixin that = (CreateMixin) o;
        return Objects.equal( this.mixin, that.mixin );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.mixin );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.mixin, "mixin cannot be null" );
    }
}
