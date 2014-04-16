try {
    require('./bogus');
    test.fail('FAIL require throws error when module missing');
} catch (exception) {
    test.assertEquals('Error: Resource [require-1.0.0:/missing/bogus.js] was not found', exception);
}
