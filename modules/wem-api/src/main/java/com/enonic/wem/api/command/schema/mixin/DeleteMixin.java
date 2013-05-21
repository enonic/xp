package com.enonic.wem.api.command.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;

public final class DeleteMixin
    extends Command<DeleteMixinResult>
{
    private QualifiedMixinName qualifiedMixinName;

    public QualifiedMixinName getName()
    {
        return this.qualifiedMixinName;
    }

    public DeleteMixin name( final QualifiedMixinName qualifiedMixinName )
    {
        this.qualifiedMixinName = qualifiedMixinName;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteMixin ) )
        {
            return false;
        }

        final DeleteMixin that = (DeleteMixin) o;
        return Objects.equal( this.qualifiedMixinName, that.qualifiedMixinName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedMixinName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.qualifiedMixinName, "qualifiedMixinName cannot be null" );
    }
}
