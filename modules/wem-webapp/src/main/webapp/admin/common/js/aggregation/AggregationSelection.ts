module api.aggregation {

    export class AggregationSelection {

        name: string;
        values: string[];

        constructor(name: string) {
            this.name = name;
        }

        public setValues(values: string[]) {
            this.values = values;
        }

        public getName(): string {
            return this.name;
        }

        public getValues(): string[] {
            return this.values;
        }

    }


}