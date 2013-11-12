module api_content_page{

    export class PageTemplate extends Template<PageTemplateName> {

        private canRender:api_schema_content.ContentTypeName[];

        constructor(builder:PageTemplateBuilder) {
            super(builder);
            this.canRender = builder.canRender;
        }
    }

    export class PageTemplateBuilder extends TemplateBuilder<PageTemplateName> {

        canRender:api_schema_content.ContentTypeName[];

        setCanRender(value:api_schema_content.ContentTypeName[]):PageTemplateBuilder {
            this.canRender = value;
            return this;
        }

        public build():PageTemplate {
            return new PageTemplate(this);
        }
    }
}