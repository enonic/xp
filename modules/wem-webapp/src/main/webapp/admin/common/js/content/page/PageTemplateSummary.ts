module api.content.page {

    export class PageTemplateSummary {

        private key: PageTemplateKey;

        private displayName: string;

        private descriptorKey: DescriptorKey;

        constructor(builder: PageTemplateSummaryBuilder) {

            this.key = builder.key;
            this.displayName = builder.displayName;
            this.descriptorKey = builder.descriptorKey;
        }

        getKey(): PageTemplateKey {
            return this.key;
        }

        getName(): PageTemplateName {
            return this.key.getTemplateName();
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getDescriptorKey(): DescriptorKey {
            return this.descriptorKey;
        }

        static fromJsonArray(jsonArray: json.PageTemplateSummaryJson[]): PageTemplateSummary[] {
            var array: PageTemplateSummary[] = [];

            jsonArray.forEach((summaryJson: json.PageTemplateSummaryJson) => {
                array.push(new PageTemplateSummaryBuilder().fromJson(summaryJson).build());
            });
            return array;
        }
    }

    export class PageTemplateSummaryBuilder {

        key: PageTemplateKey;

        displayName: string;

        descriptorKey: DescriptorKey;

        fromJson(json: api.content.page.json.PageTemplateSummaryJson): PageTemplateSummaryBuilder {

            this.setKey(PageTemplateKey.fromString(json.key));
            this.setDisplayName(json.displayName);
            this.setDescriptorKey(DescriptorKey.fromString(json.descriptorKey));
            return this;
        }

        public setKey(value: PageTemplateKey): PageTemplateSummaryBuilder {
            this.key = value;
            return this;
        }

        public setDisplayName(value: string): PageTemplateSummaryBuilder {
            this.displayName = value;
            return this;
        }

        public setDescriptorKey(value: DescriptorKey): PageTemplateSummaryBuilder {
            this.descriptorKey = value;
            return this;
        }

        public build(): PageTemplateSummary {
            return new PageTemplateSummary(this);
        }
    }
}