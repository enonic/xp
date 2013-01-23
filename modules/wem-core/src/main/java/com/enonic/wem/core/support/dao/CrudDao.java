package com.enonic.wem.core.support.dao;


import javax.jcr.Session;

public interface CrudDao<TObject, TObjects, TQualifiedName, TQualifiedNames, TSelectors>
{
    public void create( TObject object, Session session );

    public void update( TObject object, Session session );

    public void delete( TQualifiedName object, Session session );

    public TQualifiedNames exists( TSelectors object, Session session );

    public TObjects selectAll( Session session );

    public TObjects select( TSelectors selectors, Session session );
}
