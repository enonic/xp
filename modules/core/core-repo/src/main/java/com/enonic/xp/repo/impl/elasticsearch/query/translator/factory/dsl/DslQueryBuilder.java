package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.Value;

public abstract class DslQueryBuilder
{
    private final PropertySet expression;

    public DslQueryBuilder( final PropertySet expression )
    {
        this.expression = expression;
    }

    protected String getString( final String name )
    {
        return Optional.ofNullable( expression.getProperty( name ) ).map( Property::getString ).orElse( null );
    }

    protected boolean getBoolean( final String name )
    {
        return Optional.ofNullable( expression.getProperty( name ) ).map( Property::getBoolean ).orElse( false );
    }

    protected Long getLong( final String name, final Long defaultValue )
    {
        return Optional.ofNullable( expression.getProperty( name ) ).map( Property::getLong ).orElse( defaultValue );
    }

    protected Double getDouble( final String name )
    {
        return Optional.ofNullable( expression.getProperty( name ) ).map( Property::getDouble ).orElse( null );
    }

    protected List<String> getStrings( final String name )
    {
        return expression.getProperties( name ).stream().map( Property::getValue ).map( Value::asString ).collect( Collectors.toList() );
    }

    protected List<Object> getObjects( final String name )
    {
        return expression.getProperties( name ).stream().map( Property::getValue ).map( Value::getObject ).collect( Collectors.toList() );
    }

    protected Object getObject( final String name )
    {
        return Optional.ofNullable( expression.getValue( name ) ).map( Value::getObject ).orElse( null );
    }

    protected Iterable<Property> getProperties()
    {
        return expression.getProperties();
    }

    public abstract QueryBuilder create();
}
