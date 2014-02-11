package com.enonic.wem.portal.postprocess;

import org.attoparser.AttoParseException;
import org.attoparser.IAttoHandler;
import org.attoparser.markup.CommentMarkupParsingUtil;
import org.attoparser.markup.ICommentHandling;
import org.attoparser.markup.MarkupAttoParser;

import com.enonic.wem.portal.rendering.RenderException;

final class PostProcessEvaluatorImpl
    implements IAttoHandler, ICommentHandling, PostProcessEvaluator
{
    private final StringBuilder result;

    private final PostProcessInstruction[] instructions;

    public PostProcessEvaluatorImpl( final PostProcessInstruction... instructions )
    {
        this.result = new StringBuilder();
        this.instructions = instructions;
    }

    @Override
    public String evaluate( final String input )
    {
        try
        {
            final MarkupAttoParser parser = new MarkupAttoParser();
            parser.parse( input, this );
            return this.result.toString();
        }
        catch ( final AttoParseException e )
        {
            throw new RenderException( "Failed to post process document", e );
        }
    }

    public String execute()
    {
        return this.result.toString();
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
        this.result.append( new String( buffer, offset, len ) );
    }

    @Override
    public void handleStructure( final char[] buffer, final int offset, final int len, final int line, final int col )
        throws AttoParseException
    {
        if ( CommentMarkupParsingUtil.tryParseComment( buffer, offset, len, line, col, this ) )
        {
            return;
        }

        this.result.append( new String( buffer, offset, len ) );
    }

    @Override
    public void handleComment( final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset,
                               final int outerLen, final int line, final int col )
        throws AttoParseException
    {
        if ( ( contentLen < 1 ) || ( buffer[contentOffset] != '#' ) )
        {
            this.result.append( new String( buffer, outerOffset, outerLen ) );
            return;
        }

        final String content = new String( buffer, contentOffset + 1, contentLen - 1 ).trim();
        if ( !tryExecuteInstruction( content ) )
        {
            this.result.append( new String( buffer, outerOffset, outerLen ) );
        }
    }

    private boolean tryExecuteInstruction( final String content )
    {
        for ( final PostProcessInstruction instruction : this.instructions )
        {
            final String result = instruction.evaluate( copy(), content );
            if ( result != null )
            {
                this.result.append( result );
                return true;
            }
        }

        return false;
    }

    private PostProcessEvaluator copy()
    {
        return new PostProcessEvaluatorImpl( this.instructions );
    }
}
