package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.core.command.CommandContext;

abstract class ContentService
{
    public static final String NON_CONTENT_NODE_PREFIX = "__";

    final ContentNodeTranslator translator;

    CommandContext context;

    Session session;

    Client client;

    NodeService nodeService;

    ContentTypeService contentTypeService;

    protected ContentService( final CommandContext context, final NodeService nodeService, final ContentTypeService contentTypeService )
    {
        this.context = context;
        this.session = context.getJcrSession();
        this.client = context.getClient();
        this.nodeService = nodeService;
        this.contentTypeService = contentTypeService;

        this.translator = new ContentNodeTranslator( this.client, this.contentTypeService );
    }

    Nodes removeNonContentNodes( final Nodes nodes )
    {
        Nodes.Builder filtered = new Nodes.Builder();

        for ( final Node node : nodes )
        {
            if ( !Strings.startsWithIgnoreCase( node.name().toString(), ContentService.NON_CONTENT_NODE_PREFIX ) )
            {
                filtered.add( node );
            }
        }

        return filtered.build();
    }

}
