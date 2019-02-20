package com.enonic.xp.repo.impl.dump.upgrade.htmlarea;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeVersion;

public class HtmlAreaNodeDataUpgrader
{
    private static final Pattern HTML_LINK_PATTERN =
        Pattern.compile( "(?:href|src)=(\"((content|media|image)://(?:(download|inline)/)?([0-9a-z-/]+)(\\?[^\"]+)?)\")" );

    private Set<String> references = Sets.newHashSet();

    private final PatternIndexConfigDocument indexConfigDocument;

    public HtmlAreaNodeDataUpgrader( final PatternIndexConfigDocument indexConfigDocument )
    {
        this.indexConfigDocument = indexConfigDocument;

    }

    public boolean upgrade( final NodeVersion nodeVersion )
    {
        if ( !isContent( nodeVersion ) )
        {
            return false;
        }

        final PropertyTree nodeData = nodeVersion.getData();
        upgradeNodeData( nodeData.getRoot() );

        //TODO Handle old cases that did not have htmlStripper processors

        System.out.println( "References" + references );

        return false;
    }

    private boolean isContent( final NodeVersion nodeVersion )
    {
        return ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.getNodeType() );
    }

    private void upgradeNodeData( final PropertySet contentData )
    {
        indexConfigDocument.getPathIndexConfigs().
            stream().
            filter( this::hasHtmlStripperProcessor ).
            forEach( pathIndexConfig -> upgradeHtmlAreaProperty( contentData, pathIndexConfig ) );
    }

    private boolean hasHtmlStripperProcessor( final PathIndexConfig pathIndexConfig )
    {
        for ( IndexValueProcessor indexValueProcessor : pathIndexConfig.getIndexConfig().getIndexValueProcessors() )
        {
            if ( "htmlStripper".equals( indexValueProcessor.getName() ) )
            {
                return true;
            }
        }
        return false;
    }

    private void upgradeHtmlAreaProperty( final PropertySet nodeData, final PathIndexConfig pathIndexConfig )
    {
        final String source = nodeData.getString( pathIndexConfig.getPath() );
        if ( source != null )
        {
            System.out.println( "\nHtml area [" + pathIndexConfig.getPath().toString() + "]: " + source );
            final Matcher contentMatcher = HTML_LINK_PATTERN.matcher( source );

            while ( contentMatcher.find() )
            {
                if ( contentMatcher.groupCount() >= 5 )
                {
                    final String reference = contentMatcher.group( 5 );
                    references.add( reference );
                }
            }
        }
    }
}
