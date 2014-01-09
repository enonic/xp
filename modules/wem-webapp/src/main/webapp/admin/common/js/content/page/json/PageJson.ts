module api.content.page.json{

    export interface PageJson{

        template:string;

        regions: api.content.page.region.json.RegionJson[];

        config: api.data.json.DataTypeWrapperJson[];

    }
}