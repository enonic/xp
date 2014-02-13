package com.enonic.wem.portal.postprocess;

import java.util.Set;

import org.attoparser.AttoParseException;
import org.attoparser.IAttoHandler;
import org.attoparser.markup.CommentMarkupParsingUtil;
import org.attoparser.markup.ICommentHandling;
import org.attoparser.markup.MarkupAttoParser;

import com.google.common.collect.Sets;

import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.postprocess.inject.PostProcessInjection;
import com.enonic.wem.portal.postprocess.instruction.PostProcessInstruction;
import com.enonic.wem.portal.rendering.RenderException;

final class PostProcessEvaluator
    implements IAttoHandler, ICommentHandling
{
    private final StringBuilder result;

    protected JsContext context;

    protected String input;

    protected Set<PostProcessInstruction> instructions;

    protected Set<PostProcessInjection> injections;

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
            final String result = processInstruction( instruction, content );
            if ( result != null )
            {
                this.result.append( result );
                return true;
            }
        }

        return false;
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
        evaluator.injections = Sets.newHashSet();
        evaluator.instructions = this.instructions;
        evaluator.input = result;

        return evaluator.evaluate();
    }
}
