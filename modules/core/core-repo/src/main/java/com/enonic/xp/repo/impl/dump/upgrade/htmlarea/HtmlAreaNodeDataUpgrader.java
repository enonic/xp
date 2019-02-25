package com.enonic.xp.repo.impl.dump.upgrade.htmlarea;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyVisitor;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.util.Reference;

public class HtmlAreaNodeDataUpgrader
{
    private static final List<Pattern> BACKWARD_COMPATIBILITY_HTML_PROPERTY_PATH_PATTERNS =
        Stream.of( "x.**", "* data.siteConfig.confg.**", "components.layout.config.*.**", "components.part.config.*.**",
                   "components.page.config.*.**", "components.text.value" ).
            map( HtmlAreaNodeDataUpgrader::toPattern ).
            collect( Collectors.toList() );

    private static final Pattern HTML_LINK_PATTERN =
        Pattern.compile( "(?:href|src)=(\"((content|media|image)://(?:(download|inline)/)?([0-9a-z-/]+)(\\?[^\"]+)?)\")" );

    private static final int HTML_LINK_PATTERN_TYPE_GROUP = 3;

    private static final int HTML_LINK_PATTERN_ID_GROUP = 5;

    private static final Pattern KEEP_SIZE_IMAGE_PATTERN =
        Pattern.compile( "(href|src)=\"image://([0-9a-z-/]+)\\?keepSize=true\"" );

    private static final String PROCESSED_REFERENCES_PROPERTY_NAME = "processedReferences";

    private static final String HTML_STRIPPER_PROCESSOR_NAME = "htmlStripper";

    private static final String STRING_PROPERTY_TYPE_NAME = "String";

    private Set<Reference> references = Sets.newHashSet();

    private HtmlAreaFigureXsltTransformer figureXsltTransformer;

    public HtmlAreaNodeDataUpgrader()
    {
        this.figureXsltTransformer = new HtmlAreaFigureXsltTransformer();
    }

    public boolean upgrade( final NodeVersion nodeVersion, final PatternIndexConfigDocument indexConfigDocument )
    {

        if ( !isContent( nodeVersion ) )
        {
            return false;
        }

        final PropertyTree nodeData = nodeVersion.getData();
        upgradeNodeData( nodeData.getRoot(), indexConfigDocument );

        if ( !references.isEmpty() )
        {
            references.forEach( reference -> nodeData.addReference( PROCESSED_REFERENCES_PROPERTY_NAME, reference ) );
            return true;
        }

        return false;
    }

    private boolean isContent( final NodeVersion nodeVersion )
    {
        return ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.getNodeType() );
    }

    private void upgradeNodeData( final PropertySet nodeData, final PatternIndexConfigDocument indexConfigDocument )
    {
        final List<Pattern> htmlAreaPatterns = indexConfigDocument.getPathIndexConfigs().
            stream().
            filter( this::hasHtmlStripperProcessor ).
            map( PathIndexConfig::getPath ).
            map( PropertyPath::toString ).
            map( HtmlAreaNodeDataUpgrader::toPattern ).
            collect( Collectors.toList() );
        htmlAreaPatterns.addAll( BACKWARD_COMPATIBILITY_HTML_PROPERTY_PATH_PATTERNS );

        final PropertyVisitor propertyVisitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                if ( isHtmlAreaProperty( property ) )
                {
                    upgradeHtmlAreaProperty( property );
                }
            }

            private boolean isHtmlAreaProperty( Property property )
            {
                return htmlAreaPatterns.stream().
                    anyMatch( htmlPattern -> htmlPattern.matcher( property.getPath().toString() ).matches() );
            }
        };
        propertyVisitor.traverse( nodeData );
    }

    private static Pattern toPattern( final String path )
    {
        final String pattern = path.
            replace( ".", "(?:\\[\\d+\\])?." ). //Handle arrays
            replace( "**", ".+" ).
            replace( "*", "[^\\.]+" );
        return Pattern.compile( pattern );
    }

    private boolean hasHtmlStripperProcessor( final PathIndexConfig pathIndexConfig )
    {
        for ( IndexValueProcessor indexValueProcessor : pathIndexConfig.getIndexConfig().getIndexValueProcessors() )
        {
            if ( HTML_STRIPPER_PROCESSOR_NAME.equals( indexValueProcessor.getName() ) )
            {
                return true;
            }
        }
        return false;
    }

    private void upgradeHtmlAreaProperty( final Property property )
    {
        if ( STRING_PROPERTY_TYPE_NAME.equals( property.getType().getName() ) )
        {
            String value = property.getString();
            if ( value != null )
            {
                final Matcher contentMatcher = HTML_LINK_PATTERN.matcher( value );
                boolean containsHtmlAreaImage = false;
                while ( contentMatcher.find() )
                {
                    if ( contentMatcher.groupCount() >= HTML_LINK_PATTERN_ID_GROUP )
                    {
                        if ( "image".equals( contentMatcher.group( HTML_LINK_PATTERN_TYPE_GROUP ) ) )
                        {
                            containsHtmlAreaImage = true;
                        }
                        final String reference = contentMatcher.group( HTML_LINK_PATTERN_ID_GROUP );
                        references.add( Reference.from( reference ) );
                    }
                }

                if ( containsHtmlAreaImage )
                {
                    upgradePropertyValue( property );
                }
            }
        }
    }

    private void upgradePropertyValue( final Property property )
    {
        String upgradedValue = upgradeKeepSizeImages( property.getString() );
        upgradedValue = upgradeFigures( upgradedValue );
        property.setValue( ValueFactory.newString( upgradedValue ) );
    }

    private String upgradeKeepSizeImages( final String value )
    {
        final Matcher matcher = KEEP_SIZE_IMAGE_PATTERN.matcher( value );
        matcher.reset();
        boolean result = matcher.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                final String attributeName = matcher.group( 1 );
                final String contentId = matcher.group( 2 );
                final String attachmentLinkEquivalent = attributeName + "=\"media://" + contentId + "\"";

                matcher.appendReplacement(sb, attachmentLinkEquivalent);
                result = matcher.find();
            } while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return value;
    }

    private String upgradeFigures( final String value )
    {
        return figureXsltTransformer.transform( value );
    }
}
