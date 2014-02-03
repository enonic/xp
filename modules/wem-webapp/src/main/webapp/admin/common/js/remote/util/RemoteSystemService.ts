module api.remote.util {

    export var RemoteSystemService: RemoteSystemServiceInterface;

    export interface RemoteSystemServiceInterface {
        system_getSystemInfo (params: SystemGetSystemInfoParams, success: (result: SystemGetSystemInfoResult)=>void,
                              failure?: (result: api.remote.FailureResult)=>void):void;
    }

    class RemoteSystemServiceImpl extends api.remote.BaseRemoteService implements RemoteSystemServiceInterface {

        constructor() {
            var methods: string[] = [
                "system.getSystemInfo"
            ];
            super('api.remote.util.RemoteSystemService', methods);
        }

        system_getSystemInfo(params: SystemGetSystemInfoParams, success: (result: SystemGetSystemInfoResult)=>void,
                             failure?: (result: api.remote.FailureResult)=>void): void {
            console.log(params, success, failure);
        }
    }

    var remoteSystemServiceImpl = new RemoteSystemServiceImpl();
    RemoteSystemService = remoteSystemServiceImpl;
    remoteSystemServiceImpl.init();
}
