module api.util.htmlarea.dialog {

    import FormItem = api.ui.form.FormItem;
    import InputAlignment = api.ui.InputAlignment;
    import Action = api.ui.Action;
    import TextInput = api.ui.text.TextInput;
    import Checkbox = api.ui.Checkbox;
    import ElementHelper = api.dom.ElementHelper;
    import Element = api.dom.Element;

    export class SearchReplaceModalDialog extends ModalDialog {

        private findInput: TextInput;
        private replaceInput: TextInput;
        private matchCaseCheckbox: Checkbox;
        private wholeWordsCheckbox: Checkbox;

        private nextAction: Action;
        private prevAction: Action;
        private replaceAction: Action;
        private replaceAllAction: Action;

        private findAction: Action;

        private searchAndReplaceHelper: SearchAndReplaceHelper;

        constructor(editor: HtmlAreaEditor) {
            super(editor, 'Find and replace', 'search-and-replace-modal-dialog');
            this.searchAndReplaceHelper = new SearchAndReplaceHelper(editor);

            this.searchAndReplaceHelper.onActionButtonsEnabled((enabled: boolean) => {
                this.updateActions(enabled);
            });

            this.searchAndReplaceHelper.onNavigationButtonsUpdated(() => {
                this.updateButtonStates();
            });

            this.setupListeners();
        }

        protected getMainFormItems(): FormItem[] {
            const findField = this.createFormItem('find', 'Find');
            const replaceField = this.createFormItem('replace', 'Replace with');
            const matchCaseCheckbox = this.createCheckbox('matchcase', 'Match case');
            const wholeWordsCheckbox = this.createCheckbox('wholewords', 'Whole words');

            this.findInput = <TextInput>findField.getInput();
            this.replaceInput = <TextInput>replaceField.getInput();
            this.matchCaseCheckbox = <Checkbox>matchCaseCheckbox.getInput();
            this.wholeWordsCheckbox = <Checkbox>wholeWordsCheckbox.getInput();

            this.setFirstFocusField(findField.getInput());

            return [
                findField,
                replaceField,
                matchCaseCheckbox,
                wholeWordsCheckbox
            ];
        }

        private createCheckbox(id: string, label: string): FormItem {
            let checkbox: Checkbox = Checkbox.create().setLabelText(label).setInputAlignment(InputAlignment.RIGHT).build();

            checkbox.onValueChanged(() => {
                this.findAction.execute();
            });

            return this.createFormItem(id, null, null, null, checkbox);
        }

        protected initializeActions() {
            this.addAction(this.createReplaceAction());
            this.addAction(this.createReplaceAllAction());
            this.addAction(this.createPrevAction());
            this.addAction(this.createNextAction());

            this.findAction = new Action('Submit');
            this.findAction.onExecuted(() => {
                this.searchAndReplaceHelper.submit(this.findInput.getValue(), this.matchCaseCheckbox.isChecked(),
                    this.wholeWordsCheckbox.isChecked());
            });

            this.setSubmitAction(this.findAction);
        }

        private setupListeners() {
            let debouncedKeyDownHandler: () => void = api.util.AppHelper.debounce(() => {
                this.findAction.execute();
            }, 100);

            this.findInput.onValueChanged(debouncedKeyDownHandler);
        }

        private updateActions(enabled: boolean) {
            this.replaceAction.setEnabled(enabled);
            this.replaceAllAction.setEnabled(enabled);
            this.prevAction.setEnabled(enabled);
            this.nextAction.setEnabled(enabled);
        }

        private createReplaceAction(): Action {
            this.replaceAction = new Action('Replace');
            this.replaceAction.setEnabled(false);

            this.replaceAction.onExecuted(() => {
                if (!this.searchAndReplaceHelper.replace(this.replaceInput.getValue())) {

                    this.replaceAction.setEnabled(false);
                    this.replaceAllAction.setEnabled(false);
                    this.prevAction.setEnabled(false);
                    this.nextAction.setEnabled(false);

                    this.searchAndReplaceHelper.currentIndex = -1;
                    this.searchAndReplaceHelper.last = {};
                }
            });

            return this.replaceAction;
        }

        private createReplaceAllAction(): Action {
            this.replaceAllAction = new Action('Replace all');
            this.replaceAllAction.setEnabled(false);

            this.replaceAllAction.onExecuted(() => {
                this.searchAndReplaceHelper.replace(this.replaceInput.getValue(), true, true);

                this.replaceAction.setEnabled(false);
                this.replaceAllAction.setEnabled(false);
                this.prevAction.setEnabled(false);
                this.nextAction.setEnabled(false);

                this.searchAndReplaceHelper.last = {};
            });

            return this.replaceAllAction;
        }

        private createPrevAction(): Action {
            this.prevAction = new Action('Prev');
            this.prevAction.setEnabled(false);

            this.prevAction.onExecuted(() => {
                this.searchAndReplaceHelper.prev();
                this.updateButtonStates();
            });

            return this.prevAction;
        }

        private createNextAction(): Action {
            this.nextAction = new Action('Next');
            this.nextAction.setEnabled(false);

            this.nextAction.onExecuted(() => {
                this.searchAndReplaceHelper.next();
                this.updateButtonStates();
            });

            return this.nextAction;
        }

        open() {
            super.open();
            this.searchAndReplaceHelper.last = {};

            let selectedText: string = tinymce.trim(this.getEditor().selection.getContent({format: 'text'}));
            if(selectedText) {
                this.findInput.setValue(selectedText);
            }
        }

        close() {
            super.close();
            this.searchAndReplaceHelper.done();
        }

        private updateButtonStates() {
            this.nextAction.setEnabled(!!this.searchAndReplaceHelper.findSpansByIndex(this.searchAndReplaceHelper.currentIndex + 1).length);
            this.prevAction.setEnabled(!!this.searchAndReplaceHelper.findSpansByIndex(this.searchAndReplaceHelper.currentIndex - 1).length);
        }
    }

    class SearchAndReplaceHelper {

        private editor: HtmlAreaEditor;

        private m: any;
        private matches: any;
        private text: string;
        private count: number;
        private doc: Document;
        private blockElementsMap: any;
        private hiddenTextElementsMap: any;
        private shortEndedElementsMap: any;
        private makeReplacementNode: any;
        private stencilNode: any;

        private enableActionButtonsListeners: {(enabled: boolean): void}[] = [];
        private updateNavigationButtonsListeners: {(): void}[] = [];

        currentIndex: number = -1;
        last: any = {};

        constructor(editor: HtmlAreaEditor) {
            this.editor = editor;
        }

        submit(text: string, caseState: boolean, wholeWord: boolean) {
            let count;

            if (!text.length) {
                this.done(false);
                this.notifyActionButtonsEnabled(false);
                return;
            }

            if (this.last.text == text && this.last.caseState == caseState && this.last.wholeWord == wholeWord) {
                if (this.findSpansByIndex(this.currentIndex + 1).length === 0) {
                    this.notFoundAlert();
                    return;
                }

                this.next();
                this.notifyNavigationButtonsUpdated();
                return;
            }

            count = this.find(text, caseState, wholeWord);
            if (!count) {
                this.notFoundAlert();
            }

            this.notifyActionButtonsEnabled(count !== 0);
            this.notifyNavigationButtonsUpdated();

            this.last = {
                text: text,
                caseState: caseState,
                wholeWord: wholeWord
            };
        }

        private isMatchSpan(node: any) {
            const matchIndex = this.getElmIndex(node);

            return matchIndex !== null && matchIndex.length > 0;
        }

        replace(text: string, forward?: boolean, all?: boolean) {
            let i: number;
            let nodes: any;
            let node: Element;
            let matchIndex: any;
            let currentMatchIndex: any;
            let nextIndex: number = this.currentIndex;
            let hasMore: any;

            forward = forward !== false;

            node = this.editor.getBody();
            nodes = tinymce.grep(tinymce.toArray(node.getElementsByTagName('span')), this.isMatchSpan.bind(this));
            for (i = 0; i < nodes.length; i++) {
                let nodeIndex = this.getElmIndex(nodes[i]);

                matchIndex = currentMatchIndex = parseInt(nodeIndex, 10);
                if (all || matchIndex === this.currentIndex) {
                    if (text.length) {
                        nodes[i].firstChild.nodeValue = text;
                        this.unwrap(nodes[i]);
                    } else {
                        this.removeNode(nodes[i]);
                    }

                    while (nodes[++i]) {
                        matchIndex = parseInt(this.getElmIndex(nodes[i]), 10);

                        if (matchIndex === currentMatchIndex) {
                            this.removeNode(nodes[i]);
                        } else {
                            i--;
                            break;
                        }
                    }

                    if (forward) {
                        nextIndex--;
                    }
                } else if (currentMatchIndex > this.currentIndex) {
                    nodes[i].setAttribute('data-mce-index', currentMatchIndex - 1);
                }
            }

            this.editor.undoManager.add();
            this.currentIndex = nextIndex;

            if (forward) {
                hasMore = this.findSpansByIndex(nextIndex + 1).length > 0;
                this.next();
            } else {
                hasMore = this.findSpansByIndex(nextIndex - 1).length > 0;
                this.prev();
            }

            return !all && hasMore;
        };

        private isContentEditableFalse(node: any): boolean {
            return node && node.nodeType == 1 && node.contentEditable === 'false';
        }

        private find(text: string, matchCase: boolean, wholeWord: boolean) {
            text = text.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, '\\$&');
            text = wholeWord ? '\\b' + text + '\\b' : text;

            let count = this.markAllMatches(new RegExp(text, matchCase ? 'g' : 'gi'));

            if (count) {
                this.currentIndex = -1;
                this.currentIndex = this.moveSelection(true);
            }

            return count;
        };

        private markAllMatches(regex: RegExp) {
            let node: Element;
            let marker: Element;

            marker = this.editor.dom.create('span', {
                'data-mce-bogus': 1
            });

            marker.className = 'mce-match-marker'; // IE 7 adds class="mce-match-marker" and class=mce-match-marker
            node = this.editor.getBody();

            this.done(false);

            return this.findAndReplaceDOMText(regex, node, marker, false, this.editor.schema);
        }

        done(keepEditorSelection: boolean = false) {
            let i: number;
            let nodes: [any];
            let startContainer: any;
            let endContainer: any;

            nodes = tinymce.toArray(this.editor.getBody().getElementsByTagName('span'));
            for (i = 0; i < nodes.length; i++) {
                let nodeIndex = this.getElmIndex(nodes[i]);

                if (nodeIndex !== null && nodeIndex.length) {
                    if (nodeIndex === this.currentIndex.toString()) {
                        if (!startContainer) {
                            startContainer = nodes[i].firstChild;
                        }

                        endContainer = nodes[i].firstChild;
                    }

                    this.unwrap(nodes[i]);
                }
            }

            if (startContainer && endContainer) {
                let rng = this.editor.dom.createRng();
                rng.setStart(startContainer, 0);
                rng.setEnd(endContainer, endContainer.data.length);

                if (keepEditorSelection !== false) {
                    this.editor.selection.setRng(rng);
                }

                return rng;
            }
        };

        private removeNode(node: Node) {
            let dom = this.editor.dom;
            let parent = node.parentNode;

            dom.remove(node);

            if (dom.isEmpty(parent)) {
                dom.remove(parent);
            }
        }

        private notFoundAlert() {
            console.log('Could not find the specified string.');
        }

        next() {
            let index = this.moveSelection(true);

            if (index !== -1) {
                this.currentIndex = index;
            }
        };

        prev() {
            let index = this.moveSelection(false);

            if (index !== -1) {
                this.currentIndex = index;
            }
        };

        private moveSelection(forward: boolean) {
            let testIndex: number = this.currentIndex;
            let dom: any = this.editor.dom;

            forward = forward !== false;

            if (forward) {
                testIndex++;
            } else {
                testIndex--;
            }

            dom.removeClass(this.findSpansByIndex(this.currentIndex), 'mce-match-marker-selected');

            let spans = this.findSpansByIndex(testIndex);
            if (spans.length) {
                dom.addClass(this.findSpansByIndex(testIndex), 'mce-match-marker-selected');
                Element.fromHtmlElement(spans[0]).getEl().scrollIntoView();
                return testIndex;
            }

            return -1;
        }

        findSpansByIndex(index: number): [any] {
            let nodes: [any];
            let spans: [any] = <any>[];

            nodes = tinymce.toArray(this.editor.getBody().getElementsByTagName('span'));
            if (nodes.length) {
                for (let i = 0; i < nodes.length; i++) {
                    let nodeIndex = this.getElmIndex(nodes[i]);

                    if (nodeIndex === null || !nodeIndex.length) {
                        continue;
                    }

                    if (nodeIndex === index.toString()) {
                        spans.push(nodes[i]);
                    }
                }
            }

            return spans;
        }

        private getElmIndex(elm: Element) {
            let value = elm.getAttribute('data-mce-index');

            if (typeof value == 'number') {
                return '' + value;
            }

            return value;
        }

        private unwrap(node: Node) {
            let parentNode = node.parentNode;

            if (node.firstChild) {
                parentNode.insertBefore(node.firstChild, node);
            }

            node.parentNode.removeChild(node);
        }

        private findAndReplaceDOMText(regex: RegExp, node: Element, replacementNode: Element, captureGroup: any, schema: any) {
            this.reset(node, schema);

            this.text = this.getText(node);
            if (!this.text) {
                return;
            }

            if (regex.global) {
                this.m = regex.exec(this.text);
                while (!!this.m) {
                    this.matches.push(this.getMatchIndexes(this.m, captureGroup));
                    this.m = regex.exec(this.text);
                }
            } else {
                this.m = this.text.match(regex);
                this.matches.push(this.getMatchIndexes(this.m, captureGroup));
            }

            if (this.matches.length) {
                this.count = this.matches.length;
                this.stepThroughMatches(node, this.matches, this.genReplacer(replacementNode));
            }

            return this.count;
        }

        private reset(node: Element, schema: any) {
            this.m = null;
            this.matches = [];
            this.text = null;
            this.count = 0;
            this.doc = node.ownerDocument;
            this.blockElementsMap = schema.getBlockElements(); // H1-H6, P, TD etc
            this.hiddenTextElementsMap = schema.getWhiteSpaceElements(); // TEXTAREA, PRE, STYLE, SCRIPT
            this.shortEndedElementsMap = schema.getShortEndedElements(); // BR, IMG, INPUT
        };

        private getMatchIndexes(m: any, captureGroup: number) {
            captureGroup = captureGroup || 0;

            if (!m[0]) {
                throw 'findAndReplaceDOMText cannot handle zero-length matches';
            }

            let index = m.index;

            if (captureGroup > 0) {
                let cg = m[captureGroup];

                if (!cg) {
                    throw 'Invalid capture group';
                }

                index += m[0].indexOf(cg);
                m[0] = cg;
            }

            return [index, index + m[0].length, [m[0]]];
        }

        private getText(node: any) {
            let txt;

            if (node.nodeType === 3) {
                return node.data;
            }

            if (this.hiddenTextElementsMap[node.nodeName] && !this.blockElementsMap[node.nodeName]) {
                return '';
            }

            txt = '';

            if (this.isContentEditableFalse(node)) {
                return '\n';
            }

            if (this.blockElementsMap[node.nodeName] || this.shortEndedElementsMap[node.nodeName]) {
                txt += '\n';
            }

            node = node.firstChild;
            if (node) {
                do {
                    txt += this.getText(node);
                    node = node.nextSibling;
                } while (!!node);
            }

            return txt;
        }

        private stepThroughMatches(node: any, matches: any, replaceFn: any) {
            let startNode: Text;
            let endNode: Text;
            let startNodeIndex: number;
            let endNodeIndex: number;
            let innerNodes: [Node] = <any>[];
            let atIndex: number = 0;
            let curNode: any = node;
            let matchLocation: any = matches.shift();
            let matchIndex: number = 0;

            out: while (true) {
                if (this.blockElementsMap[curNode.nodeName] || this.shortEndedElementsMap[curNode.nodeName] ||
                    this.isContentEditableFalse(curNode)) {
                    atIndex++;
                }

                if (curNode.nodeType === 3) {
                    if (!endNode && curNode.length + atIndex >= matchLocation[1]) {
                        // We've found the ending
                        endNode = curNode;
                        endNodeIndex = matchLocation[1] - atIndex;
                    } else if (startNode) {
                        // Intersecting node
                        innerNodes.push(curNode);
                    }

                    if (!startNode && curNode.length + atIndex > matchLocation[0]) {
                        // We've found the match start
                        startNode = curNode;
                        startNodeIndex = matchLocation[0] - atIndex;
                    }

                    atIndex += curNode.length;
                }

                if (startNode && endNode) {
                    curNode = replaceFn({
                        startNode: startNode,
                        startNodeIndex: startNodeIndex,
                        endNode: endNode,
                        endNodeIndex: endNodeIndex,
                        innerNodes: innerNodes,
                        match: matchLocation[2],
                        matchIndex: matchIndex
                    });

                    // replaceFn has to return the node that replaced the endNode
                    // and then we step back so we can continue from the end of the
                    // match:
                    atIndex -= (endNode.length - endNodeIndex);
                    startNode = null;
                    endNode = null;
                    innerNodes = <any>[];
                    matchLocation = matches.shift();
                    matchIndex++;

                    if (!matchLocation) {
                        break; // no more matches
                    }
                } else if ((!this.hiddenTextElementsMap[curNode.nodeName] || this.blockElementsMap[curNode.nodeName]) &&
                           curNode.firstChild) {
                    if (!this.isContentEditableFalse(curNode)) {
                        // Move down
                        curNode = curNode.firstChild;
                        continue;
                    }
                } else if (curNode.nextSibling) {
                    // Move forward:
                    curNode = curNode.nextSibling;
                    continue;
                }

                // Move forward or up:
                while (true) {
                    if (curNode.nextSibling) {
                        curNode = curNode.nextSibling;
                        break;
                    } else if (curNode.parentNode !== node) {
                        curNode = curNode.parentNode;
                    } else {
                        break out;
                    }
                }
            }
        }

        /**
         * Generates the actual replaceFn which splits up text nodes
         * and inserts the replacement element.
         */
        private genReplacer(nodeName: any) {
            this.makeReplacementNode = null;

            if (typeof nodeName != 'function') {
                this.stencilNode = nodeName.nodeType ? nodeName : this.doc.createElement(nodeName);

                this.makeReplacementNode = this.makeReplacementNodeFn.bind(this);
            } else {
                this.makeReplacementNode = nodeName;
            }

            return this.replaceFn.bind(this);
        }

        private makeReplacementNodeFn(fill: string, matchIndex: number) {
            let clone = this.stencilNode.cloneNode(false);

            clone.setAttribute('data-mce-index', matchIndex);

            if (fill) {
                clone.appendChild(this.doc.createTextNode(fill));
            }

            return clone;
        }

        private replaceFn(range: any) {
            let before: Node;
            let after: Node;
            let parentNode: Node;
            let startNode: Text = range.startNode;
            let endNode: Text = range.endNode;
            let matchIndex: any = range.matchIndex;

            if (startNode === endNode) {
                let node = startNode;

                parentNode = node.parentNode;
                if (range.startNodeIndex > 0) {
                    // Add `before` text node (before the match)
                    before = this.doc.createTextNode(node.data.substring(0, range.startNodeIndex));
                    parentNode.insertBefore(before, node);
                }

                // Create the replacement node:
                let el = this.makeReplacementNode(range.match[0], matchIndex);
                parentNode.insertBefore(el, node);
                if (range.endNodeIndex < node.length) {
                    // Add `after` text node (after the match)
                    after = this.doc.createTextNode(node.data.substring(range.endNodeIndex));
                    parentNode.insertBefore(after, node);
                }

                node.parentNode.removeChild(node);

                return el;
            } else {
                // Replace startNode -> [innerNodes...] -> endNode (in that order)
                before = this.doc.createTextNode(startNode.data.substring(0, range.startNodeIndex));
                after = this.doc.createTextNode(endNode.data.substring(range.endNodeIndex));
                let elA = this.makeReplacementNode(startNode.data.substring(range.startNodeIndex), matchIndex);
                let innerEls = [];

                let i = 0;
                let l = range.innerNodes.length;
                for (; i < l; ++i) {
                    let innerNode = range.innerNodes[i];
                    let innerEl = this.makeReplacementNode(innerNode.data, matchIndex);
                    innerNode.parentNode.replaceChild(innerEl, innerNode);
                    innerEls.push(innerEl);
                }

                let elB = this.makeReplacementNode(endNode.data.substring(0, range.endNodeIndex), matchIndex);

                parentNode = startNode.parentNode;
                parentNode.insertBefore(before, startNode);
                parentNode.insertBefore(elA, startNode);
                parentNode.removeChild(startNode);

                parentNode = endNode.parentNode;
                parentNode.insertBefore(elB, endNode);
                parentNode.insertBefore(after, endNode);
                parentNode.removeChild(endNode);

                return elB;
            }
        }

        onActionButtonsEnabled(listener: (enabled: boolean) => void) {
            this.enableActionButtonsListeners.push(listener);
        }

        unActionButtonsEnabled(listener: (enabled: boolean) => void) {
            this.enableActionButtonsListeners = this.enableActionButtonsListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyActionButtonsEnabled(enabled: boolean) {
            this.enableActionButtonsListeners.forEach((listener) => {
                listener(enabled);
            });
        }

        onNavigationButtonsUpdated(listener: () => void) {
            this.updateNavigationButtonsListeners.push(listener);
        }

        unNavigationButtonsEnabled(listener: () => void) {
            this.updateNavigationButtonsListeners = this.updateNavigationButtonsListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyNavigationButtonsUpdated() {
            this.updateNavigationButtonsListeners.forEach((listener) => {
                listener();
            });
        }

    }
}
