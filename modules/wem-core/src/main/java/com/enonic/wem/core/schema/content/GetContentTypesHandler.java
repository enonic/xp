package com.enonic.wem.core.schema.content;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.entity.GetNodesByPathsService;


public final class GetContentTypesHandler
    extends AbstractContentTypeHandler<GetContentTypes>
{
    @Override
    public void handle()
        throws Exception
    {
        final ContentTypes contentTypes = getContentTypes( command.getContentTypeNames() );
        if ( !command.isMixinReferencesToFormItems() )
        {
            command.setResult( contentTypes );
        }
        else
        {
            command.setResult( transformMixinReferences( contentTypes ) );
        }
    }

    private ContentTypes getContentTypes( final ContentTypeNames contentTypeNames )
    {
        final Set<NodePath> nodePaths = createNodePaths( contentTypeNames );

        final Nodes nodes =
            new GetNodesByPathsService( this.context.getJcrSession(), Commands.node().get().byPaths( NodePaths.from( nodePaths ) ) ).
                failWithExceptionAtNoNodeFound( false ).execute();

        return nodesToContentTypes( nodes );
    }

    private ContentTypes nodesToContentTypes( final Nodes nodes )
    {
        final ContentTypes.Builder builder = ContentTypes.newContentTypes();

        final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( this.context.getClient() );

        for ( final Node node : nodes )
        {
            final ContentType contentType = nodeToContentType( node, contentTypeInheritorResolver );
            builder.add( contentType );
        }

        return builder.build();
    }

    private Set<NodePath> createNodePaths( final ContentTypeNames contentTypeNames )
    {
        final Set<NodePath> nodePaths = Sets.newHashSet();

        for ( ContentTypeName contentTypeName : contentTypeNames.getSet() )
        {
            nodePaths.add( NodePath.newPath( "/content-types/" + contentTypeName ).build() );
        }
        return nodePaths;
    }
}
