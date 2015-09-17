module api.notify {

    export enum Type {
        INFO,
        ERROR,
        WARNING,
        ACTION,
        SUCCESS
    }

    export class Action {
        private name: string;
        private handler: {():void};

        constructor(name: string, handler: {():void}) {
            this.name = name;
            this.handler = handler;
        }

        getName(): string {
            return this.name;
        }

        getHandler(): {():void} {
            return this.handler;
        }
    }

    export class Message {
        private type: Type;
        private text: string;
        private actions: Action[];
        private autoHide: boolean;

        constructor(type: Type, text: string, autoHide: boolean = true) {
            this.type = type;
            this.text = text;
            this.actions = [];
            this.autoHide = autoHide;
        }

        getType(): Type {
            return this.type;
        }

        getText(): string {
            return this.text;
        }

        getActions(): Action[] {
            return this.actions;
        }

        getAutoHide(): boolean {
            return this.autoHide;
        }

        addAction(name: string, handler: () => void) {
            this.actions.push(new Action(name, handler));
        }

        static newSuccess(text: string, autoHide: boolean = true): Message {
            return new Message(Type.SUCCESS, text, autoHide);
        }

        static newInfo(text: string, autoHide: boolean = true): Message {
            return new Message(Type.INFO, text, autoHide);
        }

        static newError(text: string, autoHide: boolean = true): Message {
            return new Message(Type.ERROR, text, autoHide);
        }

        static newWarning(text: string, autoHide: boolean = true): Message {
            return new Message(Type.WARNING, text, autoHide);
        }

        static newAction(text: string, autoHide: boolean = true): Message {
            return new Message(Type.ACTION, text, autoHide);
        }
    }
}
