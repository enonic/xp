import ClassHelper = api.ClassHelper;

describe('api.ClassHelper', function () {

    describe('tests for api.ClassHelper.getFunctionName() function', function () {

        it('returns function name as string', function () {
            expect(ClassHelper.getFunctionName(function namedFunction() { /* empty */ })).toBe('namedFunction');
        });

        it('returns empty string for anonymous function', function () {
            expect(ClassHelper.getFunctionName(function () { /* empty */ })).toBe('');
        });

    });

    describe('tests for api.ClassHelper.getClassName() function', function () {

        it('returns class name for object', function () {
            let instance = new api.dom.ElementHelper(document.body);
            expect(ClassHelper.getClassName(instance)).toBe('ElementHelper');
        });

        it("should return instance['constructor']['name']", function () {
            expect(ClassHelper.getClassName(new (function Some() { /* empty */ })())).toBe('Some');
            expect(ClassHelper.getClassName({})).toBe('Object');
        });

    });

    describe('tests for api.ClassHelper.getModuleName() function', function () {

        it('returns full module path for class which given object is instance of', function () {
            let instance = new api.dom.ElementHelper(document.body);
            expect(ClassHelper.getModuleName(instance)).toBe('api.dom');
        });

        it('returns full module path for given typescript class', function () {
            expect(ClassHelper.getModuleName(api.dom.ElementHelper)).toBe('api.dom');
        });

        it('returns full module path for given exported function', function () {
            expect(ClassHelper.getModuleName(api.util.i18n)).toBe('api.util');
        });

    });

    describe('tests for api.ClassHelper.getFullName() function', function () {

        it('returns full name for class which given object is instance of', function () {
            let instance = new api.dom.ElementHelper(document.body);
            expect(ClassHelper.getFullName(instance)).toBe('api.dom.ElementHelper');
        });

        it('returns full name for given typescript class', function () {
            expect(ClassHelper.getFullName(api.dom.ElementHelper)).toBe('api.dom.ElementHelper');
        });

        it('returns full name for given exported function', function () {
            expect(ClassHelper.getFullName(api.util.i18n)).toBe('api.util.i18n');
        });

        it('correctly resolves classes with equal names', function () {
            api['test_class1'] = {
                Class1: function Class1() { /* empty */ }
            };
            api['test_class2'] = {
                Class1: function Class1() { /* empty */ }
            };
            expect(ClassHelper.getFullName(api['test_class1'].Class1)).toBe('api.test_class1.Class1');
            expect(ClassHelper.getFullName(api['test_class2'].Class1)).toBe('api.test_class2.Class1');
        });

    });

});
