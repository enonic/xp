module api.content.page {

    export class Descriptor implements api.Cloneable {

        private key: DescriptorKey;

        private name: DescriptorName;

        private displayName: string;

        private config: api.form.Form;

        constructor(builder: DescriptorBuilder) {
            this.name = builder.name;
            this.key = builder.key;
            this.displayName = builder.displayName;
            this.config = builder.config;
        }

        getKey(): DescriptorKey {
            return this.key;
        }

        getName(): DescriptorName {
            return this.name;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getConfig(): api.form.Form {
            return this.config;
        }

        clone(): Descriptor {
            throw new Error("Must be implemented in inheritor");
        }
    }

    export class DescriptorBuilder {

        key: DescriptorKey;

        name: DescriptorName;

        displayName: string;

        config: api.form.Form;

        constructor(source?: Descriptor) {
            if (source) {
                this.key = source.getKey();
                this.name = source.getName();
                this.displayName = source.getDisplayName();
                this.config = source.getConfig();
            }
        }
    }
}