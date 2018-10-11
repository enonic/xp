package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorMode;
import com.enonic.xp.xml.DomElement;

@Beta
public final class XmlAuthDescriptorParser
    extends XmlModelParser<XmlAuthDescriptorParser>
{
    private AuthDescriptor.Builder builder;

    public XmlAuthDescriptorParser builder( final AuthDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "id-provider" );

        final AuthDescriptorMode mode = AuthDescriptorMode.valueOf( root.getChildValue( "mode" ) );
        this.builder.mode( mode );

        final XmlFormMapper formMapper = new XmlFormMapper( this.currentApplication );
        this.builder.config( formMapper.buildForm( root.getChild( "form" ) ) );
    }
}
