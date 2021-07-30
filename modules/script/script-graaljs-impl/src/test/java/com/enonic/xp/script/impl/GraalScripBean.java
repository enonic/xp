package com.enonic.xp.script.impl;

import java.util.List;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class GraalScripBean
    implements ScriptBean
{
    String service;

    private String source;

    @Override
    public void initialize( final BeanContext context )
    {
        this.service = "custom_service";
        System.out.println( "Initializing script bean" );
    }

    public void setSource( String source )
    {
        this.source = source;
    }

    public void execute()
    {
        System.out.println( "Executing script bean " + service );
        System.out.println( "Source: " + source );
    }

    public void execute( int fd )
    {
        System.out.println( "Executing script bean " + fd );
    }

    public void execute( List<Integer> values )
    {
        System.out.println( "Executing script bean " + values );
    }

    public void execute( String param1, Integer param2 )
    {
        System.out.println( "Executing script bean " + param1 + " --- " + param2 );
    }
}
