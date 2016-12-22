module api.content.resource {

    import ConstraintExpr = api.query.expr.ConstraintExpr;
    import NotExpr = api.query.expr.NotExpr;
    import LogicalExpr = api.query.expr.LogicalExpr;
    import LogicalOperator = api.query.expr.LogicalOperator;
    import CompareExpr = api.query.expr.CompareExpr;
    import FieldExpr = api.query.expr.FieldExpr;
    import ValueExpr = api.query.expr.ValueExpr;
    import OrderDirection = api.query.expr.OrderDirection;
    import OrderExpr = api.query.expr.OrderExpr;

    export class MoveAllowedTargetsRequest extends ContentSummaryRequest {

        private filterContentPaths: ContentPath[];

        protected createSearchExpression(): ConstraintExpr {
            var searchExpr: ConstraintExpr = super.createSearchExpression();

            var forbiddenPathsExpr: ConstraintExpr = this.createChildPathsExpr();

            return new LogicalExpr(searchExpr, LogicalOperator.AND, new NotExpr(forbiddenPathsExpr));
        }

        private createChildPathsExpr(): ConstraintExpr {
            if (!this.filterContentPaths || this.filterContentPaths.length === 0) {
                throw new Error("Content paths to be moved not set");
            }

            if (this.filterContentPaths.length === 1) {
                return CompareExpr.like(new FieldExpr("_path"),
                    ValueExpr.string("/content" + this.filterContentPaths[0].toString() + "/*"));
            }

            let pathExpr1 = CompareExpr.like(new FieldExpr("_path"),
                ValueExpr.string("/content" + this.filterContentPaths[0].toString() + "/*"));
            let pathExpr2 = CompareExpr.like(new FieldExpr("_path"),
                ValueExpr.string("/content" + this.filterContentPaths[1].toString() + "/*"));
            let logicalExpr: LogicalExpr = LogicalExpr.or(pathExpr1, pathExpr2);

            if (this.filterContentPaths.length === 2) {
                return logicalExpr;
            }

            this.filterContentPaths.forEach((contentPath: ContentPath, index: number) => {
                if (index === 0 || index === 1) {
                    return;
                }

                let pathExpr: ConstraintExpr = CompareExpr.like(new FieldExpr("_path"),
                    ValueExpr.string("/content" + this.filterContentPaths[index].toString() + "/*"));
                logicalExpr = LogicalExpr.or(logicalExpr, pathExpr);
            });

            return logicalExpr;
        }

        setFilterContentPaths(contentPaths: ContentPath[]) {
            this.filterContentPaths = contentPaths;
        }

        protected getDefaultOrder(): OrderExpr[] {
            return [ContentSummaryRequest.SCORE_DESC, ContentSummaryRequest.PATH_ASC];
        }
    }
}