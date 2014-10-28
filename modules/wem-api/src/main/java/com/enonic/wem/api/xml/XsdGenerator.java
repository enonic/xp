package com.enonic.wem.api.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public final class XsdGenerator
{
    private final class SchemaOutput
        extends SchemaOutputResolver
    {
        private DOMResult result;

        @Override
        public Result createOutput( final String namespaceUri, final String suggestedFileName )
            throws IOException
        {
            this.result = new DOMResult();
            this.result.setSystemId( namespaceUri );
            return this.result;
        }

        public String getAsString()
            throws Exception
        {
            return DomHelper.serialize( this.result.getNode() );
        }
    }

    private String generate()
        throws Exception
    {
        final JAXBContext context = JAXBContext.newInstance( "com.enonic.wem.api.xml.model" );

        final SchemaOutput output = new SchemaOutput();
        context.generateSchema( output );

        return output.getAsString();
    }

    public static String generateXsd()
        throws Exception
    {
        return new XsdGenerator().generate();
    }

    public static void main( final String... args )
        throws Exception
    {
        final File file = new File( "modules/wem-api/src/main/resources/com/enonic/wem/api/xml/schema/model.xsd" );
        file.getParentFile().mkdirs();

        final String xsd = generateXsd();
        Files.write( xsd, file, Charsets.UTF_8 );
    }
}
