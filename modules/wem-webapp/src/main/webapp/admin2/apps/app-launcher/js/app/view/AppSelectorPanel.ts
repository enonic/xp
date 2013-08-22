///<reference path='../model/Application.ts' />
///<reference path='AppTile.ts' />

module app_view {

    export class AppSelectorPanel extends api_dom.DivEl {
        private selectedAppIndex:number;
        private apps:app_model.Application[];
        private appTiles:{[name: string]: AppTile;};
        private onAppMouseEnter:(app:app_model.Application) => void;
        private onAppMouseLeave:(app:app_model.Application) => void;
        private emptyMessagePlaceholder:api_dom.DivEl;

        constructor(applications:app_model.Application[]) {
            super();
            this.apps = applications;
            this.appTiles = {};
            this.selectedAppIndex = -1;

            this.getEl().setAttribute('data-screen', 'app-selector');

            var homeAppSelector = new api_dom.DivEl(null, 'admin-home-app-selector');
            homeAppSelector.addClass('fade-in-and-scale-up');

            var searchInputContainer = new api_dom.DivEl(null, 'admin-home-app-selector-search-input-container');
            var searchInput = new api_ui.TextInput(null, 'admin-home-app-selector-search-inputEl');
            searchInput.setPlaceholder('Application Filter');
            searchInput.addListener({
                onValueChanged: (oldValue, newValue) => {
                    this.filterTiles(newValue);
                }
            });
            searchInputContainer.appendChild(searchInput);

            homeAppSelector.appendChild(searchInputContainer);

            var tilesPlaceholder = new api_dom.DivEl(null, 'admin-home-app-tiles-placeholder');
            this.emptyMessagePlaceholder = new api_dom.DivEl();
            this.emptyMessagePlaceholder.getEl().setInnerHtml('No applications found');
            this.emptyMessagePlaceholder.hide();
            tilesPlaceholder.appendChild(this.emptyMessagePlaceholder);

            this.addAppTiles(applications, tilesPlaceholder);
            homeAppSelector.appendChild(tilesPlaceholder);

            this.appendChild(homeAppSelector);

            api_ui.KeyBindings.bindKey(new api_ui.KeyBinding('tab', (e:ExtendedKeyboardEvent, combo:string)=> {
                this.tabKeyPressed();
                return false;
            }));
            api_ui.KeyBindings.bindKey(new api_ui.KeyBinding('shift+tab', (e:ExtendedKeyboardEvent, combo:string)=> {
                this.shiftTabKeyPressed();
                return false;
            }));
        }

        private tabKeyPressed() {
            var n = this.apps.length, i = 0, idx;
            do {
                i++;
                idx = (this.selectedAppIndex + i) % n;
            }
            while (i < n && !this.isAppTileVisible(idx));

            this.selectAppTile(this.apps[idx], idx);
        }

        private shiftTabKeyPressed() {
            var n = this.apps.length, i = 0, idx;
            do {
                i++;
                idx = ((this.selectedAppIndex - i % n) + n) % n; // workaround for js negative mod bug
            }
            while (i < n && !this.isAppTileVisible(idx));

            this.selectAppTile(this.apps[idx], idx);
        }

        private addAppTiles(applications:app_model.Application[], tilesPlaceholder:api_dom.DivEl) {
            applications.forEach((application:app_model.Application, idx:number) => {
                var appTile = new AppTile(application);

                appTile.onMouseEnter((event:MouseEvent) => {
                    this.selectAppTile(application, idx, appTile);
                });
                appTile.onMouseLeave((event:MouseEvent) => {
                    this.deselectAppTile(application, idx, appTile);
                });
                appTile.getEl().addEventListener("click", (evt:Event) => {
                    console.log('App click', application);
                });

                tilesPlaceholder.appendChild(appTile);
                this.appTiles[application.getName()] = appTile;
            });
        }

        private selectAppTile(application:app_model.Application, index:number, appTile?:AppTile) {
            if (!appTile) {
                appTile = this.appTiles[application.getName()];
            }
            var currentSelected = this.selectedAppIndex >= 0;
            if (currentSelected) {
                var currentTileSelected = this.appTiles[this.apps[this.selectedAppIndex].getName()];
                currentTileSelected.removeClass('admin-home-app-tile-over');
            }
            appTile.addClass('admin-home-app-tile-over');
            this.selectedAppIndex = index;
            if (this.onAppMouseEnter) {
                this.onAppMouseEnter(application);
            }
        }

        private deselectAppTile(application:app_model.Application, index:number, appTile?:AppTile) {
            if (!appTile) {
                appTile = this.appTiles[application.getName()];
            }
            appTile.removeClass('admin-home-app-tile-over');
            if (this.selectedAppIndex === index) {
                this.selectedAppIndex = -1;
            }
            if (this.onAppMouseLeave) {
                this.onAppMouseLeave(application);
            }
        }

        private filterTiles(value:string) {
            var valueLowerCased = value.toLowerCase();
            var anyMatch = false;
            this.apps.forEach((app:app_model.Application) => {
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

        private isAppTileVisible(appIndex:number):bool {
            return this.appTiles[this.apps[appIndex].getName()].isVisible();
        }

        onAppSelected(handler:(app:app_model.Application) => void) {
            this.onAppMouseEnter = handler;
        }

        onAppDeselected(handler:(app:app_model.Application) => void) {
            this.onAppMouseLeave = handler;
        }
    }

}
