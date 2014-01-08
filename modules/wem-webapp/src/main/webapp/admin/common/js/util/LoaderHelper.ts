module api.util {

    export class LoaderHelper {

        private requestFunc:(searchString:string)=>void;
        private delay:number;
        private timerId:number;
        private context:any;

        constructor(requestFunc:(searchString:string)=>void, context:any, delay:number = 500)
        {
            this.requestFunc = requestFunc;
            this.delay = delay;
            this.context = context;
        }


        search(searchString:string) {

            if (this.timerId) {
                window.clearTimeout(this.timerId);
            }
            this.timerId = window.setTimeout(() => {
                this.requestFunc.call(this.context, searchString);
                this.timerId = null;
            }, this.delay);
        }
    }
}