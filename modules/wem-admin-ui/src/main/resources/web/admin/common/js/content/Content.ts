module api.content {

    export class Content extends ContentSummary implements api.Equitable, api.Cloneable {

        private data: api.content.ContentData;

        private metadata: Metadata[] = [];

        private form: api.form.Form;

        private pageObj: api.content.page.Page;

        constructor(builder: ContentBuilder) {
            super(builder);
            this.data = builder.data;
            this.form = builder.form;

            this.pageObj = builder.pageObj;
        }

        getContentData(): ContentData {
            return this.data;
        }

        getMetadata(name: api.schema.metadata.MetadataSchemaName): Metadata {
            return this.metadata.filter((item: Metadata) => item.getName().equals(name))[0];
        }

        getAllMetadata(): Metadata[] {
            return this.metadata;
        }

        getForm(): api.form.Form {
            return this.form;
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

            return true;
        }

        clone(): Content {
            return this.newBuilder().build();
        }

        newBuilder(): ContentBuilder {
            return new ContentBuilder(this);
        }

        static fromJson(json: api.content.json.ContentJson): Content {

            var type = new api.schema.content.ContentTypeName(json.type);

            if (type.isSite()) {
                return new site.SiteBuilder().fromContentJson(json).build();
            }
            else if (type.isPageTemplate()) {
                return new page.PageTemplateBuilder().fromContentJson(json).build();
            }
            return new ContentBuilder().fromContentJson(json).build();
        }
    }

    export class ContentBuilder extends ContentSummaryBuilder {

        data: api.content.ContentData;

        form: api.form.Form;

        metadata: Metadata[];

        pageObj: api.content.page.Page;

        constructor(source?: Content) {
            super(source);
            if (source) {

                this.data = source.getContentData() ? source.getContentData().clone() : null;
                this.form = source.getForm();
                this.pageObj = source.getPage() ? source.getPage().clone() : null;
                if (this.pageObj) {

                }
            }
        }

        fromContentJson(json: api.content.json.ContentJson): ContentBuilder {

            super.fromContentSummaryJson(json);

            this.data = ContentDataFactory.createContentData(json.data);
            this.metadata = json.metadata.map(Metadata.fromJson);
            this.form = json.form != null ? api.form.Form.fromJson(json.form) : null;

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