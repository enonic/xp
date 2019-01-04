package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.auth.IdProviderDescriptor;
import com.enonic.xp.auth.IdProviderDescriptorMode;
import com.enonic.xp.xml.DomElement;

@Beta
public final class XmlIdProviderDescriptorParser
    extends XmlModelParser<XmlIdProviderDescriptorParser>
{
    private IdProviderDescriptor.Builder builder;

    public XmlIdProviderDescriptorParser builder( final IdProviderDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "id-provider" );

        final IdProviderDescriptorMode mode = IdProviderDescriptorMode.valueOf( root.getChildValue( "mode" ) );
        this.builder.mode( mode );

        final XmlFormMapper formMapper = new XmlFormMapper( this.currentApplication );
        this.builder.config( formMapper.buildForm( root.getChild( "form" ) ) );
    }
}
