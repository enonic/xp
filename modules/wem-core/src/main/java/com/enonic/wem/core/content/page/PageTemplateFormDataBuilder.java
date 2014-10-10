package com.enonic.wem.core.content.page;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
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

    void appendData( final RootDataSet data )
    {
        if ( supports != null )
        {
            for ( ContentTypeName name : supports )
            {
                data.addProperty( "supports", Value.newString( name.toString() ) );
            }
        }
    }
}
