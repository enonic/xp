package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;

abstract class ContentService
{
    public static final String NON_CONTENT_NODE_PREFIX = "__";

    public static final ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();

    Session session;

    protected ContentService( final Session session )
    {
        this.session = session;
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
