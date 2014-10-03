module app.wizard {

    export class PersistedNewContentRoutineContext {

        content: api.content.Content = null;
    }

    export class PersistNewContentRoutine extends api.util.Flow<api.content.Content,PersistedNewContentRoutineContext> {

        private createContentRequestProducer: {() : api.content.CreateContentRequest; };

        private doneHandledContent = false;

        constructor(thisOfProducer: ContentWizardPanel) {
            super(thisOfProducer);
        }

        public setCreateContentRequestProducer(producer: {() : api.content.CreateContentRequest; }): PersistNewContentRoutine {
            this.createContentRequestProducer = producer;
            return this;
        }

        public execute(): wemQ.Promise<api.content.Content> {

            var context = new PersistedNewContentRoutineContext();
            return this.doExecute(context);
        }

        doExecuteNext(context: PersistedNewContentRoutineContext): wemQ.Promise<api.content.Content> {

            if (!this.doneHandledContent) {

                return this.doHandleCreateContent(context).
                    then(() => {

                        this.doneHandledContent = true;
                        return this.doExecuteNext(context);

                    });
            }
            else {
                return wemQ(context.content);
            }
        }

        private doHandleCreateContent(context: PersistedNewContentRoutineContext): wemQ.Promise<void> {

            if (this.createContentRequestProducer != undefined) {

                return this.createContentRequestProducer.call(this.getThisOfProducer()).
                    sendAndParse().
                    then((content: api.content.Content): void => {

                        context.content = content;

                    });
            }
            else {
                var deferred = wemQ.defer<void>();
                deferred.resolve(null)
                return deferred.promise;
            }
        }
    }
}