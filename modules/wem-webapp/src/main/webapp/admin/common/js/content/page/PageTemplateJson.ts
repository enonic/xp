module api.content.page {

    export interface PageTemplateJson extends PageTemplateSummaryJson {

        regions: api.content.page.region.RegionJson[];

        config: api.data.json.DataTypeWrapperJson[];

        canRender: string[];

    }
}