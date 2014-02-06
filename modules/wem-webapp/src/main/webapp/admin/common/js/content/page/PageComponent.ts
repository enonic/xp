module api.content.page {

    export class PageComponent {

        private name: ComponentName;

        private region: RegionPath;

        private path: ComponentPath;

        private template: PageTemplateKey;

        private config: api.data.RootDataSet;

        constructor(builder?: PageComponentBuilder<any>) {
            if (builder != undefined) {
                this.name = builder.name;
                this.template = builder.template;
                this.config = builder.config;
                this.region = builder.region;
                this.path = ComponentPath.fromRegionPathAndComponentName(this.region, this.name);
            }
        }

        setPath(path: ComponentPath) {
            this.path = path;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getName(): api.content.page.ComponentName {
            return this.name;
        }

        getTemplate(): PageTemplateKey {
            return this.template;
        }

        setTemplate(template: PageTemplateKey) {
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

        template: PageTemplateKey;

        config: api.data.RootDataSet;

        region: RegionPath;

        public setName(value: api.content.page.ComponentName): PageComponentBuilder<COMPONENT> {
            this.name = value;
            return this;
        }

        public setTemplate(value: PageTemplateKey): PageComponentBuilder<COMPONENT> {
            this.template = value;
            return this;
        }

        public setConfig(value: api.data.RootDataSet): PageComponentBuilder<COMPONENT> {
            this.config = value;
            return this;
        }

        public setRegion(value: api.content.page.RegionPath): PageComponentBuilder<COMPONENT> {
            this.region = value;
            return this;
        }

        public build(): COMPONENT {
            throw new Error("Must be implemented by inheritor");
        }
    }
}