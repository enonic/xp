module api.content.page {

    export class DescriptorName {

        private name: string;

        constructor(name: string) {
            if (name == null) {
                throw new Error("name cannot be null");
            }
            this.name = name;
        }

        public toString(): string {
            return this.name;
        }
    }
}