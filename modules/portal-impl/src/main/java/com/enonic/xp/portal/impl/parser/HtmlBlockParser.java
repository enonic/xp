package com.enonic.xp.portal.impl.parser;

import org.attoparser.AttoParseException;
import org.attoparser.markup.MarkupAttoParser;

import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.postprocess.HtmlTag;

public final class HtmlBlockParser
{
    private StringBuilder currentBlock;

    private HtmlBlocks.Builder htmlBlocks;

    public HtmlBlockParser()
    {
    }

    public HtmlBlocks parse( final String html )
    {
        this.currentBlock = new StringBuilder();
        this.htmlBlocks = HtmlBlocks.builder();

        try
        {
            final MarkupAttoParser parser = new MarkupAttoParser();
            parser.parse( html, new HtmlBlockParseAttoHandler( this ) );

            addStaticHtml();

            return this.htmlBlocks.build();
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

    void appendHtml( final String html )
    {
        this.currentBlock.append( html );
    }

    void addStaticHtml()
    {
        if ( this.currentBlock.length() == 0 )
        {
            return;
        }
        final StaticHtml block = new StaticHtml( this.currentBlock.toString() );
        this.htmlBlocks.add( block );
        this.currentBlock = new StringBuilder();
    }

    void addInstruction( final String instructionContent )
    {
        addStaticHtml();
        final Instruction block = new Instruction( instructionContent );
        this.htmlBlocks.add( block );
        this.currentBlock = new StringBuilder();
    }

    void addTagMarker( final HtmlTag htmlTag )
    {
        addStaticHtml();
        final TagMarker block = new TagMarker( htmlTag );
        this.htmlBlocks.add( block );
    }

}
