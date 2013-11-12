module api_content_page{

    export class Component<TEMPLATE_NAME extends TemplateName> {

        private template:Template<TEMPLATE_NAME>;

        constructor(builder:ComponentBuilder<TEMPLATE_NAME>) {
            this.template = builder.template;
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