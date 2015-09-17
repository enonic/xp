module api.query.aggregation {

    export class Range {

        private key: string;

        constructor(key?: string) {
            this.key = key;
        }

        public setKey(key: string) {
            this.key = key;
        }

        public getKey(): string {
            return this.key;
        }

        public toRangeJson(): api.query.aggregation.RangeJson {

            if (this.getKey() != null) {
                return {
                    "key": this.getKey()
                };
            } else {
                return {};
            }


        }

    }

}