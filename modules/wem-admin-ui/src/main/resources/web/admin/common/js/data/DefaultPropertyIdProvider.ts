module api.data {

    export class DefaultPropertyIdProvider implements PropertyIdProvider {

        private seed: string;

        private nextId: number = 1;

        constructor(seed?: string) {
            this.seed = seed;
        }

        getNextId(): PropertyId {
            var seedPrefix = (!this.seed ? "" : this.seed + "-");
            return new PropertyId(seedPrefix + (this.nextId++));
        }
    }
}