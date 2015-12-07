// Login to any of the user stores, in sequence.
var result = auth.login({
    user: 'user1@enonic.com',
    password: 'secret',
    userStore: ['enonic', 'vip']
});
