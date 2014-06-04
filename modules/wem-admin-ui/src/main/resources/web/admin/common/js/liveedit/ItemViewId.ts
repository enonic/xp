module api.liveedit {


    export class ItemViewId {

        private value: number;

        private refString: string;

        constructor(value: number) {
            api.util.assert(value >= 1, "An ItemViewId must be 1 or larger");
            this.value = value;
            this.refString = "" + value;
        }

        toNumber(): number {
            return this.value;
        }

        toString(): string {
            return this.refString;
        }

        static fromString(s: string) {
            return new ItemViewId(+s);
        }
    }
}