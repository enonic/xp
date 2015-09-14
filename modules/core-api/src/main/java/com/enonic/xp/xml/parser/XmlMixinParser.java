package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.xml.DomElement;

@Beta
public final class XmlMixinParser
    extends XmlModelParser<XmlMixinParser>
{
    private Mixin.Builder builder;

    public XmlMixinParser builder( final Mixin.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "mixin" );
        this.builder.displayName( root.getChildValue( "display-name" ) );
        this.builder.description( root.getChildValue( "description" ) );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentApplication );
        final Form form = mapper.buildForm( root.getChild( "items" ) );
        this.builder.form( form );
    }
}
