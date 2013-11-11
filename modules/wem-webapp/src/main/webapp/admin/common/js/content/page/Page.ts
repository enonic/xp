module api_content_page{

    export class Page {

        private regionsByName:{[name:string] : Region; } = {};

        constructor(pageJson:api_content_page_json.PageJson) {

            pageJson.regions.forEach((regionJson:api_content_page_json.RegionJson) => {
                var region = new Region(regionJson);
                this.regionsByName[region.getName()] = region;
            });
        }

    }
}