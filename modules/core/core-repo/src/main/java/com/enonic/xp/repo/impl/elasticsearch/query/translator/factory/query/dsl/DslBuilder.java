package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query.dsl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.Value;

public abstract class DslBuilder
{
    private PropertySet expression;

    public DslBuilder( final PropertySet expression )
    {
        this.expression = expression;
    }

    protected String getString( final String name )
    {
        return Optional.ofNullable( expression.getProperty( name ) ).map( Property::getString ).orElse( null );
    }

    protected List<String> getStrings( final String name )
    {
        return expression.getProperties( name ).stream()
                         .map( Property::getValue )
                         .map( Value::asString )
                         .collect( Collectors.toList() );
    }

    public abstract QueryBuilder create();
}
