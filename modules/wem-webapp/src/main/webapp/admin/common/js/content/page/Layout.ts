module api_content_page{

    export class Layout extends Component {

        private regions:Region[];

        constructor(layoutJson:api_content_page_json.LayoutJson) {
            super();

            layoutJson.regions.forEach((regionJson:api_content_page_json.RegionJson) => {
                this.regions.push(new Region(regionJson));
            });
        }

        getRegions(): Region[] {
            return this.regions;
        }
    }
}