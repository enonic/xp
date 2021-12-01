package com.enonic.xp.xml.parser;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.xml.DomElement;

@PublicApi
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
        this.builder.displayName( root.getChildValueTrimmed( "display-name" ) );
        this.builder.displayNameI18nKey(
            root.getChild( "display-name" ) != null ? root.getChild( "display-name" ).getAttribute( "i18n" ) : null );

        this.builder.description( root.getChildValue( "description" ) );
        this.builder.descriptionI18nKey(
            root.getChild( "description" ) != null ? root.getChild( "description" ).getAttribute( "i18n" ) : null );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentApplication );
        final Form form = mapper.buildForm( root.getChild( "form" ) );
        this.builder.form( form );
    }
}
