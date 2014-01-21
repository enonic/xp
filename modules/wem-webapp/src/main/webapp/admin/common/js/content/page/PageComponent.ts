module api.content.page {

    export class PageComponent<TEMPLATE_KEY extends TemplateKey> {

        private name: api.content.page.ComponentName;

        private template: TEMPLATE_KEY;

        private config: api.data.RootDataSet;

        constructor(builder?: PageComponentBuilder<TEMPLATE_KEY,any>) {
            if (builder != undefined) {
                this.name = builder.name;
                this.template = builder.template;
                this.config = builder.config;
            }
        }

        getName(): api.content.page.ComponentName {
            return this.name;
        }

        getTemplate(): TEMPLATE_KEY {
            return this.template;
        }

        setTemplate(template: TEMPLATE_KEY) {
            this.template = template;
        }

        setConfig(value: api.data.RootDataSet) {
            this.config = value;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }

        toJson(): api.content.page.json.PageComponentTypeWrapperJson {
            throw new Error("Must be implemented by inheritor: " + api.util.getClassName(this));
        }

        toPageComponentJson(): json.PageComponentJson {

            return {
                "name": this.name.toString(),
                "template": this.template != null ? this.template.toString() : null,
                "config": this.config != null ? this.config.toJson() : null
            };
        }
    }

    export class PageComponentBuilder<TEMPLATE_KEY extends TemplateKey,COMPONENT extends PageComponent> {

        name: api.content.page.ComponentName;

        template: TEMPLATE_KEY;

        config: api.data.RootDataSet;

        public setName(value: api.content.page.ComponentName): PageComponentBuilder<TEMPLATE_KEY,COMPONENT> {
            this.name = value;
            return this;
        }

        public setTemplate(value: TEMPLATE_KEY): PageComponentBuilder<TEMPLATE_KEY,COMPONENT> {
            this.template = value;
            return this;
        }

        public setConfig(value: api.data.RootDataSet): PageComponentBuilder<TEMPLATE_KEY,COMPONENT> {
            this.config = value;
            return this;
        }

        public build(): COMPONENT {
            throw new Error("Must be implemented by inheritor");
        }
    }
}