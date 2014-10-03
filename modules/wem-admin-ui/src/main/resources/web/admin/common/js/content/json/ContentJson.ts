module api.content.json{

    export interface ContentJson  extends ContentSummaryJson {

        data: api.data.json.DataTypeWrapperJson[];

        metadata: api.content.json.MetadataJson[];

        form: api.form.json.FormJson;

        site: api.content.site.SiteJson;

        page: api.content.page.PageJson;
    }
}