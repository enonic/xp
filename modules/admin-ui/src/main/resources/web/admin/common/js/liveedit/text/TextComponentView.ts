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


            if (!this.isEmpty()) {
                // article comes from server
                this.article = this.getChildren()[0];
            } else {
                // create it in case of new component
                this.article = new api.dom.ArticleEl();
                this.appendChild(this.article);
            }

            this.onKeyDown(this.handleKey.bind(this));
            this.onKeyUp(this.handleKey.bind(this));
            this.onDblClicked(this.handleDbClick.bind(this));
        }

        getElement(): api.dom.Element {
            return this;
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

            this.setEditMode(true, true);
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
                new StartTextEditModeEvent(this).fire();
            }
        }

        handleKey() {
            this.processChanges();
        }

        public isEditMode(): boolean {
            return this.hasClass('edit-mode');
        }

        public setEditMode(flag: boolean, selectText?: boolean) {

            this.toggleClass('edit-mode', flag);
            this.article.getEl().setAttribute('contenteditable', flag.toString());
            this.setDraggableEnabled(!flag);

            if (flag) {

                if (selectText) {
                    this.focusText();
                }

                if (!this.editor) {
                    this.editor = this.createEditor();
                    this.editor.onHideToolbar = this.processChanges.bind(this);
                }

                this.editor.activate();

            } else {
                if (this.editor) {
                    this.editor.deactivate();
                }
            }
        }

        private setDraggableEnabled(enabled: boolean = true) {
            wemjq(RegionItemType.get().getConfig().getCssSelector()).sortable(enabled ? 'enable' : 'disable');
        }

        private createEditor(): MediumEditorType {
            return new MediumEditor([this.article.getHTMLElement()], {
                buttons: ['bold', 'italic', 'underline', 'strikethrough',
                    'justifyLeft', 'justifyCenter', 'justifyRight', 'justifyFull',
                    'anchor',
                    'header1', 'header2',
                    'orderedlist', 'unorderedlist',
                    'quote'
                ],
                cleanPastedHTML: true,
                targetBlank: true,
                placeholder: '',
                firstHeader: 'h1',
                secondHeader: 'h2'
            });
        }

        private focusText() {
            var element = this.article.getHTMLElement();
            var selection = window.getSelection();
            var range = document.createRange();
            range.selectNodeContents(element);
            range.setStart(range.endContainer, range.startOffset);
            range.setEnd(range.endContainer, range.endOffset);
            selection.removeAllRanges();
            selection.addRange(range);
        }

        private createTextContextMenuActions(): api.ui.Action[] {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Edit').onExecuted(() => {
                this.setEditMode(true);
                new StartTextEditModeEvent(this).fire();
            }));
            return actions;
        }
    }
}