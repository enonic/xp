module app.wizard {

    import Content = api.content.Content;
    import Site = api.content.site.Site;
    import PageCUDRequest = api.content.page.PageCUDRequest;
    import CreatePageRequest = api.content.page.CreatePageRequest;
    import UpdatePageRequest = api.content.page.UpdatePageRequest;
    import DeletePageRequest = api.content.page.DeletePageRequest;

    export class UpdatePersistedContentRoutineContext {

        content: Content = null;
    }

    export class UpdatePersistedContentRoutine extends api.util.Flow<Content,UpdatePersistedContentRoutineContext> {

        private persistedContent: Content;

        private viewedContent: Content;

        private updateContentRequestProducer: {(content: Content, viewedContent: Content) : api.content.UpdateContentRequest; };

        private doneHandledContent = false;

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

        public execute(): wemQ.Promise<Content> {

            var context = new UpdatePersistedContentRoutineContext();
            context.content = this.persistedContent;
            return this.doExecute(context);
        }

        doExecuteNext(context: UpdatePersistedContentRoutineContext): wemQ.Promise<Content> {

            if (!this.doneHandledContent) {

                return this.doHandleUpdateContent(context).
                    then(() => {

                        this.doneHandledContent = true;
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

                return wemQ(context.content);
            }
        }

        private doHandleUpdateContent(context: UpdatePersistedContentRoutineContext): wemQ.Promise<void> {

            return this.updateContentRequestProducer.call(this.getThisOfProducer(), context.content, this.viewedContent).
                sendAndParse().
                then((content: Content): void => {

                    context.content = content;

                });
        }

        private doHandlePage(context: UpdatePersistedContentRoutineContext): wemQ.Promise<void> {

            var pageCUDRequest = this.producePageCUDRequest(context.content, this.viewedContent);

            if (pageCUDRequest != null) {
                return pageCUDRequest
                    .sendAndParse().
                    then((content: Content): void => {

                        context.content = content;

                    });
            }
            else {
                var deferred = wemQ.defer<void>();
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
                    setController(viewedPage.getController()).
                    setPageTemplateKey(viewedPage.getTemplate()).
                    setConfig(viewedPage.getConfig()).
                    setRegions(viewedPage.getRegions()).
                    setFragment(viewedPage.getFragment()).
                    setCustomized(viewedPage.isCustomized());
            }
            else if (persistedContent.isPage() && viewedContent.isPage()) {

                var viewedPage = viewedContent.getPage();
                return new UpdatePageRequest(persistedContent.getContentId()).
                    setController((viewedPage.getController())).
                    setPageTemplateKey((viewedPage.getTemplate())).
                    setConfig(viewedPage.getConfig()).
                    setRegions(viewedPage.getRegions()).
                    setFragment(viewedPage.getFragment()).
                    setCustomized(viewedPage.isCustomized());
            }
        }

    }
}