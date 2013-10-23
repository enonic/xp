module app {
    export class Router {
        static setHash(path:string) {
            window.parent["setHash"](getAppName() + "/" + path);
        }

    }
}