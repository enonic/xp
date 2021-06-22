package com.enonic.xp.script.graaljs.impl.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.script.graaljs.impl.GraalJSContextProviderImpl;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraalJSHelperTest
{
    private Context context;

    @BeforeEach
    public void setUp()
    {
        this.context = new GraalJSContextProviderImpl().getContext();
    }

    @AfterEach
    public void destroy()
    {
        this.context.close();
    }

    @Test
    public void test()
    {
        Value date = context.eval( "js", "var date = new Date(); date;" );
        assertTrue( GraalJSHelper.isDateType( date ) );
        System.out.println(GraalJSHelper.toDate( date ));
    }
}
