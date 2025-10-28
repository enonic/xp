package com.enonic.xp.xml.parser;

import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.DomHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class XmlInputTypeConfigMapperTest
{
    private static final ApplicationKey APP_KEY = ApplicationKey.from( "myapp" );

    private InputStream findResource( final String suffix )
    {
        final String name = getClass().getSimpleName() + "-" + suffix;
        final InputStream in = getClass().getResourceAsStream( name );

        if ( in == null )
        {
            throw new IllegalArgumentException( "Could not find resource [" + name + "]" );
        }

        return in;
    }

    private DomElement parse( final String suffix )
    {
        final Document doc = DomHelper.parse( findResource( suffix ) );
        return DomElement.from( doc.getDocumentElement() );
    }

    private InputTypeConfig build( final String suffix )
    {
        return build( suffix, InputTypeName.from( "some-input-type" ) );
    }

    private InputTypeConfig build( final String suffix, final InputTypeName inputTypeName )
    {
        final XmlInputTypeConfigMapper mapper = new XmlInputTypeConfigMapper( APP_KEY, inputTypeName );
        return mapper.build( parse( suffix ) );
    }

    @Test
    void parseNone()
    {
        final InputTypeConfig config = build( "none.xml" );
        assertNotNull( config );
        assertEquals( 0, config.getSize() );
    }

    @Test
    void parseSimple()
    {
        final InputTypeConfig config = build( "simple.xml" );
        assertNotNull( config );
        assertEquals( 2, config.getSize() );

        assertEquals( "timezone=true[]", toString( config.getProperties( "timezone" ) ) );
        assertEquals( "other=world[]", toString( config.getProperties( "other" ) ) );
    }

    @Test
    void parseAttributes()
    {
        final InputTypeConfig config = build( "attributes.xml" );
        assertNotNull( config );
        assertEquals( 2, config.getSize() );

        assertEquals( "other=world[a=1],other=world[a=1,b=2]", toString( config.getProperties( "other" ) ) );
    }

    @Test
    void parseResolve()
    {
        final InputTypeConfig config = build( "resolve.xml" );
        assertNotNull( config );
        assertEquals( 8, config.getSize() );

        assertEquals( "contentType=test[]", toString( config.getProperties( "contentType" ) ) );
        assertEquals( "myContentType=test[]", toString( config.getProperties( "myContentType" ) ) );

        assertEquals( "mixinType=myapp:test[]", toString( config.getProperties( "mixinType" ) ) );
        assertEquals( "myMixinType=myapp:test[]", toString( config.getProperties( "myMixinType" ) ) );

        assertEquals( "other1=[mixinType=myapp:test]", toString( config.getProperties( "other1" ) ) );
        assertEquals( "other2=[contentType=test]", toString( config.getProperties( "other2" ) ) );
        assertEquals( "other4=[myMixinType=myapp:test]", toString( config.getProperties( "other4" ) ) );
        assertEquals( "other5=[myContentType=test]", toString( config.getProperties( "other5" ) ) );
    }

    @Test
    void parseAliased()
    {
        final InputTypeConfig config = build( "aliased.xml", InputTypeName.CONTENT_SELECTOR );
        assertNotNull( config );
        assertEquals( 1, config.getSize() );

        assertEquals( "allowContentType=contentTypeTest[]", toString( config.getProperties( "allowContentType" ) ) );
    }

    @Test
    void parseCamelCase()
    {
        final InputTypeConfig config = build( "camelcase.xml" );
        assertNotNull( config );
        assertEquals( 8, config.getSize() );

        assertEquals( "contentType=test[]", toString( config.getProperties( "contentType" ) ) );
        assertEquals( "myContentType=test[]", toString( config.getProperties( "myContentType" ) ) );

        assertEquals( "mixinType=myapp:test[]", toString( config.getProperties( "mixinType" ) ) );
        assertEquals( "myMixinType=myapp:test[]", toString( config.getProperties( "myMixinType" ) ) );

        assertEquals( "other1=[mixinType=myapp:test]", toString( config.getProperties( "other1" ) ) );
        assertEquals( "other2=[contentType=test]", toString( config.getProperties( "other2" ) ) );
        assertEquals( "other4=[myMixinType=myapp:test]", toString( config.getProperties( "other4" ) ) );
        assertEquals( "other5=[myContentType=test]", toString( config.getProperties( "other5" ) ) );
    }

    @Test
    void parseEmpty()
    {
        final InputTypeConfig config = build( "empty.xml" );
        assertNotNull( config );
        assertEquals( 2, config.getSize() );

        assertEquals( "contentType=[]", toString( config.getProperties( "contentType" ) ) );
        assertEquals( "other=[contentType=]", toString( config.getProperties( "other" ) ) );
    }

    private String toString( final Collection<InputTypeProperty> properties )
    {
        return properties.stream().map( Objects::toString ).collect( Collectors.joining( "," ) );
    }
}
