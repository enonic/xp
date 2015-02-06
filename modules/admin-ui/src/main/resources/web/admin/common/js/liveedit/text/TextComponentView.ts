module api.liveedit.text {

    import ComponentView = api.liveedit.ComponentView;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.region.TextComponent;

    export class TextComponentViewBuilder extends ComponentViewBuilder<TextComponent> {
        constructor() {
            super();
            this.setType(TextItemType.get());
        }
    }

    export class TextComponentView extends ComponentView<TextComponent> {

        private textComponent: TextComponent;

        private article: api.dom.Element;

        private editor: MediumEditorType;

        public static debug = false;

        // special handling for click to allow dblclick event without triggering 2 clicks before it
        public static DBL_CLICK_TIMEOUT = 200;
        private singleClickTimer: number;
        private lastClicked: number;

        constructor(builder: TextComponentViewBuilder) {

            this.lastClicked = 0;
            this.liveEditModel = builder.parentRegionView.liveEditModel;
            this.textComponent = builder.component;

            super(builder.
                setContextMenuActions(this.createTextContextMenuActions()).
                setPlaceholder(new TextPlaceholder(this)).
                setTooltipViewer(new TextComponentViewer()));

            this.addClass('text-view');

            this.initializeArticle();

            this.onKeyDown(this.handleKey.bind(this));
            this.onKeyUp(this.handleKey.bind(this));

        }

        showTooltip() {
            if (!this.isEditMode()) {
                super.showTooltip();
            }
        }

        hideTooltip(hideParentTooltip: boolean = true) {
            if (!this.isEditMode()) {
                super.hideTooltip(hideParentTooltip);
            }
        }

        highlight() {
            if (!this.isEditMode()) {
                super.highlight();
            }
        }

        unhighlight() {
            if (!this.isEditMode()) {
                super.unhighlight();
            }
        }

        private initializeArticle() {
            for (var i = 0; i < this.getChildren().length; i++) {
                var child = this.getChildren()[i];
                if (child.getEl().getTagName().toUpperCase() == 'ARTICLE') {
                    this.article = child;
                    break;
                }
            }
            if (!this.article) {
                // create it in case of new component
                this.article = new api.dom.ArticleEl();
                this.prependChild(this.article);
            }

            this.article.onClicked((event: MouseEvent) => {
                if (TextComponentView.debug) {
                    console.log('Article clicked', event);
                }
            });

            // text component has a placeholder text
            // that should be used instead of a regular one
            this.article.setHtml(this.textComponent.getText());
        }

        private processChanges() {
            var text = this.article.getHtml();
            if (TextComponentView.debug) {
                console.log('Processing editor contents:', text);
            }
            this.textComponent.setText(text);
        }

        isEmpty(): boolean {
            return !this.textComponent || this.textComponent.isEmpty();
        }

        duplicate(duplicate: TextComponent): TextComponentView {
            var duplicatedView = new TextComponentView(new TextComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setParentElement(this.getParentElement()).
                setComponent(duplicate));

            duplicatedView.insertAfterEl(this);
            return duplicatedView;
        }

        private startPageTextEditMode() {
            this.deselect();
            var pageView = this.getPageView();
            if (!pageView.isTextEditMode()) {
                pageView.setTextEditMode(true);
            }
            this.giveFocus();
        }

        private doHandleDbClick(event: MouseEvent) {
            if (this.isEditMode()) {
                return;
            }

            this.startPageTextEditMode();
            this.selectText();
        }

        private doHandleClick(event: MouseEvent) {
            if (this.isEditMode()) {
                return;
            }

            if (!this.isSelected()) {
                this.deselectParent();
                this.select(!this.isEmpty() ? {x: event.pageX, y: event.pageY} : null);
            } else {
                this.deselect();
            }
        }


        handleClick(event: MouseEvent) {
            if (TextComponentView.debug) {
                console.group('Handling click [' + this.getId() + ']');
                console.log(event);
            }

            event.stopPropagation();
            event.preventDefault();

            if (this.isEditMode()) {
                if (TextComponentView.debug) {
                    console.log('Is in text edit mode, not handling click');
                    console.groupEnd();
                }
                return;
            }

            var timeSinceLastClick = new Date().getTime() - this.lastClicked;

            if (timeSinceLastClick > TextComponentView.DBL_CLICK_TIMEOUT) {
                this.singleClickTimer = setTimeout(() => {
                    if (TextComponentView.debug) {
                        console.log('no dblclick occured during ' + TextComponentView.DBL_CLICK_TIMEOUT + 'ms, notifying click', this);
                        console.groupEnd();
                    }

                    this.doHandleClick(event);
                }, TextComponentView.DBL_CLICK_TIMEOUT);

            } else {

                if (TextComponentView.debug) {
                    console.log('dblclick occured after ' + timeSinceLastClick + 'ms, notifying dbl click', this);
                    // end the group started by the first click first
                    console.groupEnd();
                    console.groupEnd();
                }
                clearTimeout(this.singleClickTimer);
                this.doHandleDbClick(event);
            }
            this.lastClicked = new Date().getTime();
        }

        private handleKey() {
            this.processChanges();
        }

        isEditMode(): boolean {
            return this.hasClass('edit-mode');
        }

        setEditMode(flag: boolean) {
            if (!flag && this.editor) {
                this.deselectText();
                this.editor.deactivate();
            }

            this.toggleClass('edit-mode', flag);
            this.article.getEl().setAttribute('contenteditable', flag.toString());
            this.setDraggable(!flag);

            if (flag) {
                this.hideTooltip();

                if (!this.editor) {
                    this.editor = this.createEditor();
                }
                this.editor.activate();
            }
        }

        private createEditor(): MediumEditorType {
            var headersExtension = new MediumHeadersDropdownExtension();
            var editor = new MediumEditor([this.article.getHTMLElement()], {
                buttons: ['bold', 'italic', 'underline', 'strikethrough',
                    'justifyLeft', 'justifyCenter', 'justifyRight', 'justifyFull',
                    'anchor',
                    'headers',
                    'orderedlist', 'unorderedlist',
                    'quote'
                ],
                buttonLabels: {
                    'bold': '<i class="icon-bold"></i>',
                    'italic': '<i class="icon-italic"></i>',
                    'underline': '<i class="icon-underline"></i>',
                    'strikethrough': '<i class="icon-strikethrough"></i>',
                    'justifyCenter': '<i class="icon-justify-center"></i>',
                    'justifyFull': '<i class="icon-justify-full"></i>',
                    'justifyLeft': '<i class="icon-justify-left"></i>',
                    'justifyRight': '<i class="icon-justify-right"></i>',
                    'anchor': '<i class="icon-anchor"></i>',
                    'header1': '<i class="icon-header1"></i>',
                    'header2': '<i class="icon-header2"></i>',
                    'orderedlist': '<i class="icon-ordered-list"></i>',
                    'unorderedlist': '<i class="icon-unordered-list"></i>',
                    'quote': '<i class="icon-quote"></i>'
                },
                cleanPastedHTML: true,
                targetBlank: true,
                disablePlaceholders: true,
                firstHeader: 'h1',
                secondHeader: 'h2',
                extensions: {
                    'headers': headersExtension
                }
            });
            var checkActiveButtons = editor.checkActiveButtons.bind(editor);
            editor.checkActiveButtons = () => {
                headersExtension.beforeCheckState();
                checkActiveButtons();
                headersExtension.afterCheckState();
            };
            headersExtension.setEditor(editor);

            editor.onHideToolbar = () => {
                this.processChanges();
                headersExtension.onHideToolbar();
            };
            editor.onShowToolbar = () => {
                headersExtension.onShowToolbar();
            };
            return editor;
        }

        private selectText() {
            var doc = document;
            var text = this.article.getHTMLElement();

            if (window['getSelection']) { // moz, opera, webkit
                var selection = window['getSelection']();
                var rangeOther = doc.createRange();
                rangeOther.selectNodeContents(text);
                selection.removeAllRanges();
                selection.addRange(rangeOther);
            } else if (doc.body['createTextRange']) { // ms
                var rangeIE = doc.body['createTextRange']();
                rangeIE.moveToElementText(text);
                rangeIE.select();
            }
            text.click();
        }

        private deselectText() {
            if (window['getSelection']) {  // moz, opera, webkit
                window['getSelection']().removeAllRanges();
            } else if (document.selection) {    // ms
                document.selection.empty();
            }
        }

        giveFocus() {
            if (this.isEditMode()) {
                this.article.getHTMLElement().focus();
                return true;
            }
            return false;
        }

        private createTextContextMenuActions(): api.ui.Action[] {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Edit').onExecuted(() => {
                this.startPageTextEditMode();
            }));
            return actions;
        }
    }
}