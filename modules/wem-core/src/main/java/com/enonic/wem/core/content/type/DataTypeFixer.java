package com.enonic.wem.core.content.type;


import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.data.Entry;
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
        for ( Entry entry : contentData )
        {
            if ( entry instanceof Data )
            {
                Data data = (Data) entry;
                Component component = contentType.getField( data.getPath().resolveFormItemPath() );
                component.getFieldType().ensureType( data );
            }
        }
    }
}
