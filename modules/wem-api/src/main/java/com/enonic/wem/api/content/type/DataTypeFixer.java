package com.enonic.wem.api.content.type;


import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataVisitor;
import com.enonic.wem.api.content.type.form.FormItemPath;
import com.enonic.wem.api.content.type.form.HierarchicalFormItem;
import com.enonic.wem.api.content.type.form.Input;

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
                final HierarchicalFormItem formItem = contentType.form().getFormItem( path );
                if ( formItem != null && formItem.getPath().equals( path ) )
                {
                    if ( formItem instanceof Input )
                    {
                        final Input input = (Input) formItem;
                        input.getInputType().ensureType( data );
                    }
                }
            }
        };
        dataVisitor.traverse( contentData );
    }
}
