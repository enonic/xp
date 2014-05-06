module app.wizard {

    import Content = api.content.Content;
    import PageCUDRequest = api.content.page.PageCUDRequest;
    import CreatePageRequest = api.content.page.CreatePageRequest;
    import UpdatePageRequest = api.content.page.UpdatePageRequest;
    import DeletePageRequest = api.content.page.DeletePageRequest;
    import UpdateSiteRequest = api.content.site.UpdateSiteRequest;

    export class UpdatePersistedContentRoutineContext {

        content: Content = null;
    }

    export class UpdatePersistedContentRoutine extends api.util.Flow<Content,UpdatePersistedContentRoutineContext> {

        private persistedContent: Content;

        private viewedContent: Content;

        private updateContentRequestProducer: {(content: Content, viewedContent: Content) : api.content.UpdateContentRequest; };

        private doneHandledContent = false;

        private doneHandledSite = false;

        private doneHandledPage = false;

        constructor(thisOfProducer: any, persistedContent: Content, viewedContent: Content) {
            super(thisOfProducer);
            this.persistedContent = persistedContent;
            this.viewedContent = viewedContent;
        }

        public setUpdateContentRequestProducer(producer: {(content: Content,
                                                           viewedContent: Content) : api.content.UpdateContentRequest; }): UpdatePersistedContentRoutine {
            this.updateContentRequestProducer = producer;
            return this;
        }

        public execute(): Q.Promise<Content> {

            var context = new UpdatePersistedContentRoutineContext();
            context.content = this.persistedContent;
            return this.doExecute(context);
        }

        doExecuteNext(context: UpdatePersistedContentRoutineContext): Q.Promise<Content> {

            if (!this.doneHandledContent) {

                return this.doHandleUpdateContent(context).
                    then(() => {

                        this.doneHandledContent = true;
                        return this.doExecuteNext(context);

                    });
            }
            else if (!this.doneHandledSite) {

                return this.doHandleUpdateSite(context).
                    then(()=> {

                        this.doneHandledSite = true;
                        return this.doExecuteNext(context);

                    });
            }
            else if (!this.doneHandledPage) {

                return this.doHandlePage(context).
                    then(() => {

                        this.doneHandledPage = true;
                        return this.doExecuteNext(context);

                    });
            }
            else {

                return Q(context.content);
            }
        }

        private doHandleUpdateContent(context: UpdatePersistedContentRoutineContext): Q.Promise<void> {

            return this.updateContentRequestProducer.call(this.getThisOfProducer(), context.content, this.viewedContent).
                sendAndParse().
                then((content: Content): void => {

                    context.content = content;

                });
        }

        private doHandleUpdateSite(context: UpdatePersistedContentRoutineContext): Q.Promise<void> {

            var updateSiteRequest = this.produceUpdateSiteRequest(context.content, this.viewedContent);
            if (updateSiteRequest != null) {
                return updateSiteRequest.
                    sendAndParse().
                    then((content: Content): void => {

                        context.content = content;

                    });
            }
            else {
                var deferred = Q.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
        }

        private produceUpdateSiteRequest(persistedContent: Content, viewedContent: Content): UpdateSiteRequest {

            if (!viewedContent.isSite()) {
                return null;
            }

            var viewedSite = viewedContent.getSite();

            return new UpdateSiteRequest(persistedContent.getId()).
                setSiteTemplateKey(viewedSite.getTemplateKey()).
                setModuleConfigs(viewedSite.getModuleConfigs());
        }

        private doHandlePage(context: UpdatePersistedContentRoutineContext): Q.Promise<void> {

            var pageCUDRequest = this.producePageCUDRequest(context.content, this.viewedContent);

            if (pageCUDRequest != null) {
                return pageCUDRequest
                    .sendAndParse().
                    then((content: Content): void => {

                        context.content = content;

                    });
            }
            else {
                var deferred = Q.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
        }

        private producePageCUDRequest(persistedContent: Content, viewedContent: Content): PageCUDRequest {

            if (persistedContent.isPage() && !viewedContent.isPage()) {

                return new DeletePageRequest(persistedContent.getContentId());
            }
            else if (!persistedContent.isPage() && viewedContent.isPage()) {

                var viewedPage = viewedContent.getPage();
                return new CreatePageRequest(persistedContent.getContentId()).
                    setPageTemplateKey(viewedPage.getTemplate()).
                    setConfig(viewedPage.getConfig()).
                    setRegions(viewedPage.getRegions());
            }
            else if (persistedContent.isPage() && viewedContent.isPage()) {

                var viewedPage = viewedContent.getPage();
                return new UpdatePageRequest(persistedContent.getContentId()).
                    setPageTemplateKey((viewedPage.getTemplate())).
                    setConfig(viewedPage.getConfig()).
                    setRegions(viewedPage.getRegions());
            }
        }

    }
}