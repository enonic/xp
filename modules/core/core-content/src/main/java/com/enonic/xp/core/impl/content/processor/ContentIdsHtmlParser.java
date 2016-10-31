package com.enonic.xp.core.impl.content.processor;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Sets;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.node.NodeId;

@Component
public class ContentIdsHtmlParser
    implements Parser<ContentIds>
{
    private final static String HREF_TAG_START = "<a href=\"";

    private final static String IMG_TAG_START = "<img src=\"";

    private final static String[] LINK_FORMATS =
        {HREF_TAG_START + "content://", HREF_TAG_START + "media://download/", IMG_TAG_START + "image://"};

    private final static String NODE_ID_GROUP = "(?<nodeId>" + NodeId.VALID_NODE_ID_PATTERN + ")";

    private final static String PARAMETERS = "(\\?[^\"]+)?";

    private final static Pattern PATTERN =
        Pattern.compile( "(" + StringUtils.join( LINK_FORMATS, "|" ) + ")" + NODE_ID_GROUP + PARAMETERS + "\"" );

    @Override
    public ContentIds parse( final String source )
    {
        if ( StringUtils.isEmpty( source ) )
        {
            return ContentIds.empty();
        }

        final Matcher matcher = PATTERN.matcher( source );

        final Set<ContentId> ids = Sets.newHashSet();

        while ( matcher.find() )
        {
            final String baseUri = matcher.group( "nodeId" );
            ids.add( ContentId.from( baseUri ) );
        }

        return ContentIds.from( ids );
    }
}
