package com.enonic.xp.repo.impl.dump.upgrade.htmlarea;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyVisitor;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.util.Reference;

public class HtmlAreaNodeDataUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( HtmlAreaNodeDataUpgrader.class );

    private static final List<Pattern> BACKWARD_COMPATIBILITY_HTML_PROPERTY_PATH_PATTERNS =
        Stream.of( "x.**", "data.**", "components.layout.config.*.**", "components.part.config.*.**", "components.page.config.*.**",
                   "components.text.value" ).
            map( HtmlAreaNodeDataUpgrader::toPattern ).
            collect( Collectors.toList() );

    private static final Pattern HTML_LINK_PATTERN =
        Pattern.compile( "(?:href|src)=(\"((content|media|image)://(?:(download|inline)/)?([0-9a-z-/]+)(\\?[^\"]+)?)\")" );

    private static final int HTML_LINK_PATTERN_TYPE_GROUP = 3;

    private static final int HTML_LINK_PATTERN_ID_GROUP = 5;

    private static final Pattern KEEP_SIZE_IMAGE_PATTERN = Pattern.compile( "(href|src)=\"image://([0-9a-z-/]+)\\?keepSize=true\"" );

    private static final Pattern FIGURE_PATTERN =
        Pattern.compile( "<figure(?:\\s+([^>]+))?>(.*?)<\\/figure>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );

    private static final Pattern CLASS_VALUE_PATTERN = Pattern.compile( "class\\s*=\\s*\"([^\"]*)\"" );

    private static final Pattern STYLE_VALUE_PATTERN = Pattern.compile( "style\\s*=\\s*\"([^\"]*)\"" );

    private static final String PROCESSED_REFERENCES_PROPERTY_NAME = "processedReferences";

    private static final String HTML_STRIPPER_PROCESSOR_NAME = "htmlStripper";

    private static final String STRING_PROPERTY_TYPE_NAME = "String";

    private Set<Reference> references;

    private NodeVersion nodeVersion;

    private DumpUpgradeStepResult.Builder result;

    public boolean upgrade( final NodeVersion nodeVersion, final PatternIndexConfigDocument indexConfigDocument,
                            DumpUpgradeStepResult.Builder result )
    {
        references = new HashSet<>();
        this.nodeVersion = nodeVersion;

        this.result = result;

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
            filter( this::hasHtmlStripperProcessor ).map( PathIndexConfig::getIndexPath )
            .map( IndexPath::toString )
            .map( HtmlAreaNodeDataUpgrader::toPattern )
            .toList();

        final PropertyVisitor propertyVisitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                if ( isHtmlAreaProperty( property ) )
                {
                    upgradeHtmlAreaProperty( property, false );
                }
                else if ( isBackwardCompatibleHtmlAreaProperty( property ) )
                {
                    upgradeHtmlAreaProperty( property, true );
                }
            }

            private boolean isHtmlAreaProperty( Property property )
            {
                return ValueTypes.STRING.equals( property.getType() ) && htmlAreaPatterns.stream().
                    anyMatch( htmlPattern -> htmlPattern.matcher( property.getPath().toString() ).matches() );
            }

            private boolean isBackwardCompatibleHtmlAreaProperty( Property property )
            {
                return ValueTypes.STRING.equals( property.getType() ) && BACKWARD_COMPATIBILITY_HTML_PROPERTY_PATH_PATTERNS.stream().
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

    private void upgradeHtmlAreaProperty( final Property property, final boolean backwardCompatible )
    {
        if ( STRING_PROPERTY_TYPE_NAME.equals( property.getType().getName() ) )
        {
            String value = property.getString();
            if ( value != null )
            {
                final Matcher contentMatcher = HTML_LINK_PATTERN.matcher( value );
                boolean containsHtmlLink = false;
                boolean containsHtmlAreaImage = false;
                while ( contentMatcher.find() )
                {
                    containsHtmlLink = true;
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

                if ( containsHtmlLink && backwardCompatible )
                {
                    LOG.info( "Property [{}] in node [{}] contains HTML Area links but is not indexed as an HTML Area input. Treating as an HTML Area",
                              property.getPath(), nodeVersion.getId() );
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
        upgradedValue = upgradeFigures( property.getName(), upgradedValue );
        property.setValue( ValueFactory.newString( upgradedValue ) );
    }

    private String upgradeKeepSizeImages( final String value )
    {
        final Matcher matcher = KEEP_SIZE_IMAGE_PATTERN.matcher( value );
        matcher.reset();
        boolean result = matcher.find();
        if ( result )
        {
            StringBuffer sb = new StringBuffer();
            do
            {
                final String attributeName = matcher.group( 1 );
                final String contentId = matcher.group( 2 );
                final String attachmentLinkEquivalent = attributeName + "=\"media://" + contentId + "\"";

                matcher.appendReplacement( sb, attachmentLinkEquivalent );
                result = matcher.find();
            }
            while ( result );
            matcher.appendTail( sb );
            return sb.toString();
        }
        return value;
    }

    private String upgradeFigures( final String propertyName, final String value )
    {
        //For each figure
        final Matcher matcher = FIGURE_PATTERN.matcher( value );
        matcher.reset();
        boolean result = matcher.find();
        if ( result )
        {
            StringBuffer sb = new StringBuffer();
            do
            {
                //Retrieves attributes and content
                final String figureElement = matcher.group( 0 );
                String attributes = matcher.group( 1 );
                if ( attributes == null )
                {
                    attributes = "";
                }
                final String figureContent = matcher.group( 2 );

                //Retrieves class and style values and if it contains an image with a media URL
                String oldClassValue = null;
                String oldStyleValue = null;
                final Matcher classMatcher = CLASS_VALUE_PATTERN.matcher( attributes );
                if ( classMatcher.find() )
                {
                    oldClassValue = classMatcher.group( 1 );
                }
                final Matcher styleMatcher = STYLE_VALUE_PATTERN.matcher( attributes );
                if ( styleMatcher.find() )
                {
                    oldStyleValue = styleMatcher.group( 1 );
                }
                final boolean containsMediaUrl = figureContent.contains( "=\"media://" );

                //Generates the new style value
                String newStyleValue = null;
                String newClassValue = null;
                if ( oldStyleValue != null && oldStyleValue.startsWith( "float:left" ) )
                {
                    newStyleValue = "float: left; width: 40%;";
                    newClassValue = "editor-align-left";
                }
                else if ( oldStyleValue != null && oldStyleValue.startsWith( "float:right" ) )
                {
                    newStyleValue = "float: right; width: 40%;";
                    newClassValue = "editor-align-right";
                }
                else if ( oldStyleValue != null && oldStyleValue.startsWith( "float:none" ) )
                {
                    newStyleValue = "margin: auto; width: 60%;";
                    newClassValue = "editor-align-center";
                }
                else if ( "justify".equals( oldClassValue ) )
                {
                    newClassValue = "editor-align-justify";
                }
                if ( containsMediaUrl )
                {
                    newClassValue = ( newClassValue == null ? "" : newClassValue + " " ) + "editor-style-original";
                }
                final String newStyleKeyValue = newStyleValue == null ? "" : "style=\"" + newStyleValue + "\"";
                final String newClassKeyValue = newClassValue == null ? "" : "class=\"" + newClassValue + "\"";

                //Adds or replace the style and class value
                if ( oldStyleValue == null )
                {
                    if ( !newStyleKeyValue.isEmpty() )
                    {
                        attributes = ( attributes.isEmpty() ? "" : attributes + " " ) + newStyleKeyValue;
                    }
                }
                else
                {
                    attributes = attributes.replace( styleMatcher.group( 0 ), newStyleKeyValue );
                }
                if ( oldClassValue == null )
                {
                    if ( !newClassKeyValue.isEmpty() )
                    {
                        attributes = ( attributes.isEmpty() ? "" : attributes + " " ) + newClassKeyValue;
                    }
                }
                else
                {
                    attributes = attributes.replace( classMatcher.group( 0 ), newClassKeyValue );
                }

                final String newValue = "<figure" + ( attributes.isEmpty() ? "" : " " + attributes ) + ">" + figureContent + "</figure>";

                matcher.appendReplacement( sb, newValue );
                result = matcher.find();
            }
            while ( result );
            matcher.appendTail( sb );
            return sb.toString();
        }
        return value;
    }
}
