module api.content {

    export class Content extends ContentSummary implements api.Equitable, api.Cloneable {

        private data: api.content.ContentData;

        private metadataByName: {[name: string]: api.data.RootDataSet} = {};

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

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Content)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <Content>o;

            if (!api.ObjectHelper.equals(this.data, other.data)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.form, other.form)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.pageObj, other.pageObj)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.siteObj, other.siteObj)) {
                return false;
            }

            return true;
        }

        clone(): Content {
            return new ContentBuilder(this).build();
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

                this.data = source.getContentData() ? source.getContentData().clone() : null;
                this.form = source.getForm();
                this.siteObj = source.getSite() ? source.getSite().clone() : null;
                if (this.siteObj) {
                    this.site = true;
                }
                this.pageObj = source.getPage() ? source.getPage().clone() : null;
                if (this.pageObj) {

                }
            }
        }

        fromContentJson(json: api.content.json.ContentJson): ContentBuilder {

            super.fromContentSummaryJson(json);

            this.data = ContentDataFactory.createContentData(json.data);
            this.form = json.form != null ? api.form.Form.fromJson(json.form) : null;

            if (this.site) {
                this.siteObj = api.content.site.Site.fromJson(json.site);
                this.site = true;
            }
            if (this.page) {
                this.pageObj = new api.content.page.PageBuilder().fromJson(json.page).build();
                this.page = true;
            }

            return this;
        }

        setData(value: ContentData): ContentBuilder {
            this.data = value;
            return this;
        }

        setForm(value: api.form.Form): ContentBuilder {
            this.form = value;
            return this;
        }

        setSite(value: api.content.site.Site): ContentBuilder {
            this.siteObj = value;
            this.site = value ? true : false;
            return this;
        }

        setPage(value: api.content.page.Page): ContentBuilder {
            this.pageObj = value;
            this.page = value ? true : false;
            return this;
        }

        build(): Content {
            return new Content(this);
        }
    }
}