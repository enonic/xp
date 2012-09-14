package com.enonic.wem.api.userstore.config;

import org.jdom.Document;
import org.jdom.Element;
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
        Element configEl = new Element( "config" );
        Element userFields = new Element( "user-fields" );
        for ( UserStoreFieldConfig field : config.getFields() )
        {
            Element userFieldEl = buildUserFieldElement( field );

            userFields.addContent( userFieldEl );
        }
        configEl.addContent( userFields );
        return new Document( configEl );
    }

    public String toXmlString( final UserStoreConfig config )
        throws Exception
    {
        final Document doc = toXml( config );
        return this.out.outputString( doc );
    }

    private Element buildUserFieldElement( UserStoreFieldConfig field )
    {
        Element fieldEl = new Element( field.getName() );
        if ( field.isReadOnly() )
        {
            fieldEl.setAttribute( "readonly", String.valueOf( field.isReadOnly() ) );
        }
        if ( field.isRequired() )
        {
            fieldEl.setAttribute( "required", String.valueOf( field.isRequired() ) );
        }
        if ( field.isRequired() )
        {
            fieldEl.setAttribute( "remote", String.valueOf( field.isRemote() ) );
        }
        if ( "address".equals( field.getName() ) && !field.isIso() )
        {
            fieldEl.setAttribute( "iso", String.valueOf( field.isIso() ) );
        }
        return fieldEl;
    }
}
