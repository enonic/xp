package com.enonic.wem.core.jcr.old;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.And;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Comparison;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DescendantNode;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.NodeLocalName;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.StaticOperand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.jcr.query.qom.QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO;

public class JcrQuery
{
    private static final Logger LOG = LoggerFactory.getLogger( JcrQuery.class );

    private final static String SELECTOR_NAME = "selectorName";

    private final static String BASE_NODE_TYPE = "nt:base";

    private final JcrSession session;

    private QueryObjectModelFactory objectModelFactory;

    private ValueFactory valueFactory;

    private long offset = 0;

    private long limit = -1;

    private String nodeType;

    private final List<Constraint> constraints;

    JcrQuery( JcrSession session )
    {
        this.session = session;
        this.constraints = new ArrayList<Constraint>();
    }

    public long getLimit()
    {
        return limit;
    }

    public JcrQuery limit( final long limit )
    {
        this.limit = limit;
        return this;
    }

    public long getOffset()
    {
        return offset;
    }

    public JcrQuery offset( final long offset )
    {
        this.offset = offset;
        return this;
    }

    public String getNodeType()
    {
        return nodeType;
    }

    public JcrQuery selectNodeType( final String nodeType )
    {
        this.nodeType = nodeType;
        return this;
    }

    public JcrQuery from( final String path )
    {
        try
        {
            final DescendantNode constraintDescendant = getModelFactory().descendantNode( SELECTOR_NAME, path );
            constraints.add( constraintDescendant );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
        return this;
    }

    public JcrQuery withPropertyEqualsTo( final String propertyName, final String value )
    {
        Value jcrValue = getValueFactory().createValue( value );
        addPropertyEqualsToConstraint( propertyName, jcrValue );
        return this;
    }

    public JcrQuery withPropertyEqualsTo( final String propertyName, final boolean value )
    {
        Value jcrValue = getValueFactory().createValue( value );
        addPropertyEqualsToConstraint( propertyName, jcrValue );
        return this;
    }

    public JcrQuery withPropertyEqualsTo( final String propertyName, final long value )
    {
        Value jcrValue = getValueFactory().createValue( value );
        addPropertyEqualsToConstraint( propertyName, jcrValue );
        return this;
    }

    public JcrQuery withPropertyEqualsTo( final String propertyName, final double value )
    {
        Value jcrValue = getValueFactory().createValue( value );
        addPropertyEqualsToConstraint( propertyName, jcrValue );
        return this;
    }

    public JcrQuery withName( final String nodeName )
    {
        try
        {
            final NodeLocalName nodeNameConstraint = getModelFactory().nodeLocalName( SELECTOR_NAME );
            final Value nameValue = getValueFactory().createValue( nodeName );
            final StaticOperand nameOperand = getModelFactory().literal( nameValue );
            final Comparison nameComparison = getModelFactory().comparison( nodeNameConstraint, JCR_OPERATOR_EQUAL_TO, nameOperand );
            constraints.add( nameComparison );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
        return this;
    }

    private void addPropertyEqualsToConstraint( final String propertyName, final Value value )
    {
        try
        {
            final DynamicOperand op1 = getModelFactory().propertyValue( SELECTOR_NAME, propertyName );
            final StaticOperand op2 = getModelFactory().literal( value );
            final Comparison constraintEquals = getModelFactory().comparison( op1, JCR_OPERATOR_EQUAL_TO, op2 );
            constraints.add( constraintEquals );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    private QueryObjectModelFactory getModelFactory()
    {
        if ( objectModelFactory == null )
        {
            try
            {
                objectModelFactory = session.getRealSession().getWorkspace().getQueryManager().getQOMFactory();
            }
            catch ( RepositoryException e )
            {
                throw new RepositoryRuntimeException( e );
            }
        }
        return objectModelFactory;
    }

    private ValueFactory getValueFactory()
    {
        if ( valueFactory == null )
        {
            try
            {
                valueFactory = session.getRealSession().getValueFactory();
            }
            catch ( RepositoryException e )
            {
                throw new RepositoryRuntimeException( e );
            }
        }
        return valueFactory;
    }

    public JcrNodeIterator execute()
    {
        try
        {
            final QueryObjectModelFactory factory = getModelFactory();

            final String nodeType = ( this.nodeType == null ) ? BASE_NODE_TYPE : this.nodeType;
            final Selector source = factory.selector( nodeType, SELECTOR_NAME );
            final Column[] columns = null;
            final Ordering[] orderings = null;
            final Constraint constraint = consolidateConstraints( this.constraints, factory );

            final QueryObjectModel queryObj = factory.createQuery( source, constraint, orderings, columns );
            if ( limit >= 0 )
            {
                queryObj.setLimit( limit );
            }
            queryObj.setOffset( offset );

            if ( LOG.isInfoEnabled() )
            {
                LOG.info( "Executing Jcr query: " + queryObj.getStatement() );
            }
            final QueryResult result = queryObj.execute();

            return new JcrNodeIteratorImpl( result.getNodes() );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    private Constraint consolidateConstraints( final List<Constraint> constraints, final QueryObjectModelFactory factory )
        throws RepositoryException
    {
        if ( constraints.isEmpty() )
        {
            return null;
        }
        else if ( constraints.size() == 1 )
        {
            return constraints.get( 0 );
        }

        final Constraint first = constraints.get( 0 );
        final Constraint second = constraints.get( 1 );
        And andConstraint = factory.and( first, second );
        for ( int i = 2; i < constraints.size(); i++ )
        {
            andConstraint = factory.and( andConstraint, constraints.get( i ) );
        }
        return andConstraint;
    }

}
