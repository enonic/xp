module api_notify {

    export enum Type {
        INFO,
        ERROR,
        ACTION
    }

    export class Action {
        private name:string;
        private handler:Function;

        constructor(name:string, handler:Function) {
            this.name = name;
            this.handler = handler;
        }

        getName():string {
            return this.name;
        }

        getHandler():Function {
            return this.handler;
        }
    }

    export class Message {
        private type:Type;
        private text:string;
        private actions:Action[];

        constructor(type:Type, text:string) {
            this.type = type;
            this.text = text;
            this.actions = [];
        }

        getType():Type {
            return this.type;
        }

        getText():string {
            return this.text;
        }

        getActions():Action[] {
            return this.actions;
        }

        addAction(name:string, handler:() => void) {
            this.actions.push(new Action(name, handler));
        }

        send() {
            sendNotification(this);
        }
    }

    export function newInfo(text:string):Message {
        return new Message(Type.INFO, text);
    }

    export function newError(text:string):Message {
        return new Message(Type.ERROR, text);
    }

    export function newAction(text:string):Message {
        return new Message(Type.ACTION, text);
    }
}
