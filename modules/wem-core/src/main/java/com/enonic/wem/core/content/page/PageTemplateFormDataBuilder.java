package com.enonic.wem.core.content.page;


import com.enonic.wem.api.data2.PropertySet;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

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
