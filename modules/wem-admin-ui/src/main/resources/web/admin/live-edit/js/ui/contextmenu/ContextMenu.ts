module LiveEdit.ui.contextmenu {

    import ItemView = api.liveedit.ItemView;
    import DraggingPageComponentViewStartedEvent = api.liveedit.DraggingPageComponentViewStartedEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;

    export class ContextMenu extends LiveEdit.ui.Base {

        selectedComponent: ItemView;

        hidden = true;

        menuItems: LiveEdit.ui.contextmenu.menuitem.BaseMenuItem[] = [];

        constructor() {
            super();
            this.addView();
            this.registerGlobalListeners();
            this.registerEventsListeners();
        }

        private addView(): void {
            var html: string = '';
            html += '<div class="live-edit-context-menu live-edit-arrow-top" style="display: none">';
            html += '   <div class="live-edit-context-menu-title-bar">';
            html += '       <div class="live-edit-context-menu-title-icon-container"><div><!-- --></div></div>';
            html += '       <div class="live-edit-context-menu-title-text"><!-- populated --></div>';
            html += '       <div class="live-edit-context-menu-title-close-button live-edit-font-icon-close"><!-- --></div>';
            html += '   </div>';
            html += '   <div class="live-edit-context-menu-items"><!-- populated --></div>';
            html += '</div>';

            this.createHtmlFromString(html);
            this.appendTo(wemjq('body'));
            this.addMenuItems();
        }

        private registerGlobalListeners(): void {
            ItemViewSelectedEvent.on((event: ItemViewSelectedEvent) =>
                this.show(event.getItemView(), event.getPosition()));
            ItemViewDeselectEvent.on(() => this.hide());
            PageComponentRemoveEvent.on(() => this.hide());
            wemjq(window).on('editTextComponent.liveEdit', () => this.hide());
            DraggingPageComponentViewStartedEvent.on(() => this.fadeOutAndHide());
            wemjq(window).on('resizeBrowserWindow.liveEdit', () => this.handleWindowResize());
        }

        private registerEventsListeners(): void {
            this.getEl().draggable({
                handle: '.live-edit-context-menu-title-bar',
                containment: 'document',
                addClasses: false
            });

            this.getCloseButton().click(() => {
                this.selectedComponent.deselect();
            });
        }

        private show(component: ItemView, pageXYPosition: api.liveedit.Position = null): void {
            this.selectedComponent = component;

            this.updateTitleBar(component);
            this.updateMenuItemsForComponent(component);

            // Position the context menu after menu items are updated in order to get the right menu popup width
            var position = this.resolvePagePosition(component, pageXYPosition);
            this.moveToXY(position.x, position.y);

            this.getEl().show();

            this.hidden = false;
        }

        private hide(): void {
            this.selectedComponent = null;
            this.getEl().hide(null);
            this.hidden = true;
        }

        private fadeOutAndHide(): void {
            this.getEl().fadeOut(500, () => {
                this.hide();
            });
            this.selectedComponent = null;
        }

        moveToXY(x, y): void {
            this.getEl().css({
                left: x,
                top: y,
                position: "absolute"
            });
        }

        private addMenuItems(): void {
            var menuItem = LiveEdit.ui.contextmenu.menuitem;

            this.menuItems.push(new menuItem.ParentMenuItem(this));
            this.menuItems.push(new menuItem.InsertMenuItem(this));
            this.menuItems.push(new menuItem.ResetMenuItem(this));
            this.menuItems.push(new menuItem.EmptyMenuItem(this));
            this.menuItems.push(new menuItem.EmptyRegionMenuItem(this));
            this.menuItems.push(new menuItem.OpenContentMenuItem(this));
            this.menuItems.push(new menuItem.ViewMenuItem(this));
            this.menuItems.push(new menuItem.EditMenuItem(this));
            this.menuItems.push(new menuItem.RemoveMenuItem(this));
            this.menuItems.push(new menuItem.DuplicateMenuItem(this));

            var i,
                menuItemsPlaceholder: JQuery = this.getMenuItemsPlaceholderElement();

            for (i = 0; i < this.menuItems.length; i++) {
                this.menuItems[i].appendTo(menuItemsPlaceholder);
            }
        }

        private updateMenuItemsForComponent(component: ItemView): void {

            var menuItemsForComponent = component.getType().getConfig().getContextMenuConfig();

            var menuItems: any[] = this.getMenuItems();

            var i;
            for (i = 0; i < menuItems.length; i++) {
                var menuItemEl: JQuery = menuItems[i].getEl();
                var name: string = menuItemEl.attr('data-live-edit-ctx-menu-item-name');
                if (menuItemsForComponent.indexOf(name) > -1) {
                    menuItemEl.show(null);
                } else {
                    menuItemEl.hide(null);
                }
            }
        }

        private resolvePagePosition(itemView: ItemView, pageXYPosition: api.liveedit.Position): api.liveedit.Position {
            var componentElementDimensions = itemView.getElementDimensions();
            var pageXPosition, pageYPosition;
            if (pageXYPosition) {
                pageXPosition = pageXYPosition.x - this.getEl().width() / 2;
                pageYPosition = pageXYPosition.y + 15;
            } else {
                // component element - center
                pageXPosition = componentElementDimensions.left + (componentElementDimensions.width / 2) - this.getEl().width() / 2;
                pageYPosition = componentElementDimensions.top + 10 + (!itemView.isEmpty() ? 0 : componentElementDimensions.height);
            }

            return {
                x: pageXPosition,
                y: pageYPosition
            }
        }

        private updateTitleBar(component: ItemView): void {
            this.setIcon(component);
            this.setTitle(component.getName());
        }

        private setTitle(titleText: string): void {
            this.getTitleElement().text(titleText);
        }

        private setIcon(component: ItemView): void {
            var iconContainer: JQuery = this.getIconContainerElement(),
                iconCls: string = component.getType().getConfig().getIconCls();

            iconContainer.children('div').attr('class', iconCls);
            iconContainer.attr('title', component.getType().getShortName());
        }

        private handleWindowResize(): void {
            // fixme: improve!
            if (this.selectedComponent) {
                var dimensions = this.selectedComponent.getElementDimensions(),
                    x = dimensions.left + dimensions.width / 2 - this.getEl().width() / 2,
                    y = this.getEl().offset().top;

                //this.moveToXY(x, y);
            }
        }

        private getMenuItems(): any[] {
            return this.menuItems;
        }

        private getIconContainerElement(): JQuery {
            return wemjq('.live-edit-context-menu-title-icon-container', this.getEl());
        }

        private getTitleElement(): JQuery {
            return wemjq('.live-edit-context-menu-title-text', this.getEl());
        }

        private getCloseButton(): JQuery {
            return wemjq('.live-edit-context-menu-title-close-button', this.getEl());
        }

        private getMenuItemsPlaceholderElement(): JQuery {
            return wemjq('.live-edit-context-menu-items', this.getEl());
        }

    }
}