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

            return this.updateContentRequestProducer.call(this.getThisOfProducer()).
                sendAndParse().
                then((content: api.content.Content):void => {

                    context.content = content;

                });
        }

        private doHandleUpdateSite(context: UpdatePersistedContentRoutineContext): Q.Promise<void> {

            var updateSiteRequest = this.updateSiteRequestProducer.call(this.getThisOfProducer(), context.content);
            if (updateSiteRequest != null) {
                return updateSiteRequest.
                    sendAndParse().
                    then((content: api.content.Content):void => {

                        context.content = content;

                    });
            }
            else {
                var deferred = Q.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
        }

        private doHandlePage(context: UpdatePersistedContentRoutineContext): Q.Promise<void> {

            var createPageRequest = this.pageCUDRequestProducer.call(this.getThisOfProducer(), context.content);

            if (createPageRequest != null) {
                return createPageRequest
                    .sendAndParse().
                    then((content: api.content.Content):void => {

                        context.content = content;

                    });
            }
            else {
                var deferred = Q.defer<void>();
                deferred.resolve(null);
                return deferred.promise;
            }
        }

    }
}