module api.content.page.region.json {

    export interface RegionJson {

        name: string;

        components: api.content.page.json.PageComponentTypeWrapperJson[];
    }

}