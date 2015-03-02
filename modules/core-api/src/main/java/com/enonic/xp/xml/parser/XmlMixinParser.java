package com.enonic.xp.xml.parser;

import org.w3c.dom.Element;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.xml.DomHelper;

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
    protected void doParse( final Element root )
        throws Exception
    {
        assertTagName( root, "mixin" );
        this.builder.displayName( DomHelper.getChildElementValueByTagName( root, "display-name" ) );
        this.builder.description( DomHelper.getChildElementValueByTagName( root, "description" ) );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentModule );
        final Form form = mapper.buildForm( DomHelper.getChildElementByTagName( root, "items" ) );
        this.builder.formItems( form.getFormItems() );
    }
}
