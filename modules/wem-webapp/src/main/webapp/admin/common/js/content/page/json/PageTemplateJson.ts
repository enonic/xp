module api.content.page.json{

    export interface PageTemplateJson extends PageTemplateSummaryJson {

        descriptor:PageDescriptorJson;

        config: api.data.json.DataTypeWrapperJson[];

        canRender: string[];

    }
}