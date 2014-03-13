module api.content.page {

    export interface PageJson{

        template:string;

        regions: api.content.page.region.RegionJson[];

        config: api.data.json.DataTypeWrapperJson[];

    }
}