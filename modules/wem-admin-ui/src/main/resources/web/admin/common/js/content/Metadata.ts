module api.content {

    import MetadataSchemaName = api.schema.metadata.MetadataSchemaName;
    import RootDataSet = api.data.RootDataSet;

    export class Metadata {

        private name: MetadataSchemaName;

        private data: RootDataSet;

        constructor(name: MetadataSchemaName, data: RootDataSet) {
            this.name = name;
            this.data = data;
        }

        static fromJson(metadataJson: api.content.json.MetadataJson): Metadata {
            return new Metadata( new MetadataSchemaName(metadataJson.name), api.data.DataFactory.createRootDataSet(metadataJson.data));
        }

        toJson(): api.content.json.MetadataJson {
            return {
                name: this.name.toString(),
                data: this.data.toJson()
            };
        }

        getName(): MetadataSchemaName {
            return this.name;
        }

        getData(): RootDataSet {
            return this.data;
        }
    }

}