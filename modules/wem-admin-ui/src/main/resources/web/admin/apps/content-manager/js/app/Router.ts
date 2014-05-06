module app {
    export class Router {
        static setHash(path:string) {
            if (window.parent["setHash"]) {
                window.parent["setHash"](api.app.Application.getAppId() + "/" + path);
            }
        }
    }
}