module api.content.page {

    export class PageComponent {

        private name: ComponentName;

        private path: ComponentPath;

        private template: TemplateKey;

        private config: api.data.RootDataSet;

        constructor(builder?: PageComponentBuilder<any>) {
            if (builder != undefined) {
                this.name = builder.name;
                this.template = builder.template;
                this.config = builder.config;
            }
        }

        setComponentPath(path: ComponentPath) {
            this.path = path;
        }

        getName(): api.content.page.ComponentName {
            return this.name;
        }

        getTemplate(): TemplateKey {
            return this.template;
        }

        setTemplate(template: TemplateKey) {
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

    export class PageComponentBuilder<COMPONENT extends PageComponent> {

        name: api.content.page.ComponentName;

        template: TemplateKey;

        config: api.data.RootDataSet;

        public setName(value: api.content.page.ComponentName): PageComponentBuilder<COMPONENT> {
            this.name = value;
            return this;
        }

        public setTemplate(value: TemplateKey): PageComponentBuilder<COMPONENT> {
            this.template = value;
            return this;
        }

        public setConfig(value: api.data.RootDataSet): PageComponentBuilder<COMPONENT> {
            this.config = value;
            return this;
        }

        public build(): COMPONENT {
            throw new Error("Must be implemented by inheritor");
        }
    }
}