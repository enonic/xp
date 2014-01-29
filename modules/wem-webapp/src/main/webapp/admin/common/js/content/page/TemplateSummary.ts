module api.content.page {

    export class TemplateSummary<KEY extends TemplateKey,NAME extends TemplateName> {

        private key: KEY;

        private name: NAME;

        private displayName: string;

        private descriptorKey: DescriptorKey;

        constructor(builder: TemplateSummaryBuilder<KEY,NAME>) {
            this.key = builder.key;
            this.name = <NAME>this.key.getTemplateName();
            this.displayName = builder.displayName;
            this.descriptorKey = builder.descriptorKey;
        }

        getKey(): KEY {
            return this.key;
        }

        getName(): NAME {
            return this.name;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getDescriptorKey(): DescriptorKey {
            return this.descriptorKey;
        }
    }

    export class TemplateSummaryBuilder<KEY extends TemplateKey,NAME extends TemplateName> {

        key: KEY;

        displayName: string;

        descriptorKey: DescriptorKey;

        public setKey(value: KEY): TemplateSummaryBuilder<KEY,NAME> {
            this.key = value;
            return this;
        }

        public setDisplayName(value: string): TemplateSummaryBuilder<KEY,NAME> {
            this.displayName = value;
            return this;
        }

        public setDescriptorKey(value: DescriptorKey): TemplateSummaryBuilder<KEY,NAME> {
            this.descriptorKey = value;
            return this;
        }

    }
}