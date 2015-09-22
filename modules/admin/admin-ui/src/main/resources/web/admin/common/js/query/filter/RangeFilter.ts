module api.query.filter {

    export class RangeFilter extends api.query.filter.Filter {

        private from: api.data.Value;
        private to: api.data.Value;
        private fieldName: string;

        constructor(fieldName: string, from: api.data.Value, to: api.data.Value) {
            super();
            this.fieldName = fieldName;
            this.from = from;
            this.to = to;
        }

        toJson(): api.query.filter.FilterTypeWrapperJson {

            var json: api.query.filter.RangeFilterJson = {
                fieldName: this.fieldName,
                from: this.from != null ? this.from.getString() : null,
                to: this.to != null ? this.to.getString() : null
            };

            return <api.query.filter.FilterTypeWrapperJson> {
                RangeFilter: json
            }

        }


    }


}

