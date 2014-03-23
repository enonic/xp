module api.util {

    export class Flow<RESULT,CONTEXT> {

        private thisOfProducer: any;

        constructor(thisOfProducer: any) {
            this.thisOfProducer = thisOfProducer;
        }

        getThisOfProducer(): any {
            return this.thisOfProducer;
        }

        public doExecute(context:CONTEXT): Q.Promise<RESULT> {

            var deferred = Q.defer<RESULT>();

            this.doExecuteNext(context).
                done((result: RESULT) => {

                    deferred.resolve(result);
                });

            return deferred.promise;
        }

        doExecuteNext(context:CONTEXT): Q.Promise<RESULT> {
            throw new Error("Must be implemented by inheritor");
        }
    }
}