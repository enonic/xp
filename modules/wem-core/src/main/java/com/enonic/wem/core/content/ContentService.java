package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;

abstract class ContentService
{
    public static final String CONTENTS_NODE = "contents";

    public static final String NON_CONTENT_NODE_PREFIX = "__";

    public static final String CONTENT_EMBEDDED_NODE = NON_CONTENT_NODE_PREFIX + "embedded";

    public static final String CONTENT_ATTACHMENTS_NODE = NON_CONTENT_NODE_PREFIX + "attachments";

    public static final ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();

    Session session;

    protected ContentService( final Session session )
    {
        this.session = session;
    }

    Contents filterHiddenContents( final Contents contents )
    {
        Contents.Builder filtered = Contents.builder();

        for ( final Content content : contents )
        {
            if ( !Strings.startsWithIgnoreCase( content.getName().toString(), ContentService.NON_CONTENT_NODE_PREFIX ) )
            {
                filtered.add( content );
            }
        }

        return filtered.build();
    }

}
