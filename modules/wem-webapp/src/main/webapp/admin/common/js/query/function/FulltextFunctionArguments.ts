module api.query.function {

    export class FulltextFunctionArguments {

        public static FIELDNAME_INDEX:number = 0;

        public static SEARCHSTRING_INDEX:number = 1;

        public static OPERATOR_INDEX:number = 2;

        private minArguments:number = 2;

        private maxArguments:number = 3;

        private functionName:string = "fulltext";

        private fieldName:string;

        private operator:api.query.expr.LogicalOperator = api.query.expr.LogicalOperator.OR;

        private searchString:string;

        constructor( arguments:api.query.expr.ValueExpr[] )
        {
            var verify:boolean = this.verifyNumberOfArguments( arguments );

            if ( verify ) {
                this.fieldName = arguments[FulltextFunctionArguments.FIELDNAME_INDEX].getValue().asString();
                this.searchString = arguments[FulltextFunctionArguments.SEARCHSTRING_INDEX].getValue().asString();

                this.setOperator( arguments );
            }
        }

        private verifyNumberOfArguments( arguments:api.query.expr.ValueExpr[] ):boolean
        {
            if ( arguments == null || arguments.length < this.getMinArguments() || arguments.length > this.getMaxArguments() )
            {
                var message:string = "Wrong number of arguments (" + ( arguments == null ? "0" : "" + arguments.length ) +
                    ") for function '" + this.getFunctionName() + "' (expected " + this.getMinArguments() +
                    " to " +
                    this.getMaxArguments() + ")";
                api.notify.showWarning( message );

                return false;
            }
            return true;
        }

        private setOperator( arguments:api.query.expr.ValueExpr[] )
        {
            if ( arguments.length >= FulltextFunctionArguments.OPERATOR_INDEX + 1 && arguments[FulltextFunctionArguments.OPERATOR_INDEX] != null )
            {
                var operatorAsString:string = arguments[FulltextFunctionArguments.OPERATOR_INDEX].getValue().asString().toUpperCase();

                this.operator = this.valueOfLogicalOperator( operatorAsString );

                if ( this.operator == null ) {
                    // throw message 'Wrong operator'
                    api.notify.showWarning( "Invalid operator: " + operatorAsString );
                }
            }
        }

        private valueOfLogicalOperator( operatorAsString:string ):api.query.expr.LogicalOperator {
            if ( operatorAsString == "AND" ) {
                return api.query.expr.LogicalOperator.AND;

            } else if ( operatorAsString == "OR" ) {
                return api.query.expr.LogicalOperator.OR;
            }

            return null;
        }

        public getOperator():api.query.expr.LogicalOperator
        {
            return this.operator;
        }

        public getSearchString():string
        {
            return this.searchString;
        }

        public getFieldName():string
        {
            return this.fieldName;
        }

        getMinArguments():number
        {
            return this.minArguments;
        }

        getMaxArguments():number
        {
            return this.maxArguments;
        }

        public getFunctionName():string
        {
            return this.functionName;
        }
    }
}
