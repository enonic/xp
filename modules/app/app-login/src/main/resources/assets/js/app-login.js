function handleAuthenticateResponse(loginResult) {
    if (loginResult.authenticated) {
        location.reload();
    } else {
        document.getElementById("message-container").innerHTML = 'Login failed!';
        document.getElementById("message-container").focus();
        document.getElementById("username-input").classList.add('invalid');
        document.getElementById("password-input").classList.add('invalid');
        document.getElementById("login-button").classList.add('invalid');
    }
}

function loginButtonClick() {
    var userNameInput = document.getElementById("username-input");
    var passwordInput = document.getElementById("password-input");
    var loginButton = document.getElementById("login-button");
    if (userNameInput.value === '' || passwordInput.value === '') {
        return;
    }

    userNameInput.classList.remove("invalid");
    passwordInput.classList.remove("invalid");
    loginButton.classList.remove("invalid");

    var data = {
        user: userNameInput.value,
        password: passwordInput.value,
        userStore: CONFIG.userStoreKey
    }

    $.ajax({
        url: CONFIG.appLoginServiceUrl,
        type: 'post',
        dataType: 'json',
        contentType: 'application/json',
        success: handleAuthenticateResponse,
        data: JSON.stringify(data)
    });
}