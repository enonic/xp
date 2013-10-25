package com.enonic.wem.api.command.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;

public final class GetMixin
    extends Command<Mixin>
{
    private QualifiedMixinName qualifiedName;

    public QualifiedMixinName getQualifiedName()
    {
        return this.qualifiedName;
    }

    public GetMixin qualifiedName( final QualifiedMixinName value )
    {
        this.qualifiedName = value;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetMixin ) )
        {
            return false;
        }

        final GetMixin that = (GetMixin) o;
        return Objects.equal( this.qualifiedName, that.qualifiedName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.qualifiedName, "qualifiedName cannot be null" );
    }

}
