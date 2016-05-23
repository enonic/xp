package com.enonic.xp.portal.impl.postprocess.instruction;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.macro.MacroProcessorScriptFactory;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.site.Site;

@Component(immediate = true)
public final class MacroInstruction
    implements PostProcessInstruction
{

    private static final String MACRO_BODY = "_body";

    private static final String MACRO_NAME = "_name";

    private MacroProcessorScriptFactory macroScriptFactory;

    private MacroDescriptorService macroDescriptorService;

    private PortalUrlService portalUrlService;

    @Override
    public PortalResponse evaluate( final PortalRequest portalRequest, final String instruction )
    {
        if ( !Instruction.isInstruction( instruction, "MACRO" ) )
        {
            return null;
        }

        // parse instruction
        final Instruction macroInstruction;
        try
        {
            macroInstruction = new InstructionParser().parse( instruction );
        }
        catch ( RenderException e )
        {
            return null;
        }

        final String macroName = macroInstruction.attribute( MACRO_NAME );
        if ( macroName == null )
        {
            return null;
        }

        // resolve macro processor
        final Site site = portalRequest.getSite();
        if ( site == null )
        {
            throw new RenderException( "Macro script could not be resolved, context site could not be found." );
        }

        final MacroDescriptor macroDescriptor = resolveMacroDescriptor( site, macroName );
        final MacroProcessor macroProcessor = resolveMacroProcessor( macroDescriptor );
        if ( macroProcessor == null )
        {
            throw new RenderException( "Macro script not found: " + macroName );
        }

        // execute macro
        final MacroContext context = createContext( macroInstruction, portalRequest );
        return macroProcessor.process( context );
    }

    private MacroDescriptor resolveMacroDescriptor( final Site site, final String macroName )
    {
        //Searches for the macro in the applications associated to the site
        MacroDescriptor macroDescriptor = site.getSiteConfigs().
            stream().
            map( siteConfig -> MacroKey.from( siteConfig.getApplicationKey(), macroName ) ).
            map( macroDescriptorService::getByKey ).
            filter( Objects::nonNull ).findFirst().
            orElse( null );

        //If there is no corresponding macro
        if ( macroDescriptor == null )
        {
            //Searches in the builtin macros
            final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, macroName );
            macroDescriptor = macroDescriptorService.getByKey( macroKey );
        }

        return macroDescriptor;
    }

    private MacroProcessor resolveMacroProcessor( MacroDescriptor macroDescriptor )
    {
        if ( macroDescriptor != null )
        {
            return macroScriptFactory.fromScript( macroDescriptor.toControllerResourceKey() );
        }
        return null;
    }

    private MacroContext createContext( final Instruction macroInstruction, final PortalRequest request )
    {
        final MacroContext.Builder context = MacroContext.create().name( macroInstruction.attribute( MACRO_NAME ) );
        for ( String name : macroInstruction.attributeNames() )
        {
            if ( name.equals( MACRO_BODY ) || name.equals( MACRO_NAME ) )
            {
                continue;
            }
            context.param( name, macroInstruction.attribute( name, "" ) );
        }
        context.body( macroInstruction.attribute( MACRO_BODY ) );
        context.request( request );
        context.systemAssetsBaseUri( this.getSystemAssetsBaseUri( request ) );
        return context.build();
    }

    private String getSystemAssetsBaseUri( final PortalRequest portalRequest )
    {
        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( portalRequest ).
            application( "com.enonic.xp.app.system" ).
            path( "" );

        return this.portalUrlService.assetUrl( params );
    }

    @Reference
    public void setMacroScriptFactory( final MacroProcessorScriptFactory macroScriptFactory )
    {
        this.macroScriptFactory = macroScriptFactory;
    }

    @Reference
    public void setMacroDescriptorService( final MacroDescriptorService macroDescriptorService )
    {
        this.macroDescriptorService = macroDescriptorService;
    }

    @Reference
    public void setPortalUrlService( final PortalUrlService portalUrlService )
    {
        this.portalUrlService = portalUrlService;
    }
}
