module api_remote_util {

    export var RemoteSystemService:RemoteSystemServiceInterface;

    export interface RemoteSystemServiceInterface {
        util_getCountries (params:GetCountriesParams, success:(result:GetCountriesResult)=>void,
                           failure?:(result:api_remote.FailureResult)=>void):void;
        util_getLocales (params:GetLocalesParams, success:(result:GetLocalesResult)=>void,
                         failure?:(result:api_remote.FailureResult)=>void):void;
        util_getTimeZones (params:GetTimeZonesParams, success:(result:GetTimeZonesResult)=>void,
                           failure?:(result:api_remote.FailureResult)=>void):void;
        system_getSystemInfo (params:SystemGetSystemInfoParams, success:(result:SystemGetSystemInfoResult)=>void,
                              failure?:(result:api_remote.FailureResult)=>void):void;
    }

    class RemoteSystemServiceImpl extends api_remote.BaseRemoteService implements RemoteSystemServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "util_getCountries", "util_getLocales", "util_getTimeZones",
                "system_getSystemInfo"
            ];
            super('api_remote_util.RemoteSystemService', methods);
        }

        util_getCountries(params:GetCountriesParams, success:(result:GetCountriesResult)=>void,
                          failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        util_getLocales(params:GetLocalesParams, success:(result:GetLocalesResult)=>void,
                        failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        util_getTimeZones(params:GetTimeZonesParams, success:(result:GetTimeZonesResult)=>void,
                          failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        system_getSystemInfo(params:SystemGetSystemInfoParams, success:(result:SystemGetSystemInfoResult)=>void,
                             failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }
    }

    var remoteSystemServiceImpl = new RemoteSystemServiceImpl();
    RemoteSystemService = remoteSystemServiceImpl;
    remoteSystemServiceImpl.init();
}
