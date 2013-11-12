module api_content_page{

    export class PageTemplate extends Template<PageTemplateName> {

        constructor(builder:PageTemplateBuilder) {
            super(builder);
        }
    }

    export class PageTemplateBuilder extends TemplateBuilder<PageTemplateName> {

        public build():Template<PageTemplateName> {
            return new PageTemplate(this);
        }
    }
}