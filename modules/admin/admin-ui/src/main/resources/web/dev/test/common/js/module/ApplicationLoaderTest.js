describe("api.application.ApplicationLoaderTest", function () {

    var applicationLoader;

    beforeEach(function () {
        applicationLoader = new api.application.ApplicationLoader();
    });

    it("should create an instance", function () {
        expect(applicationLoader).toBeDefined();
    });

    it("should set request property", function () {
        expect(applicationLoader.request).toBeDefined();
    });

    it("request property should be of correct type", function () {
        expect(api.ObjectHelper.iFrameSafeInstanceOf(applicationLoader.request, api.application.ListApplicationsRequest)).toBeTruthy();
    });

    describe("default loading behavior", function () {
        var deferredPromise;

        beforeEach(function () {
            deferredPromise = wemQ.defer();

            spyOn(applicationLoader, "sendRequest").and.returnValue(deferredPromise.promise);

            spyOn(applicationLoader, "notifyLoadingData");
            spyOn(applicationLoader, "notifyLoadedData");

            applicationLoader.load();
        });

        it("should fire an event before data load", function () {
            expect(applicationLoader.notifyLoadingData).toHaveBeenCalled();
        });

        describe("after applications are loaded", function () {
            var applications = [];

            beforeEach(function () {
                var startedApplication = new api.application.ApplicationBuilder().build();
                var stoppedApplication = new api.application.ApplicationBuilder().build();

                applications.push(startedApplication, stoppedApplication);

                spyOn(applications, "filter");
            });

            it("should fire an event after data load", function () {
                deferredPromise.promise.then(function () {
                    expect(applicationLoader.notifyLoadedData).toHaveBeenCalled();
                });

                deferredPromise.resolve(applications);
            });

            it("should NOT filter data", function () {
                deferredPromise.promise.then(function () {
                    expect(applications.filter).not.toHaveBeenCalled();
                });

                deferredPromise.resolve(applications);
            });
        });
    });

    describe("loading with filtering", function () {
        var deferredPromise, filterObject, promiseLoad, applications = [];

        beforeEach(function () {
            filterObject = {
                state: api.application.Application.STATE_STARTED
            };

            applicationLoader = new api.application.ApplicationLoader(500, filterObject);

            deferredPromise = wemQ.defer();

            spyOn(applicationLoader, "sendRequest").and.returnValue(deferredPromise.promise);

            promiseLoad = applicationLoader.load();
        });

        describe("after applications are loaded", function () {

            beforeEach(function () {
                applications = [];
                var applicationBuilder = new api.application.ApplicationBuilder();

                applicationBuilder.state = api.application.Application.STATE_STARTED;
                var startedApplication = applicationBuilder.build();

                applicationBuilder.state = api.application.Application.STATE_STOPPED;
                var stoppedApplication = applicationBuilder.build();

                applications.push(startedApplication, stoppedApplication);

                spyOn(applications, "filter");
            });

            it("should apply filter", function () {
                deferredPromise.promise.then(function () {
                    expect(applications.filter).toHaveBeenCalledWith(filterObject);
                });

                deferredPromise.resolve(applications);
            });

            it("should correctly filter data", function () {
                promiseLoad.then(function (filteredApplications) {
                    expect(filteredApplications.length).toBe(1);
                    expect(filteredApplications[0]).toBe(startedApplication);
                });

                deferredPromise.resolve(applications);
            });
        });
    });
});
