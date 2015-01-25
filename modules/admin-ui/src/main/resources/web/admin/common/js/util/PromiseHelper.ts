module api.util {

    export class PromiseHelper {

        static newResolvedVoidPromise(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }
    }
}