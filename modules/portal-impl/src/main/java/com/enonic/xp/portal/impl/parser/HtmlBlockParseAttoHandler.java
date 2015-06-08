package com.enonic.xp.portal.impl.parser;

import org.attoparser.AttoParseException;
import org.attoparser.IAttoHandler;
import org.attoparser.markup.CommentMarkupParsingUtil;
import org.attoparser.markup.ElementMarkupParsingUtil;
import org.attoparser.markup.IBasicElementHandling;
import org.attoparser.markup.ICommentHandling;

import com.enonic.xp.portal.postprocess.PostProcessInjection;

final class HtmlBlockParseAttoHandler
    implements IAttoHandler, ICommentHandling, IBasicElementHandling
{
    private final HtmlBlockParser parser;

    HtmlBlockParseAttoHandler( final HtmlBlockParser parser )
    {
        this.parser = parser;
    }

    @Override
    public void handleDocumentStart( final int line, final int col )
        throws AttoParseException
    {
        // Do nothing
    }

    @Override
    public void handleDocumentEnd( final int line, final int col )
        throws AttoParseException
    {
        // Do nothing
    }

    @Override
    public void handleText( final char[] buffer, final int offset, final int len, final int line, final int col )
        throws AttoParseException
    {
        parser.appendHtml( new String( buffer, offset, len ) );
    }

    @Override
    public void handleStructure( final char[] buffer, final int offset, final int len, final int line, final int col )
        throws AttoParseException
    {
        if ( CommentMarkupParsingUtil.tryParseComment( buffer, offset, len, line, col, this ) )
        {
            return;
        }

        if ( ElementMarkupParsingUtil.tryParseElement( buffer, offset, len, line, col, this ) )
        {
            return;
        }

        parser.appendHtml( new String( buffer, offset, len ) );
    }

    @Override
    public void handleComment( final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset,
                               final int outerLen, final int line, final int col )
        throws AttoParseException
    {
        if ( ( contentLen < 1 ) || ( buffer[contentOffset] != '#' ) )
        {
            parser.appendHtml( new String( buffer, outerOffset, outerLen ) );
            return;
        }

        final String content = new String( buffer, contentOffset + 1, contentLen - 1 ).trim();
        parser.addInstruction( content );
    }

    @Override
    public void handleStandaloneElement( final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset,
                                         final int outerLen, final int line, final int col )
        throws AttoParseException
    {
        parser.appendHtml( new String( buffer, outerOffset, outerLen ) );
    }

    @Override
    public void handleOpenElement( final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset,
                                   final int outerLen, final int line, final int col )
        throws AttoParseException
    {
        parser.appendHtml( new String( buffer, outerOffset, outerLen ) );

        if ( isHeadElement( buffer, contentOffset, contentLen ) )
        {
            parser.addTagMarker( PostProcessInjection.Tag.HEAD_BEGIN );
        }
        else if ( isBodyElement( buffer, contentOffset, contentLen ) )
        {
            parser.addTagMarker( PostProcessInjection.Tag.BODY_BEGIN );
        }
    }

    @Override
    public void handleCloseElement( final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset,
                                    final int outerLen, final int line, final int col )
        throws AttoParseException
    {
        if ( isHeadElement( buffer, contentOffset, contentLen ) )
        {
            parser.addTagMarker( PostProcessInjection.Tag.HEAD_END );
        }
        else if ( isBodyElement( buffer, contentOffset, contentLen ) )
        {
            parser.addTagMarker( PostProcessInjection.Tag.BODY_END );
        }

        parser.appendHtml( new String( buffer, outerOffset, outerLen ) );
    }

    private boolean isTag( final String tag, final char[] buffer, final int offset, final int len )
    {
        if ( len < tag.length() )
        {
            return false;
        }

        final String str = new String( buffer, offset, tag.length() );
        if ( len == tag.length() )
        {
            return str.equalsIgnoreCase( tag );
        }

        return str.equalsIgnoreCase( tag ) && ( buffer[offset + tag.length()] == ' ' );
    }

    private boolean isHeadElement( final char[] buffer, final int offset, final int len )
    {
        return isTag( "head", buffer, offset, len );
    }

    private boolean isBodyElement( final char[] buffer, final int offset, final int len )
    {
        return isTag( "body", buffer, offset, len );
    }

}
