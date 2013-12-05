module api_content{

    export class Content extends ContentSummary {

        private data:api_content.ContentData;

        private form:api_form.Form;

        private siteObj:api_content_site.Site;

        private pageObj:api_content_page.Page;

        constructor(json:api_content_json.ContentJson) {
            super(json);
            this.data = ContentDataFactory.createContentData(json.data);
            this.form = json.form != null ? new api_form.Form(json.form) : null;

            if( this.isSite() ){
                this.siteObj = new api_content_site.Site(json.site);
            }
            if( this.isPage() ) {
                this.pageObj = new api_content_page.PageBuilder().fromJson(json.page).build();
            }
        }

        getContentData():ContentData {
            return this.data;
        }

        getForm():api_form.Form {
            return this.form;
        }

        getPage():api_content_page.Page {
            return this.pageObj;
        }
    }
}