module app.wizard {

    export class UpdatePersistedContentRoutineContext {

        content: api.content.Content = null;
    }

    export class UpdatePersistedContentRoutine extends api.util.Flow<api.content.Content,UpdatePersistedContentRoutineContext> {

        private updateContentRequestProducer: {(content: api.content.Content) : api.content.UpdateContentRequest; };

        private doneHandledContent = false;

        private updateSiteRequestProducer: {(content: api.content.Content) : api.content.site.UpdateSiteRequest; };

        private doneHandledSite = false;

        private createPageRequestProducer: {(content: api.content.Content) : api.content.page.CreatePageRequest; };

        private updatePageRequestProducer: {(content: api.content.Content) : api.content.page.UpdatePageRequest; };

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

        public setCreatePageRequestProducer(producer: {(content: api.content.Content) : api.content.page.CreatePageRequest; }): UpdatePersistedContentRoutine {
            this.createPageRequestProducer = producer;
            return this;
        }

        public setUpdatePageRequestProducer(producer: {(content: api.content.Content) : api.content.page.UpdatePageRequest; }): UpdatePersistedContentRoutine {
            this.updatePageRequestProducer = producer;
            return this;
        }

        public execute(): Q.Promise<api.content.Content> {

            var context = new UpdatePersistedContentRoutineContext();
            return this.doExecute(context);
        }

        doExecuteNext(context: UpdatePersistedContentRoutineContext): Q.Promise<api.content.Content> {

            console.log("UpdatePersistedContentRoutine.doExecuteNext() ...");

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

            console.log("UpdatePersistedContentRoutine.doHandleUpdateContent() ... ");

            var deferred = Q.defer<void>();

            if (this.updateContentRequestProducer != undefined) {

                this.updateContentRequestProducer.call(this.getThisOfProducer()).
                    sendAndParse().
                    done((content: api.content.Content) => {

                        console.log("UpdatePersistedContentRoutine.doHandleUpdateContent() ... content updated");

                        context.content = content;

                        deferred.resolve(null);
                    });
            }
            else {

                console.log("UpdatePersistedContentRoutine.doHandleUpdateContent() ... no updateContentRequestProducer defined");

                deferred.resolve(null);

            }

            return deferred.promise;
        }

        private doHandleUpdateSite(context: UpdatePersistedContentRoutineContext): Q.Promise<void> {

            console.log("UpdatePersistedContentRoutine.doHandleUpdateSite() ...");

            var deferred = Q.defer<void>();

            if (this.updateSiteRequestProducer != undefined) {

                var updateSiteRequest = this.updateSiteRequestProducer.call(this.getThisOfProducer(), context.content);
                if (updateSiteRequest != null) {
                    updateSiteRequest.
                        sendAndParse().
                        done((content: api.content.Content) => {

                            console.log("UpdatePersistedContentRoutine.doHandleUpdateSite() ... site updated");

                            context.content = content;

                            deferred.resolve(null);
                        });
                }
                else {
                    console.log("UpdatePersistedContentRoutine.doHandleUpdateSite() ... no updateSiteRequest given");

                    deferred.resolve(null);
                }
            }
            else {
                console.log("UpdatePersistedContentRoutine.doHandleUpdateSite() ... no createSiteRequestProducer defined");

                deferred.resolve(null);
            }

            return deferred.promise;
        }

        private doHandlePage(context: UpdatePersistedContentRoutineContext): Q.Promise<void> {

            console.log("UpdatePersistedContentRoutine.doHandlePage() ...");

            var deferred = Q.defer<void>();


            if (this.createPageRequestProducer != undefined) {

                var createPageRequest = this.createPageRequestProducer.call(this.getThisOfProducer(), context.content);

                if (createPageRequest != null) {
                    createPageRequest.sendAndParse().
                        done((content: api.content.Content) => {

                            console.log("UpdatePersistedContentRoutine.doHandlePage() ... page created");

                            context.content = content;

                            deferred.resolve(null);
                        });
                }
                else {
                    console.log("UpdatePersistedContentRoutine.doHandlePage() ... no createPageRequest given");
                    deferred.resolve(null);
                }
            }
            else if (this.updatePageRequestProducer != undefined) {
                var updatePageRequest = this.updatePageRequestProducer.call(this.getThisOfProducer(), context.content);

                if (updatePageRequest != null) {
                    updatePageRequest.sendAndParse().
                        done((content: api.content.Content) => {

                            console.log("UpdatePersistedContentRoutine.doHandlePage() ... page created");

                            context.content = content;

                            deferred.resolve(null);
                        });
                }
                else {
                    console.log("UpdatePersistedContentRoutine.doHandlePage() ... no updatePageRequest given");
                    deferred.resolve(null);
                }
            }
            else {
                console.log("UpdatePersistedContentRoutine.doExecuteNext() no page to creat or update (no producers)");
                deferred.resolve(null);
            }

            return deferred.promise;
        }

    }
}