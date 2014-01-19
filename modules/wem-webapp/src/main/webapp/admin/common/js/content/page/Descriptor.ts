module api.content.page {

    export class Descriptor {

        private key:string;

        private name: DescriptorName;

        private displayName: string;

        private config: api.form.Form;

        constructor(builder: DescriptorBuilder) {
            this.name = builder.name;
            this.displayName = builder.displayName;
            this.config = builder.config;
        }

        getKey(): string {
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

        key: string;

        name: DescriptorName;

        displayName: string;

        config: api.form.Form;
    }
}