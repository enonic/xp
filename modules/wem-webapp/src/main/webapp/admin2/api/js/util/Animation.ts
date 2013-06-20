module api_util {

    export class Animation {

        static DELAY:number = 10;

        static start(doStep:Function, duration:number, delay?:number):number {
            var startTime = new Date().getTime();

            var id = setInterval(() => {
                var progress = Math.min(((new Date()).getTime() - startTime) / duration, 1);

                doStep(progress);

                if (progress == 1) {
                    clearInterval(id);
                }
            }, delay || api_util.Animation.DELAY);

            return id;
        }

        static stop(id:number):void {
            clearInterval(id);
        }
    }
}