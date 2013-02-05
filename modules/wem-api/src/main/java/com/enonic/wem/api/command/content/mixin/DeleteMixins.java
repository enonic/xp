package com.enonic.wem.api.command.content.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.mixin.QualifiedMixinNames;
import com.enonic.wem.api.content.type.MixinDeletionResult;

public final class DeleteMixins
    extends Command<MixinDeletionResult>
{
    private QualifiedMixinNames qualifiedMixinNames;

    public QualifiedMixinNames getNames()
    {
        return this.qualifiedMixinNames;
    }

    public DeleteMixins names( final QualifiedMixinNames qualifiedMixinNames )
    {
        this.qualifiedMixinNames = qualifiedMixinNames;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteMixins ) )
        {
            return false;
        }

        final DeleteMixins that = (DeleteMixins) o;
        return Objects.equal( this.qualifiedMixinNames, that.qualifiedMixinNames );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedMixinNames );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.qualifiedMixinNames, "qualifiedMixinNames cannot be null" );
    }
}
