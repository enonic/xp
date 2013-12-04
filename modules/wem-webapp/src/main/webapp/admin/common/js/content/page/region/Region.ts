module api_content_page_region{

    export class Region{

        private name:string;

        private pageComponents:api_content_page.BasePageComponent<api_content_page.TemplateKey,api_content_page.TemplateName>[] = [];

        constructor() {

        }

    }
}