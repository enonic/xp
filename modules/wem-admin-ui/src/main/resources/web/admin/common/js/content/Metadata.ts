module api.content {

    import MixinName = api.schema.mixin.MixinName;
    import PropertyTree = api.data.PropertyTree;
    import PropertyIdProvider = api.data.PropertyIdProvider;

    export class Metadata implements api.Cloneable {

        private name: MixinName;

        private data: PropertyTree;

        constructor(name: MixinName, data: PropertyTree) {
            this.name = name;
            this.data = data;
        }

        static fromJson(metadataJson: api.content.json.MetadataJson, propertyIdProvider: PropertyIdProvider): Metadata {
            return new Metadata(new MixinName(metadataJson.name), PropertyTree.fromJson(metadataJson.data, propertyIdProvider));
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

        getName(): MixinName {
            return this.name;
        }

        getData(): PropertyTree {
            return this.data;
        }
    }

}