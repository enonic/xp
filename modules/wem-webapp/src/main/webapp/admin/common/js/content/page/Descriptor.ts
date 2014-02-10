module api.content.page {

    export class Descriptor {

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
    }

    export class DescriptorBuilder {

        key: DescriptorKey;

        name: DescriptorName;

        displayName: string;

        config: api.form.Form;
    }
}