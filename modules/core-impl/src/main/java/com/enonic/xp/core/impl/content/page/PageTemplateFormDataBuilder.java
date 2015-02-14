package com.enonic.xp.core.impl.content.page;


import com.enonic.xp.core.data.PropertySet;
import com.enonic.xp.core.schema.content.ContentTypeName;
import com.enonic.xp.core.schema.content.ContentTypeNames;

class PageTemplateFormDataBuilder
{
    private ContentTypeNames supports;


    PageTemplateFormDataBuilder supports( ContentTypeNames value )
    {
        supports = value;
        return this;
    }

    void appendData( final PropertySet data )
    {
        if ( supports != null )
        {
            for ( ContentTypeName name : supports )
            {
                data.addString( "supports", name.toString() );
            }
        }
    }
}
