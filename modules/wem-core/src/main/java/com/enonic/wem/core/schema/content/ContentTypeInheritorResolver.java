package com.enonic.wem.core.schema.content;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;

public class ContentTypeInheritorResolver
{
    private final static ContentTypeNodeTranslator CONTENT_TYPE_NODE_TRANSLATOR = new ContentTypeNodeTranslator();

    private final ContentTypes allContentTypes;

    protected ContentTypeInheritorResolver( final Client client )
    {
        final Nodes nodes = client.execute( Commands.node().get().byParent( new NodePath( "/content-types" ) ) );
        this.allContentTypes = CONTENT_TYPE_NODE_TRANSLATOR.fromNodes( nodes );
    }

    protected ContentTypeNames resolveInheritors( final ContentType contentType )
    {
        final ContentTypeNames.Builder builder = ContentTypeNames.newContentTypeNames();
        for ( final ContentType potentialInheritor : this.allContentTypes )
        {
            if ( potentialInheritor.inherit( contentType.getContentTypeName() ) )
            {
                builder.add( potentialInheritor.getContentTypeName() );
            }
        }
        return builder.build();
    }
}
