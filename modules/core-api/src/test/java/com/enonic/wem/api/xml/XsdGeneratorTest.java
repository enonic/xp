package com.enonic.wem.api.xml;

import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import junit.framework.Assert;

public class XsdGeneratorTest
{
    @Test
    @Ignore
    public void testGeneratedXsd()
        throws Exception
    {
        final String actualXsd = XsdGenerator.generateXsd();

        final URL url = getClass().getResource( "schema/model.xsd" );
        final String storedXsd = Resources.toString( url, Charsets.UTF_8 );

        Assert.assertEquals( "Stored XSD is different than generated XSD. Have you remembered to execute XsdGenerator?", storedXsd,
                             actualXsd );
    }
}
