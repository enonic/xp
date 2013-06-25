module api_util {

    export class Animation {

        private static DEFAULT_INTERVAL:number = 10;

        private duration:number;
        private interval:number;

        private doStep:(progress:number)=>{};

        private id:number;
        private running:bool = false;

        constructor(duration:number, interval:number = Animation.DEFAULT_INTERVAL) {
            this.duration = duration;
            this.interval = interval;
        }

        onStep(doStep:(progress)=>{}):void {
            this.doStep = doStep;
        }

        start():void {
            var startTime = this.getCurrentTime();

            this.id = setInterval(() => {
                var progress = Math.min((this.getCurrentTime() - startTime) / this.duration, 1);

                if (this.doStep) {
                    this.doStep(progress);
                }

                if (progress == 1) {
                    this.stop();
                }
            }, this.interval);

            this.running = true;
        }

        stop():void {
            clearInterval(this.id);

            this.running = false;
        }

        isRunning():bool {
            return this.running;
        }

        private getCurrentTime():number {
            return new Date().getTime();
        }
    }
}