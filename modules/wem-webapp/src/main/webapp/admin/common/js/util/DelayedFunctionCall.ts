module api.util {

    export class DelayedFunctionCall {

        private functionToCall: () => void;

        private delay: number;

        private timerId: number;

        private context: any;

        constructor(functionToCall: () => void, context: any, delay: number = 500) {
            this.functionToCall = functionToCall;
            this.delay = delay;
            this.context = context;
        }

        delayCall() {
            if (this.timerId) {
                window.clearTimeout(this.timerId);
            }
            this.timerId = window.setTimeout(() => {

                this.functionToCall.call(this.context);
                this.timerId = null;
            }, this.delay);
        }
    }
}