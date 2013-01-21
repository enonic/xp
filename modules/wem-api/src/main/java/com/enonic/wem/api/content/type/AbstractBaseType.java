package com.enonic.wem.api.content.type;


public abstract class AbstractBaseType
    implements BaseType
{

    public BaseTypeKey getBaseTypeKey()
    {
        return BaseTypeKey.from( this.getClass(), getQualifiedName() );
    }
}
