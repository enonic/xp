module api.content {

    export class FieldOrderExpr extends OrderExpr {

        private fieldName: string;

        constructor(builder: FieldOrderExprBuilder) {
            super(builder);
            this.fieldName = builder.fieldName;
        }

        getFieldName(): string {
            return this.fieldName;
        }

        toJson(): json.OrderExprJson {
            return {
                "fieldName": this.fieldName,
                "direction": this.getDirection()
            };
        }
    }

    export class FieldOrderExprBuilder extends OrderExprBuilder {

        fieldName: string;

        constructor(json: json.OrderExprJson) {
            super(json);
            this.fieldName = json.fieldName;
        }

        public setFieldName(value: string): FieldOrderExprBuilder {
            this.fieldName = value;
            return this;
        }

        public build(): FieldOrderExpr {
            return new FieldOrderExpr(this);
        }
    }
}