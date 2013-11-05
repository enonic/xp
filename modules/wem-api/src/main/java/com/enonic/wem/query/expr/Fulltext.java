package com.enonic.wem.query.expr;

public final class Fulltext
    extends CompareExpr
{
    public final static String NAME = "fulltext";

    public Fulltext( final ValueExpr field )
    {
        super( 12, null, field );
    }

   public String toString()
   {
       final StringBuilder str = new StringBuilder();
       str.append( "fulltext(" );
       //str.append( getLeft().toString() );
       //str.append( ", " );
       str.append( getRight().toString() );
       str.append( ")" );
       return str.toString();
   }

}
