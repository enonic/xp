module api.schema.metadata {

    export class MetadataSchemaName implements api.Equitable {

        private value: string;

        constructor(name: string) {
            this.value = name;
        }

        toString(): string {
            return this.value;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, MetadataSchemaName)) {
                return false;
            }
            var other = <MetadataSchemaName> o;

            return this.value == other.value;
        }
    }
}