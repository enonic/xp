module api_content_page_layout{

    export class LayoutTemplate extends api_content_page.Template<LayoutTemplateKey,LayoutTemplateName> {

        constructor(builder:LayoutTemplateBuilder) {
            super(builder);
        }
    }

    export class LayoutTemplateBuilder extends api_content_page.TemplateBuilder<LayoutTemplateKey,LayoutTemplateName> {

        public build():LayoutTemplate {
            return new LayoutTemplate(this);
        }
    }
}