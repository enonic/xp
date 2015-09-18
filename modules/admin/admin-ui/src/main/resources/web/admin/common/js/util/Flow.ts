module api.util {

    export class Flow<RESULT,CONTEXT> {

        private thisOfProducer: any;

        constructor(thisOfProducer: any) {
            this.thisOfProducer = thisOfProducer;
        }

        getThisOfProducer(): any {
            return this.thisOfProducer;
        }

        public doExecute(context:CONTEXT): wemQ.Promise<RESULT> {
            return this.doExecuteNext(context);
        }

        doExecuteNext(context:CONTEXT): wemQ.Promise<RESULT> {
            throw new Error("Must be implemented by inheritor");
        }
    }
}