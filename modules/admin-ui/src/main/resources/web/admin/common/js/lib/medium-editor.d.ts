// Type definitions for medium-editor.js
// Project: https://github.com/daviferreira/medium-editor

interface MediumEditorOptions {
    allowMultiParagraphSelection?: boolean; // true
    anchorInputPlaceholder?: string; //'Paste or type a link'
    anchorPreviewHideDelay?: number; // 500
    buttons?: string[]; // ['bold', 'italic', 'underline', 'anchor', 'header1', 'header2', 'quote']
    buttonLabels?: any; // false | 'fontawesome' | object
    checkLinkFormat?: boolean; // false
    cleanPastedHTML?: boolean; // false
    delay?: number; // 0
    diffLeft?: number; // 0
    diffTop?: number; // -10
    disableReturn?: boolean; // false
    disableDoubleReturn?: boolean; // false
    disableToolbar?: boolean; // false
    disableEditing?: boolean; // false
    disableAnchorForm?: boolean; // false
    disablePlaceholders?: boolean; // false
    elementsContainer?: boolean; // false
    contentWindow?: Window;
    ownerDocument?: Document;
    firstHeader?: string; // 'h3'
    forcePlainText?: boolean; // true
    placeholder?: string; //'Type your text'
    secondHeader?: string; //'h4'
    targetBlank?: boolean; // false
    anchorTarget?: boolean; // false
    anchorButton?: boolean; // false
    anchorButtonClass?: string; //'btn'
    extensions?: any; // {}
    activeButtonClass?: string; //'medium-editor-button-active'
    firstButtonClass?: string; //'medium-editor-button-first'
    lastButtonClass?: string; //'medium-editor-button-last'
}

interface MediumEditorType {

    new(elements: any[], options?: MediumEditorOptions): MediumEditorType;

    deactivate(); // disables the editor
    activate(); // re-activates the editor
    serialize(); // returns a JSON object with elements contents

    onHideToolbar?: () => void;
    onShowToolbar?: () => void;
}

declare var MediumEditor: MediumEditorType;
