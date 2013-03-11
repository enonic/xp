package com.enonic.wem.api.content.schema.content;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.DataVisitor;
import com.enonic.wem.api.content.schema.content.form.FormItem;
import com.enonic.wem.api.content.schema.content.form.FormItemPath;
import com.enonic.wem.api.content.schema.content.form.Input;

public class DataTypeFixer
{
    private ContentType contentType;

    public DataTypeFixer( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public void fix( final DataSet dataSet )
    {
        final DataVisitor dataVisitor = new DataVisitor()
        {
            @Override
            public void visit( final Data data )
            {
                final FormItemPath path = FormItemPath.from( data.getPath().resolvePathElementNames() );
                final FormItem formItem = contentType.form().getFormItem( path );
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
        dataVisitor.traverse( dataSet );
    }
}
