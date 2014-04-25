module api.content {

    export class Content extends ContentSummary implements api.Equitable {

        private data: api.content.ContentData;

        private form: api.form.Form;

        private siteObj: api.content.site.Site;

        private pageObj: api.content.page.Page;

        constructor(builder: ContentBuilder) {
            super(builder);
            this.data = builder.data;
            this.form = builder.form;

            this.siteObj = builder.siteObj;
            this.pageObj = builder.pageObj;
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

        equals(o: api.Equitable): boolean {

            if (!(o instanceof Content)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <Content>o;

            if (!api.EquitableHelper.equals(this.data, other.data)) {
                return false;
            }

            if (!api.EquitableHelper.equals(this.form, other.form)) {
                return false;
            }

            if (!api.EquitableHelper.equals(this.pageObj, other.pageObj)) {
                return false;
            }

            if (!api.EquitableHelper.equals(this.siteObj, other.siteObj)) {
                return false;
            }

            return true;
        }

        static fromJson(json: api.content.json.ContentJson): Content {
            return new ContentBuilder().fromContentJson(json).build();
        }
    }

    export class ContentBuilder extends ContentSummaryBuilder {

        data: api.content.ContentData;

        form: api.form.Form;

        siteObj: api.content.site.Site;

        pageObj: api.content.page.Page;

        constructor(source?: Content) {
            super(source);
            if (source) {

                this.data = source.getContentData();
                this.form = source.getForm();
                this.siteObj = source.getSite();
                this.pageObj = source.getPage();
            }
        }

        fromContentJson(json: api.content.json.ContentJson): ContentBuilder {

            super.fromContentSummaryJson(json);

            this.data = ContentDataFactory.createContentData(json.data);
            this.form = json.form != null ? new api.form.Form(json.form) : null;

            if (this.site) {
                this.siteObj = new api.content.site.Site(json.site);
            }
            if (this.page) {
                this.pageObj = new api.content.page.PageBuilder().fromJson(json.page).build();
            }

            return this;
        }

        build(): Content {
            return new Content(this);
        }
    }
}