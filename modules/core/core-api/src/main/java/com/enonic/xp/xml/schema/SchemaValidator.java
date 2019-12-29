package com.enonic.xp.xml.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.xml.sax.SAXException;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class SchemaValidator
    implements SchemaNamespaces
{
    private final List<SchemaHandler> list;

    public SchemaValidator()
    {
        this.list = new ArrayList<>();
        register( EXPORT_NS, "/META-INF/xsd/export.xsd" );
        register( MODEL_NS, "/META-INF/xsd/model.xsd" );
    }

    private void register( final String ns, final String location )
    {
        register( new SchemaHandler( ns, location ) );
    }

    private void register( final SchemaHandler validator )
    {
        this.list.add( validator );
    }

    public DOMResult validate( final DOMSource source )
        throws IOException, SAXException
    {
        for ( final SchemaHandler validator : this.list )
        {
            if ( validator.canValidate( source ) )
            {
                return validator.validate( source );
            }
        }

        return new DOMResult( source.getNode() );
    }
}
