package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypeName;

final class XmlModuleRelativeResolver
{
    private final ModuleKey current;

    public XmlModuleRelativeResolver( final ModuleKey current )
    {
        this.current = current;
    }

    public ContentTypeName toContentTypeName( final String name )
    {
        if ( name.contains( ":" ) )
        {
            return ContentTypeName.from( name );
        }

        return ContentTypeName.from( this.current, name );
    }
}
