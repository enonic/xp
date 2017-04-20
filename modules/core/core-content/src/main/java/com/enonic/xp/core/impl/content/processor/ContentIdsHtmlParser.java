package com.enonic.xp.core.impl.content.processor;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.node.NodeId;

public class ContentIdsHtmlParser
    implements Parser<ContentIds>
{

    private final static String BASE_START = "<a\\s+(?:[^>]*?\\s+)?href=[\"\']content://";

    private final static Pattern BASE_PATTERN = Pattern.compile( BASE_START + NodeId.VALID_NODE_ID_PATTERN + "[\"\']>" );

    @Override
    public ContentIds parse( final String source )
    {

        if ( StringUtils.isEmpty( source ) )
        {
            return ContentIds.empty();
        }

        final Matcher matcher = BASE_PATTERN.matcher( source );

        final Set<ContentId> ids = Sets.newHashSet();

        while ( matcher.find() )
        {
            final String baseUri = matcher.group( 1 );
            ids.add( ContentId.from( baseUri ) );
        }

        return ContentIds.from( ids );
    }
}
