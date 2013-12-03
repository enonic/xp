module api_content_page_region{

    export class RegionResolver extends api_data.PropertyVisitor{

        private data:api_data.RootDataSet;

        private regionProperties:api_data.Property[] = [];

        constructor(data:api_data.RootDataSet) {
            super();
            this.data = data;
            this.restrictType(api_data.ValueTypes.REGION);
        }

        public visit( property:api_data.Property ) {
            this.regionProperties.push( property );
        }

        resolve():Region[] {

            this.traverse(this.data.getDataArray());
            var regions:Region[] = [];
            this.regionProperties.forEach((property:api_data.Property) => {
                var region = new Region();
                // TODO: parse region from property
                regions.push(region);
            });

            return regions;
        }
    }


}