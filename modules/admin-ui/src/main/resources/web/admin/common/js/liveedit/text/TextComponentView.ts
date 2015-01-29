module api.liveedit.text {

    import ComponentView = api.liveedit.ComponentView;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.region.TextComponent;
    import StartTextEditModeEvent = api.liveedit.StartTextEditModeEvent;


    export class TextComponentViewBuilder extends ComponentViewBuilder<TextComponent> {
        constructor() {
            super();
            this.setType(TextItemType.get());
        }
    }

    export class TextComponentView extends ComponentView<TextComponent> implements api.ui.text.TextEditorEditableArea {

        private textComponent: TextComponent;

        private article: api.dom.Element;

        private editor: MediumEditorType;

        constructor(builder: TextComponentViewBuilder) {

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
            this.onDblClicked(this.handleDbClick.bind(this));

            // ItemView is not managing tooltip for text component
            this.listenToTooltipEvents();
        }

        isManagingTooltip(): boolean {
            return true;
        }

        private listenToTooltipEvents() {
            this.onMouseOverView(() => {
                if (!this.isEditMode()) {
                    this.showTooltip();
                }
            });
            this.onMouseLeaveView(() => {
                if (!this.isEditMode()) {
                    this.hideTooltip();
                }
            });
        }

        private initializeArticle() {
            // check if article came from server
            for (var i = 0; i < this.getChildren().length; i++) {
                var child = this.getChildren()[i];
                if (child.getEl().getTagName().toLowerCase() == 'article') {
                    this.article = child;
                }
            }
            if (!this.article) {
                // create it in case of new component
                this.article = new api.dom.ArticleEl();
                this.prependChild(this.article);
            }
        }

        getElement(): api.dom.Element {
            return this.article;
        }

        processChanges() {
            this.textComponent.setText(this.article.getHtml());
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

        handleDbClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            if (this.isEditMode()) {
                return;
            }

            this.setEditMode(true);
            this.selectText();
            new StartTextEditModeEvent(this).fire();
        }

        handleClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            if (this.isEditMode()) {
                return;
            }

            if (!this.isSelected()) {
                this.deselectParent();
                this.select(!this.isEmpty() ? {x: event.pageX, y: event.pageY} : null);
            } else {
                this.deselect();
                this.setEditMode(true);
                this.giveFocus();
                new StartTextEditModeEvent(this).fire();
            }
        }

        handleKey() {
            this.processChanges();
        }

        public isEditMode(): boolean {
            return this.hasClass('edit-mode');
        }

        public setEditMode(flag: boolean) {

            this.toggleClass('edit-mode', flag);
            this.article.getEl().setAttribute('contenteditable', flag.toString());
            this.setDraggable(!flag);

            if (flag) {
                this.hideTooltip();

                if (!this.editor) {
                    this.editor = this.createEditor();
                }

                this.editor.activate();

            } else {
                if (this.editor) {
                    this.editor.deactivate();
                }
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
                placeholder: '',
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
            var element = this.article.getHTMLElement();
            var selection = window.getSelection();
            var range = document.createRange();
            range.selectNodeContents(element);
            range.setStart(range.endContainer, range.startOffset);
            range.setEnd(range.endContainer, range.endOffset);
            selection.removeAllRanges();
            selection.addRange(range);
        }

        giveFocus() {
            return this.isEditMode() && this.article.giveFocus();
        }

        private createTextContextMenuActions(): api.ui.Action[] {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Edit').onExecuted(() => {
                this.setEditMode(true);
                this.giveFocus();
                new StartTextEditModeEvent(this).fire();
            }));
            return actions;
        }
    }
}