module LiveEdit.ui.contextmenu {
    var $ = $liveEdit;
    var componentHelper = LiveEdit.ComponentHelper;
    var domHelper = LiveEdit.DomHelper;

    export class Menu extends LiveEdit.ui.Base {
        private selectedComponent:JQuery;
        private previousPageSizes = null;
        private previousPagePositions = null;
        private hidden = true;
        private buttons = [];
        private buttonConfig = {
            'page': ['reset'],
            'region': ['parent', 'reset', 'clear'],
            'layout': ['parent', 'remove'],
            'part': ['parent', 'details', 'remove'],
            'content': ['parent', 'opencontent', 'view'],
            'paragraph': ['parent', 'edit', 'remove']
        };

        constructor() {
            super();
            this.addView();
            this.registerEvents();
            this.registerGlobalListeners();

            console.log('Menu instantiated. Using jQuery ' + $().jquery);
        }

        private registerGlobalListeners():void {
            $(window).on('selectComponent.liveEdit',
                (event:JQueryEventObject, component:JQuery, pagePosition) => this.show(component, pagePosition));
            $(window).on('deselectComponent.liveEdit', () => this.hide());
            $(window).on('removeComponent.liveEdit', () => this.hide());
            $(window).on('editParagraphComponent.liveEdit', () => this.hide());
            $(window).on('sortableStart.liveEdit', () => this.fadeOutAndHide());
            $(window).on('resizeBrowserWindow.liveEdit', () => this.handleWindowResize());
        }

        private addView():void {
            var html:string = '';
            html += '<div class="live-edit-context-menu live-edit-arrow-top" style="display: none">';
            html += '   <div class="live-edit-context-menu-title-bar">';
            html += '       <div class="live-edit-context-menu-title-icon"><div><!-- --></div></div>';
            html += '       <div class="live-edit-context-menu-title-text"><!-- populated --></div>';
            html += '       <div class="live-edit-context-menu-title-close-button"><!-- --></div>';
            html += '   </div>';
            html += '   <div class="live-edit-context-menu-items">';
            html += '   </div>';
            html += '</div>';

            this.createElementsFromString(html);
            this.appendTo($('body'));
            this.addButtons();
        }

        private registerEvents():void {
            this.getRootEl().draggable({
                handle: '.live-edit-context-menu-title-bar',
                addClasses: false
            });

            this.getCloseButton().click(function () {
                $(window).trigger('deselectComponent.liveEdit');
            });
        }

        private show(component:JQuery, pagePosition):void {
            this.selectedComponent = component;
            this.previousPagePositions = pagePosition;
            this.previousPageSizes = domHelper.getViewPortSize();

            this.updateTitleBar(component);
            this.updateMenuItemsForComponent(component);

            // Calculate positions after menu is populated in order to get the right position.
            var pageXPosition = pagePosition.x - this.getRootEl().width() / 2,
                pageYPosition = pagePosition.y + 15;

            this.moveToXY(pageXPosition, pageYPosition);
            this.getRootEl().show(null);

            this.hidden = false;
        }

        private hide():void {
            this.selectedComponent = null;
            this.getRootEl().hide(null);
            this.hidden = true;
        }

        private fadeOutAndHide():void {
            this.getRootEl().fadeOut(500, () => {
                this.hide();
                $(window).trigger('deselectComponent.liveEdit', {showComponentBar: false});
            });
            this.selectedComponent = null;
        }

        private moveToXY(x, y):void {
            this.getRootEl().css({
                left: x,
                top: y
            });
        }

        private addButtons():void {
            var menuItem = LiveEdit.ui.contextmenu.menuitem;
            var parentButton = new menuitem.Parent(this);
            var detailsButton = new menuitem.Details(this);
            var insertButton = new menuitem.Insert(this);
            var resetButton = new menuitem.Reset(this);
            var clearButton = new menuitem.Empty(this);
            var openContentButton = new menuitem.OpenContent(this);
            var viewButton = new menuitem.View(this);
            var editButton = new menuitem.Edit(this);
            var removeButton = new menuitem.Remove(this);
            var i,
                $menuItemsPlaceholder = this.getMenuItemsPlaceholderElement();

            for (i = 0; i < this.buttons.length; i++) {
                this.buttons[i].appendTo($menuItemsPlaceholder);
            }
        }

        private updateMenuItemsForComponent(component:JQuery):void {
            var componentType = componentHelper.getComponentType(component);
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

        private updateTitleBar(component:JQuery):void {
            var componentInfo = componentHelper.getComponentInfo(component);
            this.setIcon(component);
            this.setTitle(componentInfo.name);
        }

        private setTitle(titleText:string):void {
            this.getTitleElement().text(titleText);
        }

        private setIcon(component:JQuery):void {
            var iconCt:JQuery = this.getIconElement(),
                iconCls:string = componentHelper.resolveCssClassForComponent(component);
            iconCt.children('div').attr('class', iconCls);
            iconCt.attr('title', componentHelper.getComponentType(component));
        }

        private handleWindowResize():void {
            // fixme: improve!
            if (this.selectedComponent) {
                var componentBoxModel = componentHelper.getBoxModel(this.selectedComponent),
                    x = componentBoxModel.left + componentBoxModel.width / 2 - this.getRootEl().width() / 2,
                    y = this.getRootEl().offset().top;

                this.moveToXY(x, y);
            }
        }

        private getButtons():any[] {
            return this.buttons;
        }

        private getConfigForButton(componentType:string):any {
            return this.buttonConfig[componentType];
        }

        private getIconElement():JQuery {
            return $('.live-edit-context-menu-title-icon', this.getRootEl());
        }

        private getTitleElement():JQuery {
            return $('.live-edit-context-menu-title-text', this.getRootEl());
        }

        private getCloseButton():JQuery {
            return $('.live-edit-context-menu-title-close-button', this.getRootEl());
        }

        private getMenuItemsPlaceholderElement():JQuery {
            return $('.live-edit-context-menu-items', this.getRootEl());
        }

    }
}