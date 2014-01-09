module api.content.page.region {

    export class Region {

        private name: string;

        private pageComponents: api.content.page.PageComponent<api.content.page.TemplateKey>[] = [];

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.pageComponents = builder.pageComponents;
        }

        getName(): string {
            return this.name;
        }

        getComponents(): api.content.page.PageComponent<api.content.page.TemplateKey>[] {
            return this.pageComponents;
        }
    }

    export class RegionBuilder {

        name: string;

        pageComponents: api.content.page.PageComponent<api.content.page.TemplateKey>[] = [];

        public setName(value: string): RegionBuilder {
            this.name = value;
            return this;
        }

        public addComponent(value: api.content.page.PageComponent<api.content.page.TemplateKey>): RegionBuilder {
            this.pageComponents.push(value);
            return this;
        }

        public build(): Region {
            return new Region(this);
        }
    }
}