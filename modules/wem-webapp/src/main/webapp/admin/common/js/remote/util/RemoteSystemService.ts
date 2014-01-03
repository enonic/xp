module api.remote.util {

    export var RemoteSystemService:RemoteSystemServiceInterface;

    export interface RemoteSystemServiceInterface {
        util_getCountries (params:GetCountriesParams, success:(result:GetCountriesResult)=>void,
                           failure?:(result:api.remote.FailureResult)=>void):void;
        util_getLocales (params:GetLocalesParams, success:(result:GetLocalesResult)=>void,
                         failure?:(result:api.remote.FailureResult)=>void):void;
        util_getTimeZones (params:GetTimeZonesParams, success:(result:GetTimeZonesResult)=>void,
                           failure?:(result:api.remote.FailureResult)=>void):void;
        system_getSystemInfo (params:SystemGetSystemInfoParams, success:(result:SystemGetSystemInfoResult)=>void,
                              failure?:(result:api.remote.FailureResult)=>void):void;
    }

    class RemoteSystemServiceImpl extends api.remote.BaseRemoteService implements RemoteSystemServiceInterface {

        constructor() {
            var methods:string[] = [
                "util.getCountries", "util.getLocales", "util.getTimeZones",
                "system.getSystemInfo"
            ];
            super('api.remote.util.RemoteSystemService', methods);
        }

        util_getCountries(params:GetCountriesParams, success:(result:GetCountriesResult)=>void,
                          failure?:(result:api.remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        util_getLocales(params:GetLocalesParams, success:(result:GetLocalesResult)=>void,
                        failure?:(result:api.remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        util_getTimeZones(params:GetTimeZonesParams, success:(result:GetTimeZonesResult)=>void,
                          failure?:(result:api.remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        system_getSystemInfo(params:SystemGetSystemInfoParams, success:(result:SystemGetSystemInfoResult)=>void,
                             failure?:(result:api.remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }
    }

    var remoteSystemServiceImpl = new RemoteSystemServiceImpl();
    RemoteSystemService = remoteSystemServiceImpl;
    remoteSystemServiceImpl.init();
}
