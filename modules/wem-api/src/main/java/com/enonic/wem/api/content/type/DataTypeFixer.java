package com.enonic.wem.api.content.type;


import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataVisitor;
import com.enonic.wem.api.content.type.formitem.Component;
import com.enonic.wem.api.content.type.formitem.FormItem;

public class DataTypeFixer
{
    private ContentType contentType;

    public DataTypeFixer( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public void fix( ContentData contentData )
    {
        final DataVisitor dataVisitor = new DataVisitor()
        {
            @Override
            public void visit( final Data data )
            {
                final FormItem formItem = contentType.getFormItem( data.getPath().resolveFormItemPath() );
                if ( formItem != null )
                {
                    if ( formItem instanceof Component )
                    {
                        final Component component = (Component) formItem;
                        component.getComponentType().ensureType( data );
                    }
                }
            }
        };
        dataVisitor.traverse( contentData );
    }
}
