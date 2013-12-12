module api_form_inputtype {

    export interface InputTypeViewConfig<C> {

        contentId: api_content.ContentId;

        contentPath: api_content.ContentPath;

        parentContentPath: api_content.ContentPath;

        dataPath:api_data.DataPath;

        inputConfig:C;
    }
}