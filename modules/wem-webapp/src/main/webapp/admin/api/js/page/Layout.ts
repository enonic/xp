module api_page{

    export class Layout extends RegionItem {

        private regions:Region[];

        constructor(layoutJson:api_page_json.LayoutJson) {
            super();

            layoutJson.regions.forEach((regionJson:api_page_json.RegionJson) => {
                this.regions.push(new Region(regionJson));
            });
        }

        getRegions(): Region[] {
            return this.regions;
        }
    }
}