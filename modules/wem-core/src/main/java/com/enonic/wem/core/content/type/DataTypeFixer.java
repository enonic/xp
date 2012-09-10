package com.enonic.wem.core.content.type;


import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.formitem.Component;

public class DataTypeFixer
{
    private ContentType contentType;

    public DataTypeFixer( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public void fix( ContentData contentData )
    {
        for ( Data data : contentData )
        {
            Component component = contentType.getComponent( data.getPath().resolveFormItemPath() );
            if ( component != null )
            {
                component.getComponentType().ensureType( data );
            }
        }
    }
}
