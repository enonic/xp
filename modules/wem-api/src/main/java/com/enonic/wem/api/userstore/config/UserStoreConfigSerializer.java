package com.enonic.wem.api.userstore.config;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public final class UserStoreConfigSerializer
{
    private final XMLOutputter out;

    public UserStoreConfigSerializer()
    {
        this.out = new XMLOutputter();
        this.out.setFormat( Format.getCompactFormat() );
    }

    public Document toXml( final UserStoreConfig config )
        throws Exception
    {
        // TODO: Implement serializing of UserStoreConfig
        return null;
    }

    public String toXmlString( final UserStoreConfig config )
        throws Exception
    {
        final Document doc = toXml( config );
        return this.out.outputString( doc );
    }
}
