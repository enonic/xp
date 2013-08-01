///<reference path='BaseRemoteService.ts' />
///<reference path='Item.ts' />
///<reference path='RemoteUtilsModel.ts' />

module api_remote {

    export var RemoteSystemService:RemoteSystemServiceInterface;

    export interface RemoteSystemServiceInterface {
        util_getCountries (params:GetCountriesParams, callback:(result:GetCountriesResult)=>void):void;
        util_getLocales (params:GetLocalesParams, callback:(result:GetLocalesResult)=>void):void;
        util_getTimeZones (params:GetTimeZonesParams, callback:(result:GetTimeZonesResult)=>void):void;
        system_getSystemInfo (params:SystemGetSystemInfoParams, callback:(result:SystemGetSystemInfoResult)=>void):void;
    }

    class RemoteSystemServiceImpl extends BaseRemoteService implements RemoteSystemServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "util_getCountries", "util_getLocales", "util_getTimeZones",
                "system_getSystemInfo"
            ];
            super('api_remote.RemoteSystemService', methods);
        }

        util_getCountries(params:GetCountriesParams, callback:(result:GetCountriesResult)=>void):void {
            console.log(params, callback);
        }

        util_getLocales(params:GetLocalesParams, callback:(result:GetLocalesResult)=>void):void {
            console.log(params, callback);
        }

        util_getTimeZones(params:GetTimeZonesParams, callback:(result:GetTimeZonesResult)=>void):void {
            console.log(params, callback);
        }

        system_getSystemInfo(params:SystemGetSystemInfoParams, callback:(result:SystemGetSystemInfoResult)=>void):void {
            console.log(params, callback);
        }
    }

    var remoteSystemServiceImpl = new RemoteSystemServiceImpl();
    RemoteSystemService = remoteSystemServiceImpl;
    remoteSystemServiceImpl.init();
}
