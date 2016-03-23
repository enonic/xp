module api.content.page {

    export interface PageJson {

        controller:string;

        template:string;

        regions: api.content.page.region.RegionJson[];

        fragment: api.content.page.region.ComponentJson;

        config: api.data.PropertyArrayJson[];

        customized: boolean;

    }
}