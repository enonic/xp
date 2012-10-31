package com.enonic.wem.api.content.type.formitem.inputtype;


import com.google.common.base.Objects;

import com.enonic.wem.api.content.datatype.BaseDataType;

public class TypedPath
{
    private String path;

    private BaseDataType dataType;

    public TypedPath( final String path, final BaseDataType dataType )
    {
        this.path = path;
        this.dataType = dataType;
    }

    public String getPath()
    {
        return path;
    }

    public BaseDataType getDataType()
    {
        return dataType;
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "path", path );
        s.add( "dataType", dataType );
        return s.toString();
    }

    public static TypedPath newTypedPath( String path, BaseDataType dataType )
    {
        return new TypedPath( path, dataType );
    }
}
