package com.enonic.xp.portal.impl.parser;

import java.util.ArrayList;
import java.util.List;

import org.attoparser.AttoParseException;
import org.attoparser.markup.MarkupAttoParser;

import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.postprocess.PostProcessInjection;

public final class HtmlBlockParser
{
    private StringBuilder currentBlock;

    private final List<HtmlBlock> htmlBlocks;

    public HtmlBlockParser()
    {
        this.htmlBlocks = new ArrayList<>();
    }

    public HtmlBlocks parse( final String html )
    {
        this.currentBlock = new StringBuilder();
        this.htmlBlocks.clear();

        try
        {
            final MarkupAttoParser parser = new MarkupAttoParser();
            parser.parse( html, new HtmlBlockParseAttoHandler( this ) );

            addStaticHtml();

            return HtmlBlocks.from( this.htmlBlocks );
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

    void addTagMarker( final PostProcessInjection.Tag tagMarker )
    {
        addStaticHtml();
        final TagMarker block = new TagMarker( tagMarker );
        this.htmlBlocks.add( block );
    }

}
