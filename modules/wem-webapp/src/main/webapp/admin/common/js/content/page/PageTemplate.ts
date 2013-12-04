module api_content_page{

    export class PageTemplate extends Template<PageTemplateKey,PageTemplateName> {

        private canRender:api_schema_content.ContentTypeName[];

        constructor(builder:PageTemplateBuilder) {
            super(builder);
            this.canRender = builder.canRender;
        }

        getCanRender():api_schema_content.ContentTypeName[] {
            return this.canRender;
        }
    }

    export class PageTemplateBuilder extends TemplateBuilder<PageTemplateKey,PageTemplateName> {

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