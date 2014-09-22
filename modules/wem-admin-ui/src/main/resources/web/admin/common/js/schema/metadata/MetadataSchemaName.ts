module api.schema.metadata {

    export class MetadataSchemaName {

        private value: string;

        constructor(name: string) {
            this.value = name;
        }

        toString(): string {
            return this.value;
        }
    }
}