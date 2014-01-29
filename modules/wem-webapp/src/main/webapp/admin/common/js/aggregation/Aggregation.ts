module api.aggregation {

    export class Aggregation {

        private name: string;

        constructor(name: string) {
            this.name = name;
        }

        public getName(): string {
            return this.name;
        }

    }
}
