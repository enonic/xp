package com.enonic.xp.xml.parser;

import java.io.InputStream;

import org.junit.Test;
import org.w3c.dom.Document;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.DomHelper;

import static org.junit.Assert.*;

public class XmlInputTypeConfigMapperTest
{
    private final static ApplicationKey APP_KEY = ApplicationKey.from( "myapp" );

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
        final XmlInputTypeConfigMapper mapper = new XmlInputTypeConfigMapper( APP_KEY );
        return mapper.build( parse( suffix ) );
    }

    @Test
    public void parseNone()
    {
        final InputTypeConfig config = build( "none.xml" );
        assertNotNull( config );
        assertEquals( 0, config.getSize() );
    }

    @Test
    public void parseSimple()
    {
        final InputTypeConfig config = build( "simple.xml" );
        assertNotNull( config );
        assertEquals( 2, config.getSize() );

        assertEquals( "timezone=true[]", toString( config.getProperties( "timezone" ) ) );
        assertEquals( "other=world[]", toString( config.getProperties( "other" ) ) );
    }

    @Test
    public void parseAttributes()
    {
        final InputTypeConfig config = build( "attributes.xml" );
        assertNotNull( config );
        assertEquals( 2, config.getSize() );

        assertEquals( "other=world[a=1],other=world[a=1,b=2]", toString( config.getProperties( "other" ) ) );
    }

    @Test
    public void parseResolve()
    {
        final InputTypeConfig config = build( "resolve.xml" );
        assertNotNull( config );
        assertEquals( 12, config.getSize() );

        assertEquals( "contentType=myapp:test[]", toString( config.getProperties( "contentType" ) ) );
        assertEquals( "myContentType=myapp:test[]", toString( config.getProperties( "myContentType" ) ) );

        assertEquals( "mixinType=myapp:test[]", toString( config.getProperties( "mixinType" ) ) );
        assertEquals( "myMixinType=myapp:test[]", toString( config.getProperties( "myMixinType" ) ) );

        assertEquals( "relationshipType=myapp:test[]", toString( config.getProperties( "relationshipType" ) ) );
        assertEquals( "myRelationshipType=myapp:test[]", toString( config.getProperties( "myRelationshipType" ) ) );

        assertEquals( "other1=[mixinType=myapp:test]", toString( config.getProperties( "other1" ) ) );
        assertEquals( "other2=[contentType=myapp:test]", toString( config.getProperties( "other2" ) ) );
        assertEquals( "other3=[relationshipType=myapp:test]", toString( config.getProperties( "other3" ) ) );
        assertEquals( "other4=[myMixinType=myapp:test]", toString( config.getProperties( "other4" ) ) );
        assertEquals( "other5=[myContentType=myapp:test]", toString( config.getProperties( "other5" ) ) );
        assertEquals( "other6=[myRelationshipType=myapp:test]", toString( config.getProperties( "other6" ) ) );
    }

    @Test
    public void parseCamelCase()
    {
        System.out.println( CaseFormat.LOWER_HYPHEN.to( CaseFormat.LOWER_CAMEL, "other-test" ) );

        final InputTypeConfig config = build( "camelcase.xml" );
        assertNotNull( config );
        assertEquals( 12, config.getSize() );

        assertEquals( "contentType=myapp:test[]", toString( config.getProperties( "contentType" ) ) );
        assertEquals( "myContentType=myapp:test[]", toString( config.getProperties( "myContentType" ) ) );

        assertEquals( "mixinType=myapp:test[]", toString( config.getProperties( "mixinType" ) ) );
        assertEquals( "myMixinType=myapp:test[]", toString( config.getProperties( "myMixinType" ) ) );

        assertEquals( "relationshipType=myapp:test[]", toString( config.getProperties( "relationshipType" ) ) );
        assertEquals( "myRelationshipType=myapp:test[]", toString( config.getProperties( "myRelationshipType" ) ) );

        assertEquals( "other1=[mixinType=myapp:test]", toString( config.getProperties( "other1" ) ) );
        assertEquals( "other2=[contentType=myapp:test]", toString( config.getProperties( "other2" ) ) );
        assertEquals( "other3=[relationshipType=myapp:test]", toString( config.getProperties( "other3" ) ) );
        assertEquals( "other4=[myMixinType=myapp:test]", toString( config.getProperties( "other4" ) ) );
        assertEquals( "other5=[myContentType=myapp:test]", toString( config.getProperties( "other5" ) ) );
        assertEquals( "other6=[myRelationshipType=myapp:test]", toString( config.getProperties( "other6" ) ) );
    }

    @Test
    public void parseEmpty()
    {
        final InputTypeConfig config = build( "empty.xml" );
        assertNotNull( config );
        assertEquals( 2, config.getSize() );

        assertEquals( "contentType=[]", toString( config.getProperties( "contentType" ) ) );
        assertEquals( "other=[contentType=]", toString( config.getProperties( "other" ) ) );
    }

    private String toString( final Iterable<InputTypeProperty> properties )
    {
        return Joiner.on( "," ).join( properties );
    }
}
