module api.liveedit {

    export class PageComponentId {

        private value: string;

        constructor(value: string) {
            this.value = value;
        }

        toString(): string {
            return this.value;
        }

        static fromNumber(value: number) {
            return new PageComponentId("" + value);
        }

    }
}