package com.enonic.wem.api.content.type;


import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataVisitor;
import com.enonic.wem.api.content.type.formitem.ComponentPath;
import com.enonic.wem.api.content.type.formitem.HierarchicalComponent;
import com.enonic.wem.api.content.type.formitem.Input;

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
                final ComponentPath path = data.getPath().resolveComponentPath();
                final HierarchicalComponent component = contentType.getComponent( path );
                if ( component != null && component.getPath().equals( path ) )
                {
                    if ( component instanceof Input )
                    {
                        final Input input = (Input) component;
                        input.getInputType().ensureType( data );
                    }
                }
            }
        };
        dataVisitor.traverse( contentData );
    }
}
