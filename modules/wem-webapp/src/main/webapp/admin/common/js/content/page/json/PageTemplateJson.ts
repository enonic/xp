module api_content_page_json{

    export interface PageTemplateJson{

        key:string;

        name:string;

        displayName:string;

        descriptor:string;

        config: api_data_json.DataTypeWrapperJson[];

        canRender: string[];

    }
}