module api_page{

    export class RegionItemFactory {

        static createRegionItem(regionItemJson:api_page_json.RegionItemJson):RegionItem {
            if (regionItemJson.regionItemType == "Layout") {
                return new Layout(<api_page_json.LayoutJson>regionItemJson);
            }
            else if (regionItemJson.regionItemType == "Component") {
                return ComponentFactory.createComponent(<api_page_json.ComponentJson>regionItemJson);
            }
            else {
                throw new Error("Unsupported RegionItem: " + regionItemJson.regionItemType);
            }
        }
    }
}