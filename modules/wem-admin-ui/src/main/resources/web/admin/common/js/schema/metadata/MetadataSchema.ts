module api.schema.metadata {

    export class MetadataSchema extends api.schema.Schema implements api.Equitable {

        private schemaKey: string;

        private form: api.form.Form;

        constructor(builder: MetadataSchemaBuilder) {
            super(builder);
            this.form = builder.form;
            this.schemaKey = builder.schemaKey;
        }

        getMetadataSchemaName(): MetadataSchemaName {
            return new MetadataSchemaName(this.getName());
        }

        getForm(): api.form.Form {
            return this.form;
        }

        getSchemaKey(): string {
            return this.schemaKey;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, MetadataSchema)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <MetadataSchema>o;


            if (!api.ObjectHelper.stringEquals(this.schemaKey, other.schemaKey)) {
                return false;
            }

            if (!api.ObjectHelper.objectEquals(this.form, other.form)) {
                return false;
            }

            return true;
        }

        static fromJson(json: api.schema.metadata.MetadataSchemaJson): MetadataSchema {
            return new MetadataSchemaBuilder().fromMetadataSchemaJson(json).build();
        }

    }

    export class MetadataSchemaBuilder extends api.schema.SchemaBuilder {

        schemaKey: string;

        form: api.form.Form;

        constructor(source?: MetadataSchema) {
            super(source);
            if (source) {
                this.schemaKey = source.getSchemaKey();
                this.form = source.getForm();
            }
        }

        fromMetadataSchemaJson(metadataSchemaJson: MetadataSchemaJson): MetadataSchemaBuilder {

            super.fromSchemaJson(metadataSchemaJson);
            this.form = api.form.FormItemFactory.createForm(metadataSchemaJson.form);
            this.schemaKey = "metadataSchema:" + this.name;
            return this;
        }

        build(): MetadataSchema {
            return new MetadataSchema(this);
        }

    }
}