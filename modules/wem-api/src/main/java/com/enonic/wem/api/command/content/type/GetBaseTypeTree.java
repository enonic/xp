package com.enonic.wem.api.command.content.type;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.BaseType;
import com.enonic.wem.api.support.tree.Tree;

public final class GetBaseTypeTree
    extends Command<Tree<BaseType>>
{
    private EnumSet<BaseTypeKind> baseTypes;

    public GetBaseTypeTree()
    {
        baseTypes = EnumSet.allOf( BaseTypeKind.class );
    }

    public GetBaseTypeTree includeTypes( final BaseTypeKind... baseTypeKinds )
    {
        baseTypes.clear();
        baseTypes.addAll( Arrays.asList( baseTypeKinds ) );
        return this;
    }

    public GetBaseTypeTree includeTypes( final Set<BaseTypeKind> baseTypeKinds )
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
    }
}
