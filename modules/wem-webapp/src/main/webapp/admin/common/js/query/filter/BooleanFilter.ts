module api.query.filter {


    export class BooleanFilter extends Filter {

        private must: api.query.filter.Filter[] = [];
        private mustNot: api.query.filter.Filter[] = [];
        private should: api.query.filter.Filter[] = [];


        public addMust(must: api.query.filter.Filter): void {
            this.must.push(must);
        }

        public addMustNot(mustNot: api.query.filter.Filter): void {
            this.mustNot.push(mustNot);
        }

        public addShould(should: api.query.filter.Filter): void {
            this.should.push(should);
        }

        toJson(): api.query.filter.FilterTypeWrapperJson {

            var json: api.query.filter.BooleanFilterJson = {
                must: this.toJsonWrapperElements(this.must),
                mustNot: this.toJsonWrapperElements(this.mustNot),
                should: this.toJsonWrapperElements(this.should)
            }

            return <api.query.filter.FilterTypeWrapperJson> {
                BooleanFilter: json
            }
        }

        toJsonWrapperElements(filters: api.query.filter.Filter[]): api.query.filter.FilterTypeWrapperJson[] {

            var wrapperJsons: api.query.filter.FilterTypeWrapperJson[] = [];

            filters.forEach((filter: api.query.filter.Filter)=> {
                var filterTypeWrapperJson = filter.toJson();
                wrapperJsons.push(filterTypeWrapperJson);
            });

            return wrapperJsons;
        }

    }

}