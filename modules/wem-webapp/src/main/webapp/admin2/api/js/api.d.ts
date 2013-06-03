module API.util {
    var baseUri: string;
    function getAbsoluteUri(uri: string): string;
}
module API_event {
    class Event {
        private name;
        constructor(name: string);
        public getName(): string;
        public fire(): void;
    }
}
module API_event {
    function onEvent(name: string, handler: (event: Event) => void): void;
    function fireEvent(event: Event): void;
}
module API_action {
    class Action {
        private label;
        private enabled;
        private executionListeners;
        private propertyChangeListeners;
        constructor(label: string);
        public getLabel(): string;
        public setLabel(value: string): void;
        public isEnabled(): bool;
        public setEnabled(value: bool): void;
        public execute(): void;
        public addExecutionListener(listener: (action: Action) => void): void;
        public addPropertyChangeListener(listener: (action: Action) => void): void;
    }
}
module API_ui {
    class Component {
        private static counstructorCounter;
        private id;
        constructor(parentName: string);
        public getId(): string;
    }
}
module API_ui_toolbar {
    class Button extends API_ui.Component {
        private action;
        private element;
        constructor(action: API_action.Action);
        public enable(value: bool): void;
        public getHTMLElement(): HTMLElement;
        private createHTMLElement();
    }
}
module API_ui_toolbar {
    class Toolbar extends API_ui.Component {
        public ext;
        private buttons;
        private element;
        constructor(actions: API_action.Action[]);
        private initExt();
        public getHTMLElement(): HTMLElement;
        private createHTMLElement();
        public add(action: API_action.Action): void;
        private addAction(action);
    }
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
module API_content_data {
    class DataId {
        private name;
        private arrayIndex;
        private refString;
        constructor(name: string, arrayIndex: number);
        public getName(): string;
        public getArrayIndex(): number;
        public toString(): string;
        static from(str: string): DataId;
    }
}
module API_content_data {
    class Data {
        private name;
        private arrayIndex;
        private parent;
        constructor(name: string);
        public setArrayIndex(value: number): void;
        public setParent(parent: DataSet): void;
        public getId(): DataId;
        public getName(): string;
        public getParent(): Data;
        public getArrayIndex(): number;
    }
}
module API_content_data {
    class DataSet extends Data {
        private dataById;
        constructor(name: string);
        public nameCount(name: string): number;
        public addData(data: Data): void;
        public getData(dataId: string): Data;
    }
}
module API_content_data {
    class ContentData extends DataSet {
        constructor();
    }
}
module API_content_data {
    class Property extends Data {
        private value;
        private type;
        static from(json): Property;
        constructor(name: string, value: string, type: string);
        public getValue(): string;
        public getType(): string;
        public setValue(value: any): void;
    }
}
module API_schema_content_form {
    class FormItem {
        private name;
        constructor(name: string);
        public getName(): string;
    }
}
module API_schema_content_form {
    class InputType {
        private name;
        constructor(json: any);
        public getName(): string;
    }
}
module API_schema_content_form {
    class Input extends FormItem {
        private inputType;
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
module API_schema_content_form {
    class Occurrences {
        private minimum;
        private maximum;
        constructor(json);
    }
}
