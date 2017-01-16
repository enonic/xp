import ApplicationLoader = api.application.ApplicationLoader;
import ListApplicationsRequest = api.application.ListApplicationsRequest;
import ApplicationBuilder = api.application.ApplicationBuilder;
import Application = api.application.Application;

describe('api.application.ApplicationLoader', () => {

    let applicationLoader;

    beforeEach(() => {
        applicationLoader = new ApplicationLoader(null);
    });

    it('should create an instance', () => {
        expect(applicationLoader).toBeDefined();
    });

    it('should set request property', () => {
        expect(applicationLoader.request).toBeDefined();
    });

    it('request property should be of correct type', () => {
        expect(api.ObjectHelper.iFrameSafeInstanceOf(applicationLoader.request, ListApplicationsRequest)).toBeTruthy();
    });

    describe('default loading behavior', () => {
        let deferredPromise;

        beforeEach(() => {
            deferredPromise = wemQ.defer();

            spyOn(applicationLoader, 'sendRequest').and.returnValue(deferredPromise.promise);

            spyOn(applicationLoader, 'notifyLoadingData');
            spyOn(applicationLoader, 'notifyLoadedData');

            applicationLoader.load();
        });

        it('should fire an event before data load', () => {
            expect(applicationLoader.notifyLoadingData).toHaveBeenCalled();
        });

        describe('after applications are loaded', () => {
            let applications = [];

            beforeEach(() => {
                let startedApplication = new ApplicationBuilder().build();
                let stoppedApplication = new ApplicationBuilder().build();

                applications.push(startedApplication, stoppedApplication);

                spyOn(applications, 'filter');
            });

            it('should fire an event after data load', (done) => {
                deferredPromise.promise.then(() => {
                    expect(applicationLoader.notifyLoadedData).toHaveBeenCalled();
                    done();
                });

                deferredPromise.resolve(applications);
            });

            it('should NOT filter data', (done) => {
                deferredPromise.promise.then(() => {
                    expect(applications.filter).not.toHaveBeenCalled();
                    done();
                });

                deferredPromise.resolve(applications);
            });
        });
    });

    describe('loading with filtering', () => {
        let deferredPromise;
        let filterObject;
        let promiseLoad;
        let applications;

        beforeEach(() => {
            filterObject = {
                state: Application.STATE_STARTED
            };

            applicationLoader = new ApplicationLoader(filterObject);

            deferredPromise = wemQ.defer();

            spyOn(applicationLoader, 'sendRequest').and.returnValue(deferredPromise.promise);

            promiseLoad = applicationLoader.load();
        });

        describe('after applications are loaded', () => {

            let startedApplication;
            let filterSpy;

            beforeEach(() => {
                applications = [];
                let applicationBuilder = new ApplicationBuilder();

                applicationBuilder.state = Application.STATE_STARTED;
                startedApplication = applicationBuilder.build();

                applicationBuilder.state = Application.STATE_STOPPED;
                let stoppedApplication = applicationBuilder.build();

                applications.push(startedApplication, stoppedApplication);

                filterSpy = spyOn(applications, 'filter');

            });

            it('should apply filter', (done) => {
                deferredPromise.promise.then(() => {
                    expect(applications.filter).toHaveBeenCalled();
                    done();
                });

                deferredPromise.resolve(applications);
            });

            it('should correctly filter data', (done) => {
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
