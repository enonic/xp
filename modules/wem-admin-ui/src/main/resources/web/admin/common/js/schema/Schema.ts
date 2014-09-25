module api.schema {

    export class Schema extends api.item.BaseItem {

        private key: string;

        private name: string;

        private displayName: string;

        private description: string;

        private iconUrl: string;

        private kind: SchemaKind;

        constructor(builder: SchemaBuilder) {
            super(builder);
            this.key = builder.key;
            this.name = builder.name;
            this.displayName = builder.displayName;
            this.description = builder.description;
            this.iconUrl = builder.iconUrl;
            this.kind = builder.kind;
        }

        getKey(): string {
            return this.key;
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

        getSchemaKind(): SchemaKind {
            return this.kind;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Schema)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <Schema>o;

            if (!ObjectHelper.stringEquals(this.key, other.key)) {
                return false;
            }

            if (!ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            if (!ObjectHelper.stringEquals(this.displayName, other.displayName)) {
                return false;
            }

            if (!ObjectHelper.stringEquals(this.iconUrl, other.iconUrl)) {
                return false;
            }

            if (!ObjectHelper.equals(this.kind, other.kind)) {
                return false;
            }

            return true;
        }

        static fromJson(json: api.schema.SchemaJson): Schema {
            return new SchemaBuilder().fromSchemaJson(json).build();
        }
    }

    export class SchemaBuilder extends api.item.BaseItemBuilder {

        key: string;

        name: string;

        displayName: string;

        description: string;

        iconUrl: string;

        kind: SchemaKind;

        constructor(source?: Schema) {
            if (source) {
                super(source);
                this.key = source.getKey();
                this.name = source.getName();
                this.displayName = source.getDisplayName();
                this.description = source.getDescription();
                this.iconUrl = source.getIconUrl();
                this.kind = source.getSchemaKind();
            }
        }

        fromSchemaJson(json: api.schema.SchemaJson): SchemaBuilder {
            super.fromBaseItemJson(json, "key");

            this.key = json.key;
            this.name = json.name;
            this.displayName = json.displayName;
            this.description = json.description;
            this.iconUrl = json.iconUrl;
            this.kind = SchemaKind.fromString(json.schemaKind);
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