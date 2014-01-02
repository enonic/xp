module api.content {

    export class Content extends ContentSummary {

        private data: api.content.ContentData;

        private form: api.form.Form;

        private siteObj: api.content.site.Site;

        private pageObj: api.content.page.Page;

        constructor(json: api.content.json.ContentJson) {
            super(json);
            this.data = ContentDataFactory.createContentData(json.data);
            this.form = json.form != null ? new api.form.Form(json.form) : null;

            if (this.isSite()) {
                this.siteObj = new api.content.site.Site(json.site);
            }
            if (this.isPage()) {
                this.pageObj = new api.content.page.PageBuilder().fromJson(json.page).build();
            }
        }

        getContentData(): ContentData {
            return this.data;
        }

        getForm(): api.form.Form {
            return this.form;
        }

        getSite(): api.content.site.Site {
            return this.siteObj;
        }

        getPage(): api.content.page.Page {
            return this.pageObj;
        }
    }
}