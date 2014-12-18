package com.enonic.wem.admin.status;

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
        // json.put( "newScriptObject", NashornDiag.newScriptObject() );
        // json.put( "newScriptArray", NashornDiag.newScriptArray() );
        // json.put( "getCurrentGlobal", NashornDiag.getCurrentGlobal() );
        // json.put( "findGlobalObject", NashornDiag.findGlobalObject() );
        // json.put( "setCurrentGlobal", NashornDiag.setCurrentGlobal() );
        json.set( "contextDiag", NashornDiag.getOtherInfo() );
    }
}
