module api.content.page.region {

    export class Region {

        private name: string;

        private pageComponents: api.content.page.BasePageComponent<api.content.page.TemplateKey>[] = [];

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.pageComponents = builder.pageComponents;
        }

    }

    export class RegionBuilder {

        name: string;

        pageComponents: api.content.page.BasePageComponent<api.content.page.TemplateKey>[] = [];

        public fromRootDataSet(data: api.data.RootDataSet): RegionBuilder {

            this.name = data.getProperty("name").getString();

            data.getDataSets().forEach((dataSet: api.data.DataSet) => {
                var component = RegionPlaceableComponentFactory.create(dataSet);
                this.pageComponents.push(component);
            });
            return this;
        }

        public build(): Region {
            return new Region(this);
        }
    }
}