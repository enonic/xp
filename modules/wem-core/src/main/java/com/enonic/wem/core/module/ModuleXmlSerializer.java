package com.enonic.wem.core.module;

import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.support.serializer.XmlParsingException;
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
        moduleEl.addContent( new Element( "info" ).setText( module.getInfo() ) );
        moduleEl.addContent( new Element( "url" ).setText( module.getUrl() ) );

        Element vendor = new Element( "vendor" );
        vendor.addContent( new Element( "name" ).setText( module.getVendorName() ) );
        vendor.addContent( new Element( "url" ).setText( module.getVendorUrl() ) );
        moduleEl.addContent( vendor );

        Element dependencies = new Element( "dependencies" );
        dependencies.setAttribute( "system-min-version", String.valueOf( module.getMinSystemVersion() ) );
        dependencies.setAttribute( "system-max-version", String.valueOf( module.getMaxSystemVersion() ) );

        for ( ModuleKey moduleKey : module.getModuleDependencies() )
        {
            dependencies.addContent( new Element( "module" ).setText( moduleKey.toString() ) );
        }

        for ( ContentTypeName contentTypeName : module.getContentTypeDependencies() )
        {
            dependencies.addContent( new Element( "content-type" ).setText( contentTypeName.toString() ) );
        }
        moduleEl.addContent( dependencies );

        Element form = new Element( "form" ).setAttribute( "name", "config" );
        if ( module.getConfig() != null )
        {
            new FormItemsXmlSerializer().serialize( module.getConfig(), form );
        }
        moduleEl.addContent( form );

    }

    public void toModule( final String xml, final Module.Builder moduleBuilder )
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

    private void parse( final Element moduleEl, final Module.Builder moduleBuilder )
        throws IOException
    {
        final String displayName = moduleEl.getChildText( "display-name" );
        moduleBuilder.
            displayName( displayName ).
            info( moduleEl.getChildText( "info" ) ).
            url( moduleEl.getChildText( "url" ) ).
            vendorName( moduleEl.getChild( "vendor" ).getChildText( "name" ) ).
            vendorUrl( moduleEl.getChild( "vendor" ).getChildText( "url" ) );

        final Element dependenciesEl = moduleEl.getChild( "dependencies" );
        if ( dependenciesEl != null )
        {
            moduleBuilder.maxSystemVersion( ModuleVersion.from( dependenciesEl.getAttributeValue( "system-max-version" ) ) ).
                minSystemVersion( ModuleVersion.from( dependenciesEl.getAttributeValue( "system-min-version" ) ) );
            for ( Element child : (List<Element>) dependenciesEl.getChildren() )
            {
                switch ( child.getName() )
                {
                    case "module":
                        moduleBuilder.addModuleDependency( ModuleKey.from( child.getText() ) );
                        break;
                    case "content-type":
                        moduleBuilder.addContentTypeDependency( ContentTypeName.from( child.getText() ) );
                        break;
                }
            }
        }

        Iterable<FormItem> formItems = new FormItemsXmlSerializer().parse( moduleEl.getChild( "form" ) );

        Form.Builder form = Form.newForm();

        for ( FormItem formItem : formItems )
        {
            form.addFormItem( formItem );
        }

        moduleBuilder.config( form.build() );
    }
}
