module api.content.page {

    export interface PageJson {

        controller:string;

        template:string;

        regions: api.content.page.region.RegionJson[];

        config: api.data2.PropertyArrayJson[];

    }
}