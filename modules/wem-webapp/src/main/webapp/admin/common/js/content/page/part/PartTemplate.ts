module api_content_page_part{

    export class PartTemplate extends api_content_page.Template<PartTemplateKey,PartTemplateName> {

        constructor(builder:PartTemplateBuilder) {
            super(builder);
        }
    }

    export class PartTemplateBuilder extends api_content_page.TemplateBuilder<PartTemplateKey,PartTemplateName> {

        public build():PartTemplate {
            return new PartTemplate(this);
        }
    }
}