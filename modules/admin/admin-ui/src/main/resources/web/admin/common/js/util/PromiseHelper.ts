module api.util {

    export class PromiseHelper {

        static newResolvedVoidPromise(): wemQ.Promise<void> {
            let deferred = wemQ.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }
    }
}