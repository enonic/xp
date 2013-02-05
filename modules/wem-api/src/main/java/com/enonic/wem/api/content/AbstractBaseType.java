package com.enonic.wem.api.content;


import com.enonic.wem.api.content.type.BaseTypeKey;

public abstract class AbstractBaseType
    implements BaseType
{

    public BaseTypeKey getBaseTypeKey()
    {
        return BaseTypeKey.from( this.getClass(), getQualifiedName() );
    }
}
