package com.enonic.wem.core.module;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.support.serializer.XmlParsingException;
import com.enonic.wem.api.xml.XmlSerializers;
import com.enonic.wem.core.schema.content.serializer.FormItemsXmlSerializer;
import com.enonic.wem.core.support.util.JdomHelper;

public final class ModuleXmlSerializer
{
    private final JdomHelper jdomHelper = new JdomHelper();

    private boolean prettyPrint = true;

    public ModuleXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    public String toString( final Module module )
    {
        return this.jdomHelper.serialize( toJDomDocument( module ), this.prettyPrint );
    }

    public Document toJDomDocument( final Module module )
    {
        final Element typeEl = new Element( "module" );
        generate( module, typeEl );
        return new Document( typeEl );
    }

    private void generate( final Module module, final Element moduleEl )
    {
        moduleEl.addContent( new Element( "display-name" ).setText( module.getDisplayName() ) );
        moduleEl.addContent( new Element( "url" ).setText( module.getUrl() ) );

        Element vendor = new Element( "vendor" );
        vendor.addContent( new Element( "name" ).setText( module.getVendorName() ) );
        vendor.addContent( new Element( "url" ).setText( module.getVendorUrl() ) );
        moduleEl.addContent( vendor );

        Element form = new Element( "form" ).setAttribute( "name", "config" );
        if ( module.getConfig() != null )
        {
            new FormItemsXmlSerializer().serialize( module.getConfig(), form );
        }
        moduleEl.addContent( form );

    }

    public void toModule( final String xml, final ModuleBuilder moduleBuilder )
        throws XmlParsingException
    {
        try
        {
            final Document document = this.jdomHelper.parse( xml );
            parse( document.getRootElement(), moduleBuilder );
        }
        catch ( JDOMException | IOException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
    }

    private void parse( final Element moduleEl, final ModuleBuilder moduleBuilder )
        throws IOException
    {
        final String displayName = moduleEl.getChildText( "display-name" );
        moduleBuilder.
            displayName( displayName ).
            url( moduleEl.getChildText( "url" ) ).
            vendorName( moduleEl.getChild( "vendor" ).getChildText( "name" ) ).
            vendorUrl( moduleEl.getChild( "vendor" ).getChildText( "url" ) );

        // TODO temporary fix, see CMS-3614
//        Iterable<FormItem> formItems = new FormItemsXmlSerializer().parse( moduleEl.getChild( "form" ) );
        Form.Builder form = Form.newForm();
        XmlSerializers.form().parse( new JdomHelper().serialize( moduleEl.getChild( "form" ), true ) ).to( form );

//        for ( FormItem formItem : formItems )
//        {
//            form.addFormItem( formItem );
//        }

        moduleBuilder.config( form.build() );
    }
}
