package com.enonic.xp.portal.impl.postprocess;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.impl.parser.HtmlBlock;
import com.enonic.xp.portal.impl.parser.HtmlBlockParser;
import com.enonic.xp.portal.impl.parser.HtmlBlocks;
import com.enonic.xp.portal.impl.parser.Instruction;
import com.enonic.xp.portal.impl.parser.StaticHtml;
import com.enonic.xp.portal.impl.parser.TagMarker;
import com.enonic.xp.portal.postprocess.PostProcessInjection;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;

import static java.util.stream.Collectors.joining;

final class PostProcessEvaluator
{
    protected PortalContext context;

    protected String input;

    protected List<PostProcessInstruction> instructions;

    protected List<PostProcessInjection> injections;

    public PostProcessEvaluator()
    {
    }

    public String evaluate()
    {
        HtmlBlocks htmlBlocks = new HtmlBlockParser().parse( this.input );

        while ( htmlBlocks.hasInstructions() )
        {
            htmlBlocks = processInstructions( htmlBlocks );
        }

        if ( htmlBlocks.hasTagMarkers() )
        {
            htmlBlocks = processContributions( htmlBlocks );
        }

        return htmlBlocks.toString();
    }

    private HtmlBlocks processInstructions( final HtmlBlocks htmlBlocks )
    {
        final HtmlBlocks.Builder processedHtmlBlocks = HtmlBlocks.builder();

        for ( HtmlBlock htmlBlock : htmlBlocks )
        {
            if ( isInstruction( htmlBlock ) )
            {
                final String instructionContent = ( (Instruction) htmlBlock ).getValue();
                final HtmlBlocks processedInstruction = executeInstruction( instructionContent );
                if ( processedInstruction != null )
                {
                    processedHtmlBlocks.addAll( processedInstruction );
                }
            }
            else
            {
                processedHtmlBlocks.add( htmlBlock );
            }
        }

        return processedHtmlBlocks.build();
    }

    private HtmlBlocks executeInstruction( final String content )
    {
        for ( final PostProcessInstruction instruction : this.instructions )
        {
            final String result = instruction.evaluate( this.context, content );
            if ( result != null )
            {
                return new HtmlBlockParser().parse( result );
            }
        }
        return null;
    }

    private HtmlBlocks processContributions( final HtmlBlocks htmlBlocks )
    {
        final HtmlBlocks.Builder processedHtmlBlocks = HtmlBlocks.builder();

        for ( HtmlBlock htmlBlock : htmlBlocks )
        {
            if ( isTagMarker( htmlBlock ) )
            {
                final PostProcessInjection.Tag tagMarker = ( (TagMarker) htmlBlock ).getTag();
                final StaticHtml injectionHtml = evalPostProcessInjection( tagMarker );
                if ( injectionHtml != null )
                {
                    processedHtmlBlocks.add( injectionHtml );
                }
            }
            else
            {
                processedHtmlBlocks.add( htmlBlock );
            }
        }

        return processedHtmlBlocks.build();
    }

    private StaticHtml evalPostProcessInjection( final PostProcessInjection.Tag tag )
    {
        List<String> injections = null;
        for ( final PostProcessInjection injection : this.injections )
        {
            final List<String> contributions = injection.inject( this.context, tag );
            if ( contributions != null )
            {
                if ( injections == null )
                {
                    injections = new ArrayList<>();
                }
                injections.addAll( contributions );
            }
        }
        return injections == null ? null : new StaticHtml( injections.stream().map( String::trim ).distinct().collect( joining() ) );
    }

    private boolean isInstruction( final HtmlBlock htmlBlock )
    {
        return htmlBlock instanceof Instruction;
    }

    private boolean isTagMarker( final HtmlBlock htmlBlock )
    {
        return htmlBlock instanceof TagMarker;
    }
}
