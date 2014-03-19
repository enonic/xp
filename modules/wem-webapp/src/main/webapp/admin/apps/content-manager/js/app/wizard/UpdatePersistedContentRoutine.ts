module app.wizard {

    export class UpdatePersistedContentRoutineContext {

        content: api.content.Content = null;
    }

    export class UpdatePersistedContentRoutine extends api.util.Flow<api.content.Content,UpdatePersistedContentRoutineContext> {

        private updateContentRequestProducer: {(content: api.content.Content) : api.content.UpdateContentRequest; };

        private doneHandledContent = false;

        private updateSiteRequestProducer: {(content: api.content.Content) : api.content.site.UpdateSiteRequest; };

        private doneHandledSite = false;

        private pageCUDRequestProducer: {(content: api.content.Content) : api.content.page.PageCUDRequest; };

        private doneHandledPage = false;

        constructor(thisOfProducer: ContentWizardPanel) {
            super(thisOfProducer);
        }

        public setUpdateContentRequestProducer(producer: {(content: api.content.Content) : api.content.UpdateContentRequest; }): UpdatePersistedContentRoutine {
            this.updateContentRequestProducer = producer;
            return this;
        }

        public setUpdateSiteRequestProducer(producer: {(content: api.content.Content) : api.content.site.UpdateSiteRequest; }): UpdatePersistedContentRoutine {
            this.updateSiteRequestProducer = producer;
            return this;
        }

        public setPageCUDRequestProducer(producer: {(content: api.content.Content) : api.content.page.PageCUDRequest; }): UpdatePersistedContentRoutine {
            this.pageCUDRequestProducer = producer;
            return this;
        }

        public execute(): Q.Promise<api.content.Content> {

            var context = new UpdatePersistedContentRoutineContext();
            return this.doExecute(context);
        }

        doExecuteNext(context: UpdatePersistedContentRoutineContext): Q.Promise<api.content.Content> {

            var deferred = Q.defer<api.content.Content>();

            if (!this.doneHandledContent) {

                this.doHandleUpdateContent(context).
                    done(() => {

                        this.doneHandledContent = true;

                        this.doExecuteNext(context).
                            done((contentFromNext: api.content.Content) => {
                                deferred.resolve(contentFromNext);
                            });
                    });
            }
            else if (!this.doneHandledSite) {

                this.doHandleUpdateSite(context).
                    done(()=> {

                        this.doneHandledSite = true;

                        this.doExecuteNext(context).
                            done((contentFromNext: api.content.Content) => {
                                deferred.resolve(contentFromNext);
                            });
                    });
            }
            else if (!this.doneHandledPage) {

                this.doHandlePage(context).
                    done(() => {

                        this.doneHandledPage = true;

                        this.doExecuteNext(context).
                            done((contentFromNext: api.content.Content) => {
                                deferred.resolve(contentFromNext);
                            });
                    });
            }
            else {

                deferred.resolve(context.content);
            }

            return deferred.promise;
        }

        private doHandleUpdateContent(context: UpdatePersistedContentRoutineContext): Q.Promise<void> {

            var deferred = Q.defer<void>();

            this.updateContentRequestProducer.call(this.getThisOfProducer()).
                sendAndParse().
                done((content: api.content.Content) => {

                    context.content = content;
                    deferred.resolve(null);
                });

            return deferred.promise;
        }

        private doHandleUpdateSite(context: UpdatePersistedContentRoutineContext): Q.Promise<void> {

            var deferred = Q.defer<void>();

            var updateSiteRequest = this.updateSiteRequestProducer.call(this.getThisOfProducer(), context.content);
            if (updateSiteRequest != null) {
                updateSiteRequest.
                    sendAndParse().
                    done((content: api.content.Content) => {

                        context.content = content;
                        deferred.resolve(null);
                    });
            }
            else {
                deferred.resolve(null);
            }

            return deferred.promise;
        }

        private doHandlePage(context: UpdatePersistedContentRoutineContext): Q.Promise<void> {

            var deferred = Q.defer<void>();

            var createPageRequest = this.pageCUDRequestProducer.call(this.getThisOfProducer(), context.content);

            if (createPageRequest != null) {
                createPageRequest.sendAndParse().
                    done((content: api.content.Content) => {

                        context.content = content;
                        deferred.resolve(null);
                    });
            }
            else {
                deferred.resolve(null);
            }

            return deferred.promise;
        }

    }
}