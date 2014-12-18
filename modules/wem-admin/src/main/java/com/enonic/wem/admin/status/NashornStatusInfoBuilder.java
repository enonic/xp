package com.enonic.wem.admin.status;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.script.NashornDiag;

public final class NashornStatusInfoBuilder
    extends StatusInfoBuilder
{
    public NashornStatusInfoBuilder()
    {
        super( "nashorn" );
    }

    @Override
    public void build( final ObjectNode json )
    {
        json.put( "scriptEngine", NashornDiag.getScriptEngine() );

        try
        {
            json.put( "newScriptObject", NashornDiag.newScriptObject() );
            json.put( "newScriptArray", NashornDiag.newScriptArray() );
            json.put( "getCurrentGlobal", NashornDiag.getCurrentGlobal() );
            json.put( "findGlobalObject", NashornDiag.findGlobalObject() );
            json.put( "setCurrentGlobal", NashornDiag.setCurrentGlobal() );
        }
        catch ( final Exception e )
        {
            final StringWriter out = new StringWriter();
            e.printStackTrace( new PrintWriter( out ) );
            json.put( "error", out.toString() );
        }

        json.set( "contextDiag", NashornDiag.getOtherInfo() );
    }
}
