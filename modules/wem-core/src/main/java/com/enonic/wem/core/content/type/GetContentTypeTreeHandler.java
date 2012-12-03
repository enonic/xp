package com.enonic.wem.core.content.type;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import com.enonic.wem.api.command.content.type.GetContentTypeTree;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeTreeNode;
import com.enonic.wem.api.content.type.ContentTypeTree;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

@Component
public class GetContentTypeTreeHandler
    extends CommandHandler<GetContentTypeTree>
{
    private ContentTypeDao contentTypeDao;

    public GetContentTypeTreeHandler()
    {
        super( GetContentTypeTree.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContentTypeTree command )
        throws Exception
    {
        final ContentTypes contentTypes = contentTypeDao.retrieveAllContentTypes( context.getJcrSession() );
        final ContentTypeTree typesTree = buildTree( contentTypes );
        command.setResult( typesTree );
    }

    private ContentTypeTree buildTree( final ContentTypes contentTypes )
    {
        final ArrayListMultimap<QualifiedContentTypeName, ContentType> typesBySuperType = ArrayListMultimap.create();
        final List<ContentType> rootTypes = Lists.newArrayList();

        for ( ContentType contentType : contentTypes )
        {
            if ( contentType.getSuperType() == null )
            {
                rootTypes.add( contentType );
            }
            else
            {
                typesBySuperType.put( contentType.getSuperType(), contentType );
            }
        }

        final ContentTypeTree tree = new ContentTypeTree( rootTypes );
        for ( ContentTypeTreeNode node : tree.getChildren() )
        {
            addSubTypes( node, typesBySuperType );
        }

        return tree;
    }

    private void addSubTypes( final ContentTypeTreeNode node, final ArrayListMultimap<QualifiedContentTypeName, ContentType> typesBySuperType )
    {
        final List<ContentType> subTypes = typesBySuperType.get( node.getContentType().getQualifiedName() );
        for ( ContentType subType : subTypes )
        {
            final ContentTypeTreeNode subTypeNode = node.add( subType );
            addSubTypes( subTypeNode, typesBySuperType );
        }
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
