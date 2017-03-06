// incomplete definitions for http://www.tinymce.com

interface HtmlAreaObservable {
    off: (name?: string, callback?: () => void) => any;
    on: (name: string, callback: () => void) => any;
    fire: (name: string, args?: any, bubble?: boolean) => Event;
}

interface HtmlAreaEditor extends HtmlAreaObservable {
    destroy: (automatic: boolean) => void;
    remove: () => void;
    hide: () => void;
    show: () => void;
    getContent: (args?: any) => string;
    setContent: (content: string, args?: any) => string;
    focus: (skip_focus?: boolean) => void;
    undoManager: HtmlAreaUndoManager;
    settings: any;
    insertContent: (content: string, args?: Object) => string;
    nodeChanged: (args?: Object) => void;
    execCommand: (c: string, u: Boolean, v: Object, args?: Object) => void;
    getBody: () => Element;
    selection: any;
    getElement: () => Element;
    getDoc: () => Document;
    editorUpload: any;
    dom: any;
    schema: any;
    getParam: (name: string, defaultVal: any, type?: string) => any
}

interface HtmlAreaUndoManager {
    undo: () => any;
    clear: () => void;
    hasUndo: () => boolean;
    add: () => void;
    transact: (o: any) => void;
}

interface HtmlAreaEvent {

}

interface HtmlAreaStatic extends HtmlAreaObservable {
    init: (settings: any) => void;
    execCommand: (c: string, u: boolean, v: string) => boolean;
    activeEditor: HtmlAreaEditor;
    get: (id: string) => HtmlAreaEditor;
    triggerSave: () => void;
    toArray: (o: any) => [any];
    trim: (o : string) => string;
    grep: (a: any, b: any) => void;
    DOM: any;
}

declare var tinymce: HtmlAreaStatic;
