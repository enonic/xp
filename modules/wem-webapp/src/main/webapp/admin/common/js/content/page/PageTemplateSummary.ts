module api.content.page {

    export class PageTemplateSummary extends TemplateSummary {

        constructor(builder: PageTemplateSummaryBuilder) {
            super(builder);
        }

        static fromJsonArray(jsonArray:json.PageTemplateSummaryJson[]):PageTemplateSummary[] {
            var array:PageTemplateSummary[] = [];

            jsonArray.forEach( (summaryJson:json.PageTemplateSummaryJson) => {
                array.push(new PageTemplateSummaryBuilder().fromJson(summaryJson).build());
            });
            return array;
        }
    }

    export class PageTemplateSummaryBuilder extends TemplateSummaryBuilder {

        fromJson(json: api.content.page.json.PageTemplateSummaryJson): PageTemplateSummaryBuilder {

            this.setKey(TemplateKey.fromString(json.key));
            this.setDisplayName(json.displayName);
            this.setDescriptorKey(DescriptorKey.fromString(json.descriptorKey));
            return this;
        }

        public build(): PageTemplateSummary {
            return new PageTemplateSummary(this);
        }
    }
}