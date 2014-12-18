package com.enonic.wem.script;

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
}
