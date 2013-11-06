package com.enonic.wem.core.schema.mixin;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.support.serializer.ParsingException;
import com.enonic.wem.api.support.serializer.SerializingException;
import com.enonic.wem.api.support.serializer.XmlParsingException;
import com.enonic.wem.core.schema.content.serializer.FormItemsXmlSerializer;
import com.enonic.wem.core.support.util.JdomHelper;

import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;

public class MixinXmlSerializer
    implements MixinSerializer
{
    private FormItemsXmlSerializer formItemsSerializer = new FormItemsXmlSerializer();

    private boolean prettyPrint = false;

    private final JdomHelper jdomHelper = new JdomHelper();

    public MixinXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    @Override
    public String toString( final Mixin mixin )
        throws SerializingException
    {
        return this.jdomHelper.serialize( toJDomDocument( mixin ), this.prettyPrint );
    }

    public Document toJDomDocument( Mixin type )
    {
        final Element typeEl = new Element( "mixin" );
        generate( type, typeEl );
        return new Document( typeEl );
    }

    private void generate( final Mixin mixin, final Element typeEl )
    {
        typeEl.addContent( new Element( "name" ).setText( mixin.getName() ) );
        typeEl.addContent( new Element( "display-name" ).setText( mixin.getDisplayName() ) );

        typeEl.addContent( formItemsSerializer.serialize( mixin.getFormItems() ) );
    }

    @Override
    public Mixin toMixin( final String xml )
        throws ParsingException
    {
        try
        {
            final Document document = this.jdomHelper.parse( xml );
            return parse( document.getRootElement() );
        }
        catch ( JDOMException | IOException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
    }

    private Mixin parse( final Element mixinEl )
        throws IOException
    {
        final Mixin.Builder builder = newMixin();
        builder.name( mixinEl.getChildTextTrim( "name" ) );
        builder.displayName( mixinEl.getChildTextTrim( "display-name" ) );

        Iterable<FormItem> formItems = formItemsSerializer.parse( mixinEl.getChild( "items" ) );
        for ( FormItem formItem : formItems )
        {
            builder.addFormItem( formItem );
        }

        return builder.build();
    }
}
