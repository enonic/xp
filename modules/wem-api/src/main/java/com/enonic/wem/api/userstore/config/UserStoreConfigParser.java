package com.enonic.wem.api.userstore.config;

import java.io.StringReader;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

public final class UserStoreConfigParser
{
    private final SAXBuilder builder;

    public UserStoreConfigParser()
    {
        this.builder = new SAXBuilder();
    }

    public UserStoreConfig parseXml( final String xml )
        throws Exception
    {
        final Document doc = this.builder.build( new StringReader( xml ) );
        return parseXml( doc );
    }

    public UserStoreConfig parseXml( final Document xml )
        throws Exception
    {
        // TODO: Implement parsing of UserStoreConfig
        return null;
    }
}
