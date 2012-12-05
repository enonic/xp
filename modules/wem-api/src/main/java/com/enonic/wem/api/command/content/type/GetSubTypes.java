package com.enonic.wem.api.command.content.type;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypes;

public final class GetSubTypes
    extends Command<SubTypes>
{
    private QualifiedSubTypeNames qualifiedSubTypeNames;

    private boolean getAllContentTypes = false;

    public QualifiedSubTypeNames getQualifiedSubTypeNames()
    {
        return this.qualifiedSubTypeNames;
    }

    public GetSubTypes names( final QualifiedSubTypeNames qualifiedSubTypeNames )
    {
        this.qualifiedSubTypeNames = qualifiedSubTypeNames;
        return this;
    }

    public boolean isGetAll()
    {
        return getAllContentTypes;
    }

    public GetSubTypes all()
    {
        getAllContentTypes = true;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetSubTypes ) )
        {
            return false;
        }

        final GetSubTypes that = (GetSubTypes) o;
        return Objects.equal( this.qualifiedSubTypeNames, that.qualifiedSubTypeNames ) &&
            ( this.getAllContentTypes == that.getAllContentTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedSubTypeNames, this.getAllContentTypes );
    }

    @Override
    public void validate()
    {
        if ( getAllContentTypes )
        {
            Preconditions.checkArgument( this.qualifiedSubTypeNames == null,
                                         "Cannot specify both get all and get content type qualifiedSubTypeNames" );
        }
        else
        {
            Preconditions.checkNotNull( this.qualifiedSubTypeNames, "Content type cannot be null" );
        }
    }

}
