package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.xml.DomElement;

@Beta
public class XmlMixinParser<P extends XmlMixinParser<P>>
    extends XmlModelParser<P>
{
    protected Mixin.Builder builder;

    public XmlMixinParser builder( final Mixin.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagNames( root, Lists.newArrayList( "mixin", "x-data" ) );
        this.builder.displayName( root.getChildValue( "display-name" ) );
        this.builder.displayNameI18nKey(
            root.getChild( "display-name" ) != null ? root.getChild( "display-name" ).getAttribute( "i18n" ) : null );

        this.builder.description( root.getChildValue( "description" ) );
        this.builder.descriptionI18nKey(
            root.getChild( "description" ) != null ? root.getChild( "description" ).getAttribute( "i18n" ) : null );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentApplication );
        final Form form = mapper.buildForm( root.getChild( "items" ) );
        this.builder.form( form );
    }
}
