module LiveEdit.ui {
    var $ = $liveedit;
    var componentHelper = LiveEdit.ComponentHelper;
    var domHelper = LiveEdit.DomHelper;

    export class Menu extends LiveEdit.ui.Base {
        private selectedComponent:JQuery;
        private previousPageSizes = null;
        private previousPagePositions = null;
        private hidden = true;
        private buttons = [];
        private buttonConfig = {
            'page':         ['settings', 'reset'],
            'region':       ['parent', 'settings', 'reset', 'clear'],
            'layout':       ['parent', 'settings', 'remove'],
            'part':         ['parent', 'settings', 'details', 'remove'],
            'content':      ['parent', 'opencontent', 'view'],
            'paragraph':    ['parent', 'edit', 'remove']
        };

        constructor() {
            super();
            this.addView();
            this.registerEvents();
            this.registerGlobalListeners();

            console.log('Menu instantiated. Using jQuery ' + $().jquery);
        }


        registerGlobalListeners():void {
            $(window).on('component.onSelect', (event, $component, pagePosition) => {
                this.show(event, $component, pagePosition);
            });

            $(window).on('component.onDeselect component.onRemove component.onParagraphEdit', () => {
                this.hide();
            });

            $(window).on('component.onSortStart', () => {
                this.fadeOutAndHide();
            });
        }


        addView():void {
            var html = '';
            html += '<div class="live-edit-component-menu live-edit-arrow-top" style="display: none">';
            html += '   <div class="live-edit-component-menu-title-bar">';
            html += '       <div class="live-edit-component-menu-title-icon"><div><!-- --></div></div>';
            html += '       <div class="live-edit-component-menu-title-text"><!-- populated --></div>';
            html += '       <div class="live-edit-component-menu-title-close-button"><!-- --></div>';
            html += '   </div>';
            html += '   <div class="live-edit-component-menu-items">';
            html += '   </div>';
            html += '</div>';

            this.createElement(html);
            this.appendTo($('body'));
            this.addButtons();
        }


        registerEvents():void {
            this.getRootEl().draggable({
                handle: '.live-edit-component-menu-title-bar',
                addClasses: false
            });

            this.getCloseButton().click(function () {
                $(window).trigger('component.onDeselect');
            });
        }


        show(event, $component, pagePosition):void {
            this.selectedComponent = $component;
            this.previousPagePositions = pagePosition;
            this.previousPageSizes = domHelper.getViewPortSize();

            this.updateTitleBar($component);

            this.updateMenuItemsForComponent($component);

            var pageXPosition = pagePosition.x - this.getRootEl().width() / 2,
                pageYPosition = pagePosition.y + 15;
            this.moveToXY(pageXPosition, pageYPosition);

            this.getRootEl().show();

            this.hidden = false;
        }


        hide():void {
            this.selectedComponent = null;
            this.getRootEl().hide();
            this.hidden = true;
        }


        fadeOutAndHide():void {
            this.getRootEl().fadeOut(500, () => {
                this.hide();
                $(window).trigger('component.onDeselect', {showComponentBar: false});
            });
            this.selectedComponent = null;
        }


        moveToXY(x, y):void {
            this.getRootEl().css({
                left: x,
                top: y
            });
        }


        addButtons():void {
            var parentButton = new LiveEdit.ui.ParentButton(this);
            var settingsButton = new LiveEdit.ui.SettingsButton(this);
            var detailsButton = new LiveEdit.ui.DetailsButton(this);
            var insertButton = new LiveEdit.ui.InsertButton(this);
            var resetButton = new LiveEdit.ui.ResetButton(this);
            var clearButton = new LiveEdit.ui.ClearButton(this);
            var openContentButton = new LiveEdit.ui.OpenContentButton(this);
            var viewButton = new LiveEdit.ui.ViewButton(this);
            var editButton = new LiveEdit.ui.EditButton(this);
            var removeButton = new LiveEdit.ui.RemoveButton(this);

            var i,
                $menuItemsPlaceholder = this.getMenuItemsPlaceholderElement();
            for (i = 0; i < this.buttons.length; i++) {
                this.buttons[i].appendTo($menuItemsPlaceholder);
            }
        }


        updateMenuItemsForComponent($component):void {
            var componentType = componentHelper.getComponentType($component);
            var buttonArray = this.getConfigForButton(componentType);
            var buttons = this.getButtons();

            var i;
            for (i = 0; i < buttons.length; i++) {
                var $button = buttons[i].getRootEl();
                var id = $button.attr('data-live-edit-ui-cmp-id');
                var subStr = id.substring(id.lastIndexOf('-') + 1, id.length);
                if (buttonArray.indexOf(subStr) > -1) {
                    $button.show();
                } else {
                    $button.hide();
                }
            }
        }


        updateTitleBar($component):void {
            var componentInfo = componentHelper.getComponentInfo($component);
            this.setIcon(componentInfo.type);
            this.setTitle(componentInfo.name);
        }


        setTitle(titleText):void {
            this.getTitleElement().text(titleText);
        }


        setIcon(componentType):void {
            var $iconCt = this.getIconElement(),
                iconCls = this.resolveCssClassForComponentType(componentType);
            $iconCt.children('div').attr('class', iconCls);
            $iconCt.attr('title', componentType);
        }


        resolveCssClassForComponentType(componentType:string):string {
            var iconCls:string;

            switch (componentType) {
                case 'page':
                    iconCls = 'live-edit-component-menu-page-icon';
                    break;

                case 'region':
                    iconCls = 'live-edit-component-menu-region-icon';
                    break;

                case 'layout':
                    iconCls = 'live-edit-component-menu-layout-icon';
                    break;

                case 'part':
                    iconCls = 'live-edit-component-menu-part-icon';
                    break;

                case 'content':
                    iconCls = 'live-edit-component-menu-content-icon';
                    break;

                case 'paragraph':
                    iconCls = 'live-edit-component-menu-paragraph-icon';
                    break;

                default:
                    iconCls = '';
            }

            return iconCls;
        }


        getButtons():any[] {
            return this.buttons;
        }


        getConfigForButton(componentType):any {
            return this.buttonConfig[componentType];
        }


        getIconElement():JQuery {
            return $('.live-edit-component-menu-title-icon', this.getRootEl());
        }


        getTitleElement():JQuery {
            return $('.live-edit-component-menu-title-text', this.getRootEl());
        }


        getCloseButton():JQuery {
            return $('.live-edit-component-menu-title-close-button', this.getRootEl());
        }


        getMenuItemsPlaceholderElement():JQuery {
            return $('.live-edit-component-menu-items', this.getRootEl());
        }


        handleWindowResize(event):void {
            if (this.selectedComponent) {
                var x = this.previousPagePositions.x,
                    y = this.previousPagePositions.y;

                x = x - (this.previousPageSizes.width - LiveEdit.DomHelper.getViewPortSize().width);

                this.moveToXY(x, y);
            }
        }

    }
}