package com.enonic.wem.api.command.content;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.BaseTypes;

public final class GetBaseTypes
    extends Command<BaseTypes>
{
    private EnumSet<BaseTypeKind> baseTypes;

    public GetBaseTypes()
    {
        baseTypes = EnumSet.allOf( BaseTypeKind.class );
    }

    public GetBaseTypes includeTypes( final BaseTypeKind... baseTypeKinds )
    {
        baseTypes.clear();
        baseTypes.addAll( Arrays.asList( baseTypeKinds ) );
        return this;
    }

    public GetBaseTypes includeTypes( final Set<BaseTypeKind> baseTypeKinds )
    {
        baseTypes.clear();
        baseTypes.addAll( baseTypeKinds );
        return this;
    }

    public boolean isIncludeType( final BaseTypeKind baseTypeKind )
    {
        return baseTypes.contains( baseTypeKind );
    }

    @Override
    public void validate()
    {
        // nothing to validate
    }
}
