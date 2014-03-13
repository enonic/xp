module api.content.page.layout {

    export interface LayoutComponentJson extends api.content.page.PageComponentJson {

        name:string;

        config:api.data.json.DataTypeWrapperJson[];

        regions: api.content.page.region.RegionJson[];
    }
}