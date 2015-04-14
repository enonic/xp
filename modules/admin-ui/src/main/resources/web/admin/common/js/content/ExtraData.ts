module api.content {

    import MixinName = api.schema.mixin.MixinName;
    import PropertyTree = api.data.PropertyTree;
    import PropertyIdProvider = api.data.PropertyIdProvider;

    export class ExtraData implements api.Cloneable, api.Equitable {

        private name: MixinName;

        private data: PropertyTree;

        constructor(name: MixinName, data: PropertyTree) {
            this.name = name;
            this.data = data;
        }

        getName(): MixinName {
            return this.name;
        }

        getData(): PropertyTree {
            return this.data;
        }

        clone(): ExtraData {
            return new ExtraData(this.name, this.data.copy());
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ExtraData)) {
                return false;
            }

            var other = <ExtraData>o;

            if (!api.ObjectHelper.equals(this.name, other.name)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.data, other.data)) {
                return false;
            }

            return true;
        }

        toJson(): api.content.json.ExtraDataJson {
            return {
                name: this.name.toString(),
                data: this.data.toJson()
            };
        }

        static fromJson(metadataJson: api.content.json.ExtraDataJson, propertyIdProvider: PropertyIdProvider): ExtraData {
            return new ExtraData(new MixinName(metadataJson.name), PropertyTree.fromJson(metadataJson.data, propertyIdProvider));
        }

        
    }

}