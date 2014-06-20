module api {

    export class Class {

        private name: string;

        private fn: Function;

        constructor(name: string, fn: Function) {
            this.name = name;
            this.fn = fn;
        }

        getName(): string {
            return this.name;
        }

        newInstance(constructorParams?: any): any {

            var newInstance = Object.create(this.fn.prototype);
            newInstance.constructor.call(newInstance, constructorParams);
            return newInstance;
        }
    }
}