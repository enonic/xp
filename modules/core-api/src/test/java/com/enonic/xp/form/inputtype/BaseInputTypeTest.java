package com.enonic.xp.form.inputtype;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.util.Reference;

public abstract class BaseInputTypeTest
{
    protected final InputType type;

    protected final JsonTestHelper jsonHelper;

    public BaseInputTypeTest( final InputType type )
    {
        this.type = type;
        this.jsonHelper = new JsonTestHelper( this );
    }

    protected final Property booleanProperty( final boolean value )
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addBoolean( "test", value );
    }

    protected final Property stringProperty( final String value )
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addString( "test", value );
    }

    protected final Property referenceProperty( final String value )
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addReference( "test", Reference.from( value ) );
    }

    protected final Property dateTimeProperty()
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addInstant( "test", Instant.MAX );
    }

    protected final Property localDateTimeProperty()
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addLocalDateTime( "test", LocalDateTime.MAX );
    }

    protected final Property localDateProperty()
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addLocalDate( "test", LocalDate.MAX );
    }
}
