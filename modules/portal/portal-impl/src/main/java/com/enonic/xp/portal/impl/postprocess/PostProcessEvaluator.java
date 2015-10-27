package com.enonic.xp.portal.impl.postprocess;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.parser.HtmlBlock;
import com.enonic.xp.portal.impl.parser.HtmlBlockParser;
import com.enonic.xp.portal.impl.parser.HtmlBlocks;
import com.enonic.xp.portal.impl.parser.Instruction;
import com.enonic.xp.portal.impl.parser.StaticHtml;
import com.enonic.xp.portal.impl.parser.TagMarker;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.postprocess.PostProcessInjection;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;

import static java.util.stream.Collectors.joining;

final class PostProcessEvaluator
{
    protected PortalRequest portalRequest;

    protected PortalResponse portalResponse;

    protected String input;

    protected List<PostProcessInstruction> instructions;

    protected List<PostProcessInjection> injections;

    public PostProcessEvaluator()
    {
    }

    public PortalResponse evaluate()
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

        return PortalResponse.create( this.portalResponse ).body( htmlBlocks.toString() ).build();
    }

    public PortalResponse evaluateInstructions()
    {
        HtmlBlocks htmlBlocks = new HtmlBlockParser().parse( this.input );

        while ( htmlBlocks.hasInstructions() )
        {
            htmlBlocks = processInstructions( htmlBlocks );
        }

        return PortalResponse.create( this.portalResponse ).body( htmlBlocks.toString() ).build();
    }

    public PortalResponse evaluateContributions()
    {
        HtmlBlocks htmlBlocks = new HtmlBlockParser().parse( this.input );

        if ( htmlBlocks.hasTagMarkers() )
        {
            htmlBlocks = processContributions( htmlBlocks );
        }

        return PortalResponse.create( this.portalResponse ).body( htmlBlocks.toString() ).build();
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
            final PortalResponse instructionResponse = instruction.evaluate( this.portalRequest, content );
            if ( instructionResponse == null )
            {
                continue;
            }

            final boolean hasHeaders = !instructionResponse.getHeaders().isEmpty();
            final boolean hasCookies = !instructionResponse.getCookies().isEmpty();
            final boolean hasContributions = instructionResponse.hasContributions();
            final boolean skipFilters = !instructionResponse.applyFilters();
            if ( hasContributions || hasHeaders || skipFilters || hasCookies )
            {
                final PortalResponse.Builder newPortalResponse = PortalResponse.create( this.portalResponse );

                if ( hasContributions )
                {
                    newPortalResponse.contributionsFrom( instructionResponse );
                }
                if ( hasHeaders )
                {
                    newPortalResponse.headers( instructionResponse.getHeaders() );
                }
                if ( hasCookies )
                {
                    newPortalResponse.cookies( instructionResponse.getCookies() );
                }
                if ( skipFilters )
                {
                    newPortalResponse.applyFilters( false );
                }

                this.portalResponse = newPortalResponse.build();
            }
            final String resultBody = instructionResponse.getAsString();
            return resultBody == null ? null : new HtmlBlockParser().parse( resultBody );
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
                final HtmlTag htmlTag = ( (TagMarker) htmlBlock ).getTag();
                final StaticHtml injectionHtml = evalPostProcessInjection( htmlTag );
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

    private StaticHtml evalPostProcessInjection( final HtmlTag htmlTag )
    {
        List<String> injections = null;
        for ( final PostProcessInjection injection : this.injections )
        {
            final List<String> contributions = injection.inject( this.portalRequest, this.portalResponse, htmlTag );
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
