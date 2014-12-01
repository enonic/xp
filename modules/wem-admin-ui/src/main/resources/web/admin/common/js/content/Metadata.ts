module api.content {

    import MetadataSchemaName = api.schema.metadata.MetadataSchemaName;
    import PropertyTree = api.data2.PropertyTree;
    import PropertyIdProvider = api.data2.PropertyIdProvider;

    export class Metadata implements api.Cloneable {

        private name: MetadataSchemaName;

        private data: PropertyTree;

        constructor(name: MetadataSchemaName, data: PropertyTree) {
            this.name = name;
            this.data = data;
        }

        static fromJson(metadataJson: api.content.json.MetadataJson, propertyIdProvider: PropertyIdProvider): Metadata {
            return new Metadata(new MetadataSchemaName(metadataJson.name), PropertyTree.fromJson(metadataJson.data, propertyIdProvider));
        }

        toJson(): api.content.json.MetadataJson {
            return {
                name: this.name.toString(),
                data: this.data.toJson()
            };
        }

        clone(): Metadata {
            return new Metadata(this.name, this.data.copy());
        }

        getName(): MetadataSchemaName {
            return this.name;
        }

        getData(): PropertyTree {
            return this.data;
        }
    }

}