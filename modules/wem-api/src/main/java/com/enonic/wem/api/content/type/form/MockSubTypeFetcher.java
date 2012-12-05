package com.enonic.wem.api.content.type.form;

import java.util.HashMap;
import java.util.Map;

public class MockSubTypeFetcher
    implements SubTypeFetcher
{
    private Map<QualifiedSubTypeName, SubType> subTypeMap = new HashMap<QualifiedSubTypeName, SubType>();

    @Override
    public SubType getSubType( final QualifiedSubTypeName qualifiedName )
    {
        return subTypeMap.get( qualifiedName );
    }

    public void add( final FormItemSetSubType subType )
    {
        subTypeMap.put( subType.getQualifiedName(), subType );
    }

    public void add( final InputSubType subType )
    {
        subTypeMap.put( subType.getQualifiedName(), subType );
    }
}
