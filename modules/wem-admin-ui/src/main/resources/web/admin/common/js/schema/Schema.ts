module api.schema {

    export class Schema extends api.item.BaseItem {

        private name: string;

        private displayName: string;

        private description: string;

        private iconUrl: string;

        constructor(builder: SchemaBuilder) {
            super(builder);
            this.name = builder.name;
            this.displayName = builder.displayName;
            this.description = builder.description;
            this.iconUrl = builder.iconUrl;
        }

        getName(): string {
            return this.name;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getDescription(): string {
            return this.description;
        }

        getIconUrl(): string {
            return this.iconUrl;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Schema)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <Schema>o;

            if (!ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            if (!ObjectHelper.stringEquals(this.displayName, other.displayName)) {
                return false;
            }

            if (!ObjectHelper.stringEquals(this.iconUrl, other.iconUrl)) {
                return false;
            }

            return true;
        }

        static fromJson(json: api.schema.SchemaJson): Schema {
            return new SchemaBuilder().fromSchemaJson(json).build();
        }
    }

    export class SchemaBuilder extends api.item.BaseItemBuilder {

        name: string;

        displayName: string;

        description: string;

        iconUrl: string;

        constructor(source?: Schema) {
            if (source) {
                super(source);
                this.name = source.getName();
                this.displayName = source.getDisplayName();
                this.description = source.getDescription();
                this.iconUrl = source.getIconUrl();
            }
        }

        fromSchemaJson(json: api.schema.SchemaJson): SchemaBuilder {
            super.fromBaseItemJson(json, "name");

            this.name = json.name;
            this.displayName = json.displayName;
            this.description = json.description;
            this.iconUrl = json.iconUrl;
            return this;
        }

        setName(value: string): SchemaBuilder {
            this.name = value;
            return this;
        }

        build(): Schema {
            return new Schema(this);
        }
    }


}