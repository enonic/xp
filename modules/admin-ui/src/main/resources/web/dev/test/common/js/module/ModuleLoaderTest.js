describe("api.module.ModuleLoaderTest", function () {

    var moduleLoader;

    beforeEach(function () {
        moduleLoader = new api.module.ModuleLoader();
    });

    it("should create an instance", function () {
        expect(moduleLoader).toBeDefined();
    });

    it("should set request property", function () {
        expect(moduleLoader.request).toBeDefined();
    });

    it("request property should be of correct type", function () {
        expect(api.ObjectHelper.iFrameSafeInstanceOf(moduleLoader.request, api.module.ListModulesRequest)).toBeTruthy();
    });

    describe("default loading behavior", function () {
        var deferredPromise;

        beforeEach(function () {
            deferredPromise = wemQ.defer();

            spyOn(moduleLoader, "sendRequest").and.returnValue(deferredPromise.promise);

            spyOn(moduleLoader, "notifyLoadingData");
            spyOn(moduleLoader, "notifyLoadedData");

            moduleLoader.load();
        });

        it("should fire an event before data load", function () {
            expect(moduleLoader.notifyLoadingData).toHaveBeenCalled();
        });

        describe("after modules are loaded", function () {
            var modules = [];

            beforeEach(function () {
                var startedModule = new api.module.ModuleBuilder().build();
                var stoppedModule = new api.module.ModuleBuilder().build();

                modules.push(startedModule, stoppedModule);

                spyOn(modules, "filter");
            });

            it("should fire an event after data load", function () {
                deferredPromise.promise.then(function () {
                    expect(moduleLoader.notifyLoadedData).toHaveBeenCalled();
                });

                deferredPromise.resolve(modules);
            });

            it("should NOT filter data", function () {
                deferredPromise.promise.then(function () {
                    expect(modules.filter).not.toHaveBeenCalled();
                });

                deferredPromise.resolve(modules);
            });
        });
    });

    describe("loading with filtering", function () {
        var deferredPromise, filterObject, promiseLoad, modules = [];

        beforeEach(function () {
            filterObject = {
                state: api.module.Application.STATE_STARTED
            };

            moduleLoader = new api.module.ModuleLoader(500, filterObject);

            deferredPromise = wemQ.defer();

            spyOn(moduleLoader, "sendRequest").and.returnValue(deferredPromise.promise);

            promiseLoad = moduleLoader.load();
        });

        describe("after modules are loaded", function () {

            beforeEach(function () {
                modules = [];
                var moduleBuilder = new api.module.ModuleBuilder();

                moduleBuilder.state = api.module.Application.STATE_STARTED;
                var startedModule = moduleBuilder.build();

                moduleBuilder.state = api.module.Application.STATE_STOPPED;
                var stoppedModule = moduleBuilder.build();

                modules.push(startedModule, stoppedModule);

                spyOn(modules, "filter");
            });

            it("should apply filter", function () {
                deferredPromise.promise.then(function () {
                    expect(modules.filter).toHaveBeenCalledWith(filterObject);
                });

                deferredPromise.resolve(modules);
            });

            it("should correctly filter data", function () {
                promiseLoad.then(function (filteredModules) {
                    expect(filteredModules.length).toBe(1);
                    expect(filteredModules[0]).toBe(startedModule);
                });

                deferredPromise.resolve(modules);
            });
        });
    });
});
