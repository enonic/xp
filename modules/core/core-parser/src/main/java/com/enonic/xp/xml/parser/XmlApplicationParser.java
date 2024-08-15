package com.enonic.xp.xml.parser;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.xml.DomElement;

public final class XmlApplicationParser
    extends XmlModelParser<XmlApplicationParser>
{
    private static final String ROOT_TAG_NAME = "application";

    private static final String DESCRIPTION = "description";

    private ApplicationDescriptor.Builder appDescriptorBuilder;

    public XmlApplicationParser appDescriptorBuilder( final ApplicationDescriptor.Builder appDescriptorBuilder )
    {
        this.appDescriptorBuilder = appDescriptorBuilder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, ROOT_TAG_NAME );
        this.appDescriptorBuilder.key( currentApplication );
        final DomElement descriptionElement = root.getChild( DESCRIPTION );
        if ( descriptionElement != null )
        {
            this.appDescriptorBuilder.description( descriptionElement.getValue() );
        }
    }

}
