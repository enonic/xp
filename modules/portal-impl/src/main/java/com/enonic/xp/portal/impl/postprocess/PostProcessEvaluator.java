package com.enonic.xp.portal.impl.postprocess;

import java.util.List;

import org.attoparser.AttoParseException;
import org.attoparser.IAttoHandler;
import org.attoparser.markup.CommentMarkupParsingUtil;
import org.attoparser.markup.ElementMarkupParsingUtil;
import org.attoparser.markup.IBasicElementHandling;
import org.attoparser.markup.ICommentHandling;
import org.attoparser.markup.MarkupAttoParser;

import com.google.common.collect.Lists;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.postprocess.PostProcessInjection;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;

final class PostProcessEvaluator
    implements IAttoHandler, ICommentHandling, IBasicElementHandling
{
    private final StringBuilder result;

    protected PortalContext context;

    protected String input;

    protected List<PostProcessInstruction> instructions;

    protected List<PostProcessInjection> injections;

    public PostProcessEvaluator()
    {
        this.result = new StringBuilder();
    }

    public String evaluate()
    {
        try
        {
            final MarkupAttoParser parser = new MarkupAttoParser();
            parser.parse( this.input, this );
            return this.result.toString();
        }
        catch ( final AttoParseException e )
        {
            final Throwable cause = e.getCause();
            if ( cause instanceof RuntimeException )
            {
                throw (RuntimeException) cause;
            }

            throw new RenderException( "Failed to post process document", cause != null ? cause : e );
        }
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

        if ( ElementMarkupParsingUtil.tryParseElement( buffer, offset, len, line, col, this ) )
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
        tryExecuteInstruction( content );
    }

    private void tryExecuteInstruction( final String content )
    {
        for ( final PostProcessInstruction instruction : this.instructions )
        {
            final String result = processInstruction( instruction, content );
            if ( result != null )
            {
                this.result.append( result );
                return;
            }
        }
    }

    private String processInstruction( final PostProcessInstruction instruction, final String content )
    {
        final String result = instruction.evaluate( this.context, content );
        if ( result == null )
        {
            return null;
        }

        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.context = this.context;
        evaluator.injections = Lists.newArrayList();
        evaluator.instructions = this.instructions;
        evaluator.input = result;

        return evaluator.evaluate();
    }

    @Override
    public void handleStandaloneElement( final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset,
                                         final int outerLen, final int line, final int col )
        throws AttoParseException
    {
        this.result.append( new String( buffer, outerOffset, outerLen ) );
    }

    @Override
    public void handleOpenElement( final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset,
                                   final int outerLen, final int line, final int col )
        throws AttoParseException
    {
        this.result.append( new String( buffer, outerOffset, outerLen ) );

        if ( isHeadElement( buffer, contentOffset, contentLen ) )
        {
            injectHtml( PostProcessInjection.Tag.HEAD_BEGIN );
        }

        if ( isBodyElement( buffer, contentOffset, contentLen ) )
        {
            injectHtml( PostProcessInjection.Tag.BODY_BEGIN );
        }
    }

    @Override
    public void handleCloseElement( final char[] buffer, final int contentOffset, final int contentLen, final int outerOffset,
                                    final int outerLen, final int line, final int col )
        throws AttoParseException
    {
        if ( isHeadElement( buffer, contentOffset, contentLen ) )
        {
            injectHtml( PostProcessInjection.Tag.HEAD_END );
        }

        if ( isBodyElement( buffer, contentOffset, contentLen ) )
        {
            injectHtml( PostProcessInjection.Tag.BODY_END );
        }

        this.result.append( new String( buffer, outerOffset, outerLen ) );
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

    private void injectHtml( final PostProcessInjection.Tag tag )
    {
        for ( final PostProcessInjection injection : this.injections )
        {
            final String html = injection.inject( this.context, tag );
            if ( html != null )
            {
                this.result.append( html );
            }
        }
    }
}
