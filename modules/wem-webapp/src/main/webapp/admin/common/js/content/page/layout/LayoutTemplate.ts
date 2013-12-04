module api_content_page_layout {

    export class LayoutTemplate extends api_content_page.Template<LayoutTemplateName> {

        constructor(builder:LayoutTemplateBuilder) {
            super(builder);
        }
    }

    export class LayoutTemplateBuilder extends api_content_page.TemplateBuilder<LayoutTemplateName> {

        public build():api_content_page.Template<LayoutTemplateName> {
            return new LayoutTemplate(this);
        }
    }
}