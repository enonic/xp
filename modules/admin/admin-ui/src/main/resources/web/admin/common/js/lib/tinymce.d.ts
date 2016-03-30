// incomplete definitions for http://www.tinymce.com

interface HtmlAreaObservable {
    off: (name?: string, callback?: Function) => Object
    on: (name: string, callback: Function) => Object
    fire: (name: string, args?: Object, bubble?: Boolean) => Event
}

interface HtmlAreaEditor extends HtmlAreaObservable {
    destroy: (automatic: boolean) => void
    remove: () => void
    hide: () => void
    show: () => void
    getContent: (args?: Object) => string
    setContent: (content: string, args?: Object) => string
    focus: (skip_focus?: Boolean) => void
    undoManager: HtmlAreaUndoManager
    settings: Object
    insertContent: (content: string, args?: Object) => string
    nodeChanged: (args?: Object) => void
    execCommand: (c: string, u: Boolean, v: Object, args?: Object) => void;
}

interface HtmlAreaUndoManager {
    undo: () => Object
    clear: () => void
    hasUndo: () => Boolean
}

interface HtmlAreaEvent {

}

interface HtmlAreaStatic extends HtmlAreaObservable {
    init: (settings: Object) => void;
    execCommand: (c: string, u: Boolean, v: string) => Boolean;
    activeEditor: HtmlAreaEditor;
    get: (id: String) => HtmlAreaEditor;
}

declare var tinymce: HtmlAreaStatic;