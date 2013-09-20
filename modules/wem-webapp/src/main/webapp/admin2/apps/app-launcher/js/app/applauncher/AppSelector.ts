module app_launcher {

    export class AppSelector extends api_dom.DivEl {
        private selectedAppIndex:number;
        private apps:Application[];
        private appTiles:{[name: string]: AppTile;};
        private listeners:AppSelectorListener[] = [];
        private emptyMessagePlaceholder:api_dom.DivEl;
        private homeAppSelector:api_dom.DivEl;

        constructor(applications:Application[]) {
            super();
            this.apps = applications;
            this.appTiles = {};
            this.selectedAppIndex = -1;

            this.homeAppSelector = new api_dom.DivEl(null, 'app-selector');

            var searchInputContainer = new api_dom.DivEl(null, 'search-input-container');
            var searchInput = new api_ui.TextInput();
            searchInput.setPlaceholder('Application Filter');
            searchInput.addListener({
                onValueChanged: (oldValue, newValue) => {
                    this.filterTiles(newValue);
                }
            });
            searchInputContainer.appendChild(searchInput);

            this.homeAppSelector.appendChild(searchInputContainer);

            var tilesPlaceholder = new api_dom.DivEl(null, 'app-tiles-placeholder');
            this.emptyMessagePlaceholder = new api_dom.DivEl();
            this.emptyMessagePlaceholder.getEl().setInnerHtml('No applications found');
            this.emptyMessagePlaceholder.hide();
            tilesPlaceholder.appendChild(this.emptyMessagePlaceholder);

            this.addAppTiles(applications, tilesPlaceholder);
            this.homeAppSelector.appendChild(tilesPlaceholder);

            this.appendChild(this.homeAppSelector);

            api_ui.KeyBindings.bindKey(new api_ui.KeyBinding('tab', (e:ExtendedKeyboardEvent, combo:string)=> {
                this.highlightNextAppTile();
                return false;
            }));
            api_ui.KeyBindings.bindKey(new api_ui.KeyBinding('shift+tab', (e:ExtendedKeyboardEvent, combo:string)=> {
                this.highlightPreviousAppTile();
                return false;
            }));
            api_ui.KeyBindings.bindKey(new api_ui.KeyBinding('return', (e:ExtendedKeyboardEvent, combo:string)=> {
                var app:Application;
                if (this.selectedAppIndex >= 0) {
                    app = this.apps[this.selectedAppIndex];
                    this.notifyAppSelected(app);
                }
                return false;
            }));
        }

        addListener(listener:AppSelectorListener) {
            this.listeners.push(listener);
        }

        afterRender() {
            super.afterRender();
            this.homeAppSelector.addClass('fade-in-and-scale-up');
        }

        private highlightNextAppTile() {
            var n = this.apps.length, i = 0, idx;
            do {
                i++;
                idx = (this.selectedAppIndex + i) % n;
            }
            while (i < n && !this.isAppTileVisible(idx));

            this.highlightAppTile(this.apps[idx], idx);
        }

        private highlightPreviousAppTile() {
            var n = this.apps.length, i = 0, idx;
            do {
                i++;
                idx = ((this.selectedAppIndex - i % n) + n) % n; // workaround for js negative mod bug
            }
            while (i < n && !this.isAppTileVisible(idx));

            this.highlightAppTile(this.apps[idx], idx);
        }

        private addAppTiles(applications:Application[], tilesPlaceholder:api_dom.DivEl) {
            applications.forEach((application:Application, idx:number) => {
                var appTile = new AppTile(application);

                appTile.onMouseEnter((event:MouseEvent) => {
                    this.highlightAppTile(application, idx, appTile);
                });
                appTile.onMouseLeave((event:MouseEvent) => {
                    this.unhighlightAppTile(application, idx, appTile);
                });
                appTile.getEl().addEventListener("click", (evt:Event) => {
                    this.notifyAppSelected(application);
                });

                tilesPlaceholder.appendChild(appTile);
                this.appTiles[application.getName()] = appTile;
            });
        }

        private highlightAppTile(application:Application, index:number, appTile?:AppTile) {
            if (!appTile) {
                appTile = this.appTiles[application.getName()];
            }
            var currentSelected = this.selectedAppIndex >= 0;
            if (currentSelected) {
                var currentTileSelected = this.appTiles[this.apps[this.selectedAppIndex].getName()];
                currentTileSelected.removeClass('app-tile-over');
            }
            appTile.addClass('app-tile-over');
            this.selectedAppIndex = index;
            this.notifyAppHighlighted(application);
        }

        private unhighlightAppTile(application:Application, index:number, appTile?:AppTile) {
            if (!appTile) {
                appTile = this.appTiles[application.getName()];
            }
            appTile.removeClass('app-tile-over');
            if (this.selectedAppIndex === index) {
                this.selectedAppIndex = -1;
            }
            this.notifyAppUnhighlighted(application);
        }

        private filterTiles(value:string) {
            var valueLowerCased = value.toLowerCase();
            var anyMatch = false;
            this.apps.forEach((app:Application) => {
                var isMatch = app.getName().toLowerCase().indexOf(valueLowerCased) > -1;
                if (isMatch) {
                    this.showAppTile(app.getName());
                    anyMatch = true;
                } else {
                    this.hideAppTile(app.getName());
                }
            });

            if (anyMatch) {
                this.emptyMessagePlaceholder.hide();
            } else {
                this.emptyMessagePlaceholder.show();
            }
        }

        private showAppTile(appName:string) {
            var appTile:AppTile = this.appTiles[appName];
            appTile.show();
        }

        private hideAppTile(appName:string) {
            var appTile:AppTile = this.appTiles[appName];
            appTile.hide();
        }

        private isAppTileVisible(appIndex:number):boolean {
            return this.appTiles[this.apps[appIndex].getName()].isVisible();
        }

        private notifyAppHighlighted(app:Application) {
            this.listeners.forEach((listener:AppSelectorListener)=> {
                listener.onAppHighlighted(app);
            });
        }

        private notifyAppUnhighlighted(app:Application) {
            this.listeners.forEach((listener:AppSelectorListener)=> {
                listener.onAppUnhighlighted(app);
            });
        }

        private notifyAppSelected(app:Application) {
            this.listeners.forEach((listener:AppSelectorListener)=> {
                listener.onAppSelected(app);
            });
        }

    }

}
