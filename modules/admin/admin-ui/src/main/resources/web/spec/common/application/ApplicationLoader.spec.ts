import ApplicationLoader = api.application.ApplicationLoader;
import ListApplicationsRequest = api.application.ListApplicationsRequest;
import ApplicationBuilder = api.application.ApplicationBuilder;
import Application = api.application.Application;

describe("api.application.ApplicationLoader", () => {

    var applicationLoader;

    beforeEach(() => {
        applicationLoader = new ApplicationLoader(null);
    });

    it("should create an instance", () => {
        expect(applicationLoader).toBeDefined();
    });

    it("should set request property", () => {
        expect(applicationLoader.request).toBeDefined();
    });

    it("request property should be of correct type", () => {
        expect(api.ObjectHelper.iFrameSafeInstanceOf(applicationLoader.request, ListApplicationsRequest)).toBeTruthy();
    });

    describe("default loading behavior", () => {
        var deferredPromise;

        beforeEach(() => {
            deferredPromise = wemQ.defer();

            spyOn(applicationLoader, "sendRequest").and.returnValue(deferredPromise.promise);

            spyOn(applicationLoader, "notifyLoadingData");
            spyOn(applicationLoader, "notifyLoadedData");

            applicationLoader.load();
        });

        it("should fire an event before data load", () => {
            expect(applicationLoader.notifyLoadingData).toHaveBeenCalled();
        });

        describe("after applications are loaded", () => {
            var applications = [];

            beforeEach(() => {
                var startedApplication = new ApplicationBuilder().build();
                var stoppedApplication = new ApplicationBuilder().build();

                applications.push(startedApplication, stoppedApplication);

                spyOn(applications, "filter");
            });

            it("should fire an event after data load", (done) => {
                deferredPromise.promise.then(() => {
                    expect(applicationLoader.notifyLoadedData).toHaveBeenCalled();
                    done();
                });

                deferredPromise.resolve(applications);
            });

            it("should NOT filter data", (done) => {
                deferredPromise.promise.then(() => {
                    expect(applications.filter).not.toHaveBeenCalled();
                    done();
                });

                deferredPromise.resolve(applications);
            });
        });
    });

    describe("loading with filtering", () => {
        var deferredPromise, filterObject, promiseLoad, applications = [];

        beforeEach(() => {
            filterObject = {
                state: Application.STATE_STARTED
            };

            applicationLoader = new ApplicationLoader(filterObject);

            deferredPromise = wemQ.defer();

            spyOn(applicationLoader, "sendRequest").and.returnValue(deferredPromise.promise);

            promiseLoad = applicationLoader.load();
        });

        describe("after applications are loaded", () => {

            var startedApplication, filterSpy;

            beforeEach(() => {
                applications = [];
                var applicationBuilder = new ApplicationBuilder();

                applicationBuilder.state = Application.STATE_STARTED;
                startedApplication = applicationBuilder.build();

                applicationBuilder.state = Application.STATE_STOPPED;
                var stoppedApplication = applicationBuilder.build();

                applications.push(startedApplication, stoppedApplication);

                filterSpy = spyOn(applications, "filter");



            });

            it("should apply filter", (done) => {
                deferredPromise.promise.then(() => {
                    expect(applications.filter).toHaveBeenCalled();
                    done();
                });

                deferredPromise.resolve(applications);
            });

            it("should correctly filter data", (done) => {
                filterSpy.and.callThrough();

                promiseLoad.then((filteredApplications) => {
                    expect(filteredApplications.length).toBe(1);
                    expect(filteredApplications[0]).toBe(startedApplication);
                    done();
                });

                deferredPromise.resolve(applications);
            });
        });
    });
});