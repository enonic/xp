module API.util {
    var baseUri: string;
    function getAbsoluteUri(uri: string): string;
}
module API.event {
    class Event {
        private name;
        constructor(name: string);
        public getName(): string;
        public fire(): void;
    }
}
module API.event {
    function onEvent(name: string, handler: (event: Event) => void): void;
    function fireEvent(event: Event): void;
}
module API.notify {
    enum Type {
        INFO,
        ERROR,
        ACTION,
    }
    class Action {
        private name;
        private handler;
        constructor(name: string, handler: Function);
        public getName(): string;
        public getHandler(): Function;
    }
    class Message {
        private type;
        private text;
        private actions;
        constructor(type: Type, text: string);
        public getType(): Type;
        public getText(): string;
        public getActions(): Action[];
        public addAction(name: string, handler: () => void): void;
        public send(): void;
    }
    function newInfo(text: string): Message;
    function newError(text: string): Message;
    function newAction(text: string): Message;
}
module API.notify {
    class NotifyManager {
        private timers;
        private el;
        constructor();
        private render();
        private getWrapperEl();
        public notify(message: Message): void;
        private doNotify(opts);
        private setListeners(el, opts);
        private remove(el);
        private startTimer(el);
        private stopTimer(el);
        private renderNotification(opts);
    }
    function sendNotification(message: Message): void;
}
module API.notify {
    class NotifyOpts {
        public message: string;
        public backgroundColor: string;
        public listeners: Object[];
    }
    function buildOpts(message: Message): NotifyOpts;
}
module API.notify {
    function showFeedback(message: string): void;
    function updateAppTabCount(appId, tabCount: Number): void;
}
module API.content.data {
    class Data {
        private name;
        private arrayIndex;
        constructor(name: string);
        public setArrayIndex(value: number): void;
        public getName(): string;
        public getArrayIndex(): number;
    }
}
module API.content.data {
    class DataSet extends Data {
        private dataById;
        constructor(json);
        public addData(data: Data): void;
        public getData(dataId: string): Data;
    }
}
module API.content.data {
    class Property extends Data {
        private value;
        private type;
        static from(json): Property;
        constructor(name: string, value: string, type: string);
        public getValue(): string;
        public getType(): string;
    }
}
module API.content.schema.content.form {
    class FormItem {
        private name;
        constructor(name: string);
        public getName(): string;
    }
}
module API.content.schema.content.form {
    class Input extends FormItem {
        private label;
        private immutable;
        private occurrences;
        private indexed;
        private customText;
        private validationRegex;
        private helpText;
        constructor(json);
        public getLabel(): string;
        public isImmutable(): bool;
        public getOccurrences(): Occurrences;
        public isIndexed(): bool;
        public getCustomText(): string;
        public getValidationRegex(): string;
        public getHelpText(): string;
    }
}
module API.content.schema.content.form {
    class Occurrences {
        private minimum;
        private maximum;
        constructor(json);
    }
}
