package com.enonic.wem.core.schema.content;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
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
import com.enonic.wem.core.schema.content.dao.ContentTypeInheritorResolver;


public final class GetContentTypesHandler
    extends AbstractContentTypeHandler<GetContentTypes>
{
    @Override
    public void handle()
        throws Exception
    {
        final List<ContentType> contentTypeList = getContentTypeList();

        if ( !command.isMixinReferencesToFormItems() )
        {
            command.setResult( ContentTypes.from( contentTypeList ) );
        }
        else
        {
            command.setResult( transformMixinReferences( ContentTypes.from( contentTypeList ) ) );
        }
    }

    private List<ContentType> getContentTypeList()
    {
        final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( getAllContentTypes() );

        final ContentTypeNames contentTypeNames = command.getContentTypeNames();

        final List<ContentType> contentTypeList = Lists.newArrayList();

        final Set<NodePath> nodePaths = createNodePaths( contentTypeNames );

        final Nodes nodes = context.getClient().execute( Commands.node().get().byPaths( NodePaths.from( nodePaths ) ) );

        for ( final Node node : nodes )
        {
            contentTypeList.add( toContentType( node, contentTypeInheritorResolver ) );
        }

        return contentTypeList;
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

    private ContentType toContentType( final Node node, final ContentTypeInheritorResolver contentTypeInheritorResolver )
    {
        ContentType contentType = CONTENT_TYPE_NODE_TRANSLATOR.fromNode( node );

        contentType = appendInheritors( contentTypeInheritorResolver, contentType );

        return contentType;
    }


}
