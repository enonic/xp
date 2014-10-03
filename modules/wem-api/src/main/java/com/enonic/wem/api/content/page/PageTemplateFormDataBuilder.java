package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public class PageTemplateFormDataBuilder
{
    private PageDescriptorKey controller;

    private ContentTypeNames supports;

    public PageTemplateFormDataBuilder controller( PageDescriptorKey value )
    {
        controller = value;
        return this;
    }

    public PageTemplateFormDataBuilder supports( ContentTypeNames value )
    {
        supports = value;
        return this;
    }

    public void appendData( final RootDataSet data )
    {
        final Value value = controller != null ? Value.newString( controller.toString() ) : Value.newValue( null, ValueTypes.STRING );
        data.setProperty( "controller", value );

        if ( supports != null )
        {
            for ( ContentTypeName name : supports )
            {
                data.addProperty( "supports", Value.newString( name.toString() ) );
            }
        }
    }
}
