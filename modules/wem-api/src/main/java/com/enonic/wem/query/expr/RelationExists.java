package com.enonic.wem.query.expr;

import com.enonic.wem.query.DynamicConstraint;
import com.enonic.wem.query.Expression;
import com.enonic.wem.query.expr.CompareExpr;
import com.enonic.wem.query.expr.FieldExpr;
import com.enonic.wem.query.expr.ValueExpr;

public final class RelationExists
    extends CompareExpr
{
    public final static String NAME = "relationExists";

    public RelationExists( final FieldExpr field, final CompareExpr compareExpr )
    {
        super( 11, field, compareExpr );
    }

   public String toString()
   {
       final StringBuilder str = new StringBuilder();
       str.append( "relationExists(" );
       str.append( getLeft().toString() );
       str.append( ", " );
       str.append( getRight().toString() );
       str.append( ")" );
       return str.toString();
   }

}
