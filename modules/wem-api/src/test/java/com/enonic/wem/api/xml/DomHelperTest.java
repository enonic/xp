package com.enonic.wem.api.xml;

import org.junit.Test;

import static org.junit.Assert.*;

public class DomHelperTest
{
    @Test
    public void newDocumentBuilder()
    {
        assertNotNull( DomHelper.newDocumentBuilder() );
    }

    @Test
    public void newDocument()
    {
        assertNotNull( DomHelper.newDocument() );
    }
}
