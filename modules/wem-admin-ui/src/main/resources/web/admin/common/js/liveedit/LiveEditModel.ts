module api.liveedit {

    import Content = api.content.Content;
    import PageModel = api.content.page.PageModel;
    import PageTemplate = api.content.page.PageTemplate;
    import SiteModel = api.content.site.SiteModel;

    export class LiveEditModel {

        private content: Content;

        private siteModel: SiteModel;

        private pageModel: PageModel;

        constructor(siteModel: SiteModel) {
            console.log("LiveEditModel.constructor");
            this.siteModel = siteModel;
        }

        init(value: Content, defaultPageTemplate: PageTemplate) {
            console.log("LiveEditModel.init");
            this.content = value;
            this.pageModel = new PageModel(this, defaultPageTemplate);
            this.pageModel.initialize();
        }


        setContent(value: Content) {
            console.log("LiveEditModel.setContent");
            this.content = value;
        }

        getContent(): Content {
            return this.content;
        }

        getSiteModel(): SiteModel {
            return this.siteModel;
        }

        getPageModel(): PageModel {
            return this.pageModel;
        }
    }
}