module api_content_page{

    export class BasePageComponent<TEMPLATE_NAME extends TemplateName> {

        private id:number;

        private template:Template<TEMPLATE_NAME>;

        constructor(builder?:ComponentBuilder<TEMPLATE_NAME>) {
            if( builder != undefined ) {
                this.template = builder.template;
            }
        }

        getTemplate():Template<TEMPLATE_NAME> {
            return this.template;
        }

        setTemplate(template:Template<TEMPLATE_NAME>) {
            this.template = template;
        }
    }


    export class ComponentBuilder<TEMPLATE_NAME extends TemplateName> {

        template:Template<TEMPLATE_NAME>;

        setTemplate(value:Template<TEMPLATE_NAME>):ComponentBuilder<TEMPLATE_NAME> {
            this.template = value;
            return this;
        }
    }
}