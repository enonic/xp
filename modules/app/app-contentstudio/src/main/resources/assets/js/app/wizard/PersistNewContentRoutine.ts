import '../../api.ts';
import {ContentWizardPanel} from './ContentWizardPanel';

import CreateContentRequest = api.content.resource.CreateContentRequest;
import Content = api.content.Content;

export class PersistedNewContentRoutineContext {

    content: api.content.Content = null;
}

export class PersistNewContentRoutine extends api.util.Flow<api.content.Content,PersistedNewContentRoutineContext> {

    private createContentRequestProducer: {() : wemQ.Promise<CreateContentRequest>; };

    private doneHandledContent: boolean = false;

    constructor(thisOfProducer: ContentWizardPanel) {
        super(thisOfProducer);
    }

    public setCreateContentRequestProducer(producer: {() : wemQ.Promise<CreateContentRequest>; }): PersistNewContentRoutine {
        this.createContentRequestProducer = producer;
        return this;
    }

    public execute(): wemQ.Promise<Content> {

        let context = new PersistedNewContentRoutineContext();
        return this.doExecute(context);
    }

    doExecuteNext(context: PersistedNewContentRoutineContext): wemQ.Promise<Content> {

        if (!this.doneHandledContent) {

            return this.doHandleCreateContent(context).then(() => {

                this.doneHandledContent = true;
                return this.doExecuteNext(context);

            });
        } else {
            return wemQ(context.content);
        }
    }

    private doHandleCreateContent(context: PersistedNewContentRoutineContext): wemQ.Promise<void> {

        if (this.createContentRequestProducer != null) {

            return this.createContentRequestProducer.call(this.getThisOfProducer()).then((createContentRequest: CreateContentRequest) => {

                return createContentRequest.sendAndParse().then((content: Content): void => {

                    context.content = content;

                });
            });
        } else {
            return api.util.PromiseHelper.newResolvedVoidPromise();
        }
    }
}
