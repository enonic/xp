module api.content.page {

    export class ComponentName {

        private value: string;

        constructor(value: string) {
            this.value = value;
        }

        public toString(): string {
            return this.value.toString();
        }
    }
}