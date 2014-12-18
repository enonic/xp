package com.enonic.wem.script;

import java.lang.reflect.Method;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jdk.nashorn.internal.runtime.Context;

import com.enonic.wem.script.internal.nashorn.NashornHelper;

public final class NashornDiag
{
    public static String getScriptEngine()
    {
        return NashornHelper.getScriptEngine().toString();
    }

    public static String newScriptObject()
    {
        try
        {
            NashornHelper.setCurrentGlobal( NashornHelper.findGlobalObject( null ) );
            return NashornHelper.newScriptObject().toString();
        }
        finally
        {
            NashornHelper.setCurrentGlobal( null );
        }
    }

    public static String newScriptArray()
    {
        try
        {
            NashornHelper.setCurrentGlobal( NashornHelper.findGlobalObject( null ) );
            return NashornHelper.newScriptArray().toString();
        }
        finally
        {
            NashornHelper.setCurrentGlobal( null );
        }
    }

    public static String getCurrentGlobal()
    {
        final Object value = NashornHelper.getCurrentGlobal();
        return value != null ? value.toString() : null;
    }

    public static String findGlobalObject()
    {
        final Object value = NashornHelper.findGlobalObject( null );
        return value != null ? value.toString() : null;
    }

    public static String setCurrentGlobal()
    {
        NashornHelper.setCurrentGlobal( NashornHelper.findGlobalObject( null ) );
        final Object value = getCurrentGlobal();
        NashornHelper.setCurrentGlobal( null );
        return value != null ? value.toString() : null;
    }

    public static JsonNode getOtherInfo()
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put( "class", Context.class.getName() );

        final ArrayNode methods = node.putArray( "methods" );
        for ( final Method method : Context.class.getMethods() )
        {
            methods.add( method.toString() );
        }

        final ObjectNode location = node.putObject( "location" );
        final ProtectionDomain pd = Context.class.getProtectionDomain();
        if ( pd != null )
        {
            location.put( "protectionDomain", pd.toString() );
            location.put( "classLoader", pd.getClassLoader().toString() );

            final CodeSource cd = pd.getCodeSource();
            location.put( "codeSource", cd != null ? cd.toString() : null );
            location.put( "codeLocation", cd != null ? cd.getLocation().toString() : null );
        }

        return node;
    }
}
