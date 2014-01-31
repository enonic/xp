module api.content.page {

    export class TemplateSummary {

        private key: TemplateKey;

        private displayName: string;

        private descriptorKey: DescriptorKey;

        constructor(builder: TemplateSummaryBuilder) {
            this.key = builder.key;
            this.displayName = builder.displayName;
            this.descriptorKey = builder.descriptorKey;
        }

        getKey(): TemplateKey {
            return this.key;
        }

        getName(): TemplateName {
            return this.key.getTemplateName();
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getDescriptorKey(): DescriptorKey {
            return this.descriptorKey;
        }

        static fromJsonArray(jsonArray:any[]):any[] {
            throw new Error("Overridden by subclasses");
        }
    }

    export class TemplateSummaryBuilder {

        key: TemplateKey;

        displayName: string;

        descriptorKey: DescriptorKey;

        public setKey(value: TemplateKey): TemplateSummaryBuilder {
            this.key = value;
            return this;
        }

        public setDisplayName(value: string): TemplateSummaryBuilder {
            this.displayName = value;
            return this;
        }

        public setDescriptorKey(value: DescriptorKey): TemplateSummaryBuilder {
            this.descriptorKey = value;
            return this;
        }

    }
}