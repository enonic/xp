package com.enonic.xp.portal.impl.parser;

import org.attoparser.AbstractMarkupHandler;

import com.enonic.xp.portal.postprocess.HtmlTag;

final class HtmlBlockParseAttoHandler
    extends AbstractMarkupHandler
{
    private final HtmlBlockParser parser;

    private StringBuilder elementBuffer;

    HtmlBlockParseAttoHandler( final HtmlBlockParser parser )
    {
        this.parser = parser;
    }

    @Override
    public void handleText( final char[] buffer, final int offset, final int len, final int line, final int col )
    {
        parser.appendHtml( buffer, offset, len );
    }

    @Override
    public void handleComment( final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset,
                               final int outerLen, final int line, final int col )
    {
        if ( ( contentLen < 1 ) || ( buffer[contentOffset] != '#' ) )
        {
            parser.appendHtml( buffer, outerOffset, outerLen );
            return;
        }

        final String content = new String( buffer, contentOffset + 1, contentLen - 1 ).trim();
        parser.addInstruction( content );
    }

    @Override
    public void handleStandaloneElementStart( final char[] buffer, final int nameOffset, final int nameLen, final boolean minimized,
                                              final int line, final int col )
    {
        elementBuffer = new StringBuilder();
        elementBuffer.append( '<' );
        elementBuffer.append( buffer, nameOffset, nameLen );
    }

    @Override
    public void handleStandaloneElementEnd( final char[] buffer, final int nameOffset, final int nameLen, final boolean minimized,
                                            final int line, final int col )
    {
        if ( minimized )
        {
            elementBuffer.append( "/>" );
        }
        else
        {
            elementBuffer.append( '>' );
        }
        parser.appendHtml( elementBuffer );
        elementBuffer = null;
    }

    @Override
    public void handleOpenElementStart( final char[] buffer, final int nameOffset, final int nameLen, final int line, final int col )
    {
        elementBuffer = new StringBuilder();
        elementBuffer.append( '<' );
        elementBuffer.append( buffer, nameOffset, nameLen );
    }

    @Override
    public void handleOpenElementEnd( final char[] buffer, final int nameOffset, final int nameLen, final int line, final int col )
    {
        elementBuffer.append( '>' );
        parser.appendHtml( elementBuffer );

        if ( isTag( "head", buffer, nameOffset, nameLen ) )
        {
            parser.addTagMarker( HtmlTag.HEAD_BEGIN );
        }
        else if ( isTag( "body", buffer, nameOffset, nameLen ) )
        {
            parser.addTagMarker( HtmlTag.BODY_BEGIN );
        }

        elementBuffer = null;
    }

    @Override
    public void handleCloseElementStart( final char[] buffer, final int nameOffset, final int nameLen, final int line, final int col )
    {
        elementBuffer = new StringBuilder();
        elementBuffer.append( "</" );
        elementBuffer.append( buffer, nameOffset, nameLen );
    }

    @Override
    public void handleCloseElementEnd( final char[] buffer, final int nameOffset, final int nameLen, final int line, final int col )
    {
        elementBuffer.append( '>' );

        if ( isTag( "head", buffer, nameOffset, nameLen ) )
        {
            parser.addTagMarker( HtmlTag.HEAD_END );
        }
        else if ( isTag( "body", buffer, nameOffset, nameLen ) )
        {
            parser.addTagMarker( HtmlTag.BODY_END );
        }

        parser.appendHtml( elementBuffer );
        elementBuffer = null;
    }

    @Override
    public void handleAttribute( final char[] buffer, final int nameOffset, final int nameLen, final int nameLine, final int nameCol,
                                 final int operatorOffset, final int operatorLen, final int operatorLine, final int operatorCol,
                                 final int valueContentOffset, final int valueContentLen, final int valueOuterOffset,
                                 final int valueOuterLen, final int valueLine, final int valueCol )
    {
        if ( elementBuffer != null )
        {
            elementBuffer.append( buffer, nameOffset, nameLen );
            if ( operatorLen > 0 )
            {
                elementBuffer.append( buffer, operatorOffset, operatorLen );
            }
            if ( valueOuterLen > 0 )
            {
                elementBuffer.append( buffer, valueOuterOffset, valueOuterLen );
            }
        }
    }

    @Override
    public void handleInnerWhiteSpace( final char[] buffer, final int offset, final int len, final int line, final int col )
    {
        if ( elementBuffer != null )
        {
            elementBuffer.append( buffer, offset, len );
        }
    }

    @Override
    public void handleDocType( final char[] buffer, final int keywordOffset, final int keywordLen, final int keywordLine,
                               final int keywordCol, final int elementNameOffset, final int elementNameLen, final int elementNameLine,
                               final int elementNameCol, final int typeOffset, final int typeLen, final int typeLine, final int typeCol,
                               final int publicIdOffset, final int publicIdLen, final int publicIdLine, final int publicIdCol,
                               final int systemIdOffset, final int systemIdLen, final int systemIdLine, final int systemIdCol,
                               final int internalSubsetOffset, final int internalSubsetLen, final int internalSubsetLine,
                               final int internalSubsetCol, final int outerOffset, final int outerLen, final int outerLine,
                               final int outerCol )
    {
        parser.appendHtml( buffer, outerOffset, outerLen );
    }

    @Override
    public void handleCDATASection( final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset,
                                    final int outerLen, final int line, final int col )
    {
        parser.appendHtml( buffer, outerOffset, outerLen );
    }

    @Override
    public void handleXmlDeclaration( final char[] buffer, final int keywordOffset, final int keywordLen, final int keywordLine,
                                      final int keywordCol, final int versionOffset, final int versionLen, final int versionLine,
                                      final int versionCol, final int encodingOffset, final int encodingLen, final int encodingLine,
                                      final int encodingCol, final int standaloneOffset, final int standaloneLen, final int standaloneLine,
                                      final int standaloneCol, final int outerOffset, final int outerLen, final int outerLine,
                                      final int outerCol )
    {
        parser.appendHtml( buffer, outerOffset, outerLen );
    }

    @Override
    public void handleProcessingInstruction( final char[] buffer, final int targetOffset, final int targetLen, final int targetLine,
                                             final int targetCol, final int contentOffset, final int contentLen, final int contentLine,
                                             final int contentCol, final int outerOffset, final int outerLen, final int outerLine,
                                             final int outerCol )
    {
        parser.appendHtml( buffer, outerOffset, outerLen );
    }

    private static boolean isTag( final String tag, final char[] buffer, final int offset, final int len )
    {
        return tag.equalsIgnoreCase( new String( buffer, offset, len ) );
    }
}
