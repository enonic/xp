module api_content_page_region {

    export class RegionResolver extends api_data.PropertyVisitor {

        private data: api_data.RootDataSet;

        private regionProperties: api_data.Property[] = [];

        constructor(data: api_data.RootDataSet) {
            super();
            this.data = data;
            this.restrictType(api_data.ValueTypes.DATA);
        }

        public visit(property: api_data.Property) {
            this.regionProperties.push(property);
        }

        resolve(): Region[] {

            this.traverse(this.data.getDataArray());
            var regions: Region[] = [];
            this.regionProperties.forEach((property: api_data.Property) => {

                var value: api_data.RootDataSet = property.getValue().asRootDataSet();
                var region: Region = new RegionBuilder().fromRootDataSet(value).build();
                regions.push(region);
            });

            return regions;
        }
    }


}