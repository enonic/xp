package com.enonic.xp.core.impl.schema.mixin;

import com.google.common.annotations.Beta;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlFormMapper;
import com.enonic.xp.xml.parser.XmlModelParser;

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

        final XmlFormMapper mapper = new XmlFormMapper( this.currentModule );
        final Form form = mapper.buildForm( root.getChild( "items" ) );
        this.builder.formItems( form.getFormItems() );
    }
}
