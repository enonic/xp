module api_content_page{

    export class LayoutTemplate extends Template<LayoutTemplateName> {

        constructor(builder:LayoutTemplateBuilder) {
            super(builder);
        }
    }

    export class LayoutTemplateBuilder extends TemplateBuilder<LayoutTemplateName> {

        public build():Template<LayoutTemplateName> {
            return new LayoutTemplate(this);
        }
    }
}