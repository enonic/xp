module api.content.resource {

    import CompareExpr = api.query.expr.CompareExpr;
    import FieldExpr = api.query.expr.FieldExpr;
    import ValueExpr = api.query.expr.ValueExpr;
    import LogicalExpr = api.query.expr.LogicalExpr;
    import LogicalOperator = api.query.expr.LogicalOperator;
    import QueryExpr = api.query.expr.QueryExpr;
    import ConstraintExpr = api.query.expr.ConstraintExpr;

    export class FragmentContentSummaryRequest extends ContentSummaryRequest {

        private parentSitePath: string;

        protected createSearchExpression(): ConstraintExpr {
            if (this.parentSitePath) {
                let searchConstraint = super.createSearchExpression();
                let nearestSiteConstraint = this.createParentSiteFragmentsOnlyQuery();
                return new LogicalExpr(searchConstraint, LogicalOperator.AND, nearestSiteConstraint);
            } else {
                return super.createSearchExpression();
            }
        }

        private createParentSiteFragmentsOnlyQuery(): CompareExpr {
            return CompareExpr.like(new FieldExpr('_path'), ValueExpr.string('/content' + this.parentSitePath + '/*'));
        }

        setParentSitePath(sitePath: string) {
            this.parentSitePath = sitePath;
        }

    }

}
