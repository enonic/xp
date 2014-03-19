module app.wizard {

    export class PersistedNewContentRoutineContext {

        content: api.content.Content = null;
    }

    export class PersistNewContentRoutine extends api.util.Flow<api.content.Content,PersistedNewContentRoutineContext> {

        private createContentRequestProducer: {() : api.content.CreateContentRequest; };

        private doneHandledContent = false;

        private createSiteRequestProducer: {(content: api.content.Content) : api.content.site.CreateSiteRequest; };

        private doneHandledSite = false;

        constructor(thisOfProducer: ContentWizardPanel) {
            super(thisOfProducer);
        }

        public setCreateContentRequestProducer(producer: {() : api.content.CreateContentRequest; }): PersistNewContentRoutine {
            this.createContentRequestProducer = producer;
            return this;
        }

        public setCreateSiteRequestProducer(producer: {(content: api.content.Content) : api.content.site.CreateSiteRequest; }): PersistNewContentRoutine {
            this.createSiteRequestProducer = producer;
            return this;
        }

        public execute(): Q.Promise<api.content.Content> {

            var context = new PersistedNewContentRoutineContext();
            return this.doExecute(context);
        }

        doExecuteNext(context: PersistedNewContentRoutineContext): Q.Promise<api.content.Content> {

            var deferred = Q.defer<api.content.Content>();

            if (!this.doneHandledContent) {

                this.doHandleCreateContent(context).
                    done(() => {

                        this.doneHandledContent = true;

                        this.doExecuteNext(context).
                            done((contentFromNext: api.content.Content) => {
                                deferred.resolve(contentFromNext);
                            });
                    });
            }
            else if (!this.doneHandledSite) {

                this.doHandleCreateSite(context).
                    done(()=> {

                        this.doneHandledSite = true;

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

        private doHandleCreateContent(context: PersistedNewContentRoutineContext): Q.Promise<void> {

            var deferred = Q.defer<void>();

            if (this.createContentRequestProducer != undefined) {

                this.createContentRequestProducer.call(this.getThisOfProducer()).
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

        private doHandleCreateSite(context: PersistedNewContentRoutineContext): Q.Promise<void> {

            var deferred = Q.defer<void>();

            var createSiteRequest = null;

            createSiteRequest = this.createSiteRequestProducer.call(this.getThisOfProducer(), context.content);
            if (createSiteRequest != null) {
                createSiteRequest.
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
    }
}