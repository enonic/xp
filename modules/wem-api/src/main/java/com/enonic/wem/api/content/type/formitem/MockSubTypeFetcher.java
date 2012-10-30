package com.enonic.wem.api.content.type.formitem;

import java.util.HashMap;
import java.util.Map;

public class MockSubTypeFetcher
    implements SubTypeFetcher
{
    private Map<SubTypeQualifiedName, SubType> subTypeMap = new HashMap<SubTypeQualifiedName, SubType>();

    @Override
    public SubType getSubType( final SubTypeQualifiedName qualifiedName )
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
