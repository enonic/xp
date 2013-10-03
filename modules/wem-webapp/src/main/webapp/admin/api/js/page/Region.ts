module api_page{

    export class Region {

        private name:string;

        private regionItems:RegionItem[];

        constructor(regionJson:api_page_json.RegionJson) {
            this.name = regionJson.name;
            regionJson.regionItems.forEach((regionItemJson:api_page_json.RegionItemJson) => {
                var regionItem = RegionItemFactory.createRegionItem(regionItemJson);
                this.regionItems.push(regionItem);
            });
        }

        getName():string {
            return this.name;
        }

        getRegionItems():RegionItem[] {
            return this.regionItems;
        }

    }
}