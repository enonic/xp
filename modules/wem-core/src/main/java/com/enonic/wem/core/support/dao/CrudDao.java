package com.enonic.wem.core.support.dao;


import javax.jcr.Session;

public interface CrudDao<T, Ts, QN, QNs, Ss>
{
    public void create( T object, Session session );

    public void update( T object, Session session );

    public void delete( QN object, Session session );

    public QNs exists( Ss object, Session session );

    public Ts selectAll( Session session );

    public Ts select( Ss selectors, Session session );
}
