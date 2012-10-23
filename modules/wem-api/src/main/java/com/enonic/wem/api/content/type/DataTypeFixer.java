package com.enonic.wem.api.content.type;


import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataVisitor;
import com.enonic.wem.api.content.type.formitem.Component;
import com.enonic.wem.api.content.type.formitem.FormItemPath;
import com.enonic.wem.api.content.type.formitem.HierarchicalFormItem;

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
                final FormItemPath path = data.getPath().resolveFormItemPath();
                final HierarchicalFormItem formItem = contentType.getFormItem( path );
                if ( formItem != null && formItem.getPath().equals( path ) )
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
