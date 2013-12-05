module api_content_page_region {

    export class Region {

        private name: string;

        private pageComponents: api_content_page.BasePageComponent<api_content_page.TemplateKey>[] = [];

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.pageComponents = builder.pageComponents;
        }

    }

    export class RegionBuilder {

        name: string;

        pageComponents: api_content_page.BasePageComponent<api_content_page.TemplateKey>[] = [];

        public fromRootDataSet(data: api_data.RootDataSet): RegionBuilder {

            this.name = data.getProperty("name").getString();

            data.getDataSets().forEach((dataSet: api_data.DataSet) => {
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