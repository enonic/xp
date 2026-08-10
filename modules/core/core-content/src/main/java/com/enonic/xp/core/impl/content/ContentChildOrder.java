package com.enonic.xp.core.impl.content;

import java.util.Locale;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;

@NullMarked
final class ContentChildOrder
{
    private ContentChildOrder()
    {
    }

    /**
     * Makes ordering by display name follow the collation rules of the language of the content the children belong to. Every other order
     * expression is kept as is.
     */
    static @Nullable ChildOrder withLanguage( final @Nullable ChildOrder childOrder, final @Nullable Locale language )
    {
        if ( childOrder == null || language == null )
        {
            return childOrder;
        }
        final ChildOrder.Builder builder = ChildOrder.create();
        for ( final OrderExpr orderExpr : childOrder.getOrderExpressions() )
        {
            if ( orderExpr instanceof FieldOrderExpr fieldOrderExpr && fieldOrderExpr.getLanguage() == null &&
                ContentIndexPath.DISPLAY_NAME.equals( fieldOrderExpr.getField().getIndexPath() ) )
            {
                builder.add( FieldOrderExpr.create( fieldOrderExpr.getField().getIndexPath(), fieldOrderExpr.getDirection(), language ) );
            }
            else
            {
                builder.add( orderExpr );
            }
        }
        return builder.build();
    }
}
