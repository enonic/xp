package com.enonic.xp.repo.impl.dump.upgrade.htmlarea;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.google.common.io.Resources;

public class HtmlAreaFigureXsltTransformerTest
{

    @Test
    public void test()
        throws IOException
    {
        final URL resource = getClass().getResource( "/com/enonic/xp/repo/impl/dump/upgrade/htmlarea/figure.xml" );
        final String transformed = new HtmlAreaFigureXsltTransformer().
            transform( Resources.toString( resource, StandardCharsets.UTF_8 ) );
        System.out.println( transformed );
    }
}
