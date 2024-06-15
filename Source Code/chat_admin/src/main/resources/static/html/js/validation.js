function randomPassword(length) {
    var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+<>?";
    var pass = "";
    for (var x = 0; x < length; x++) {
        var i = Math.floor(Math.random() * chars.length);
        pass += chars.charAt(i);
    }
    return pass;
}
// function generatePassword() {
//     signupPassword.row_password.value = randomPassword(myform.length.value);
// }

function generatePassword() {
    // set the value of the input field with id 'signupPassword'
    document.getElementById('signupPassword').value = randomPassword(8);
}

function togglePasswordVisibility() {
    var passwordInput = document.getElementById('signupPassword');
    if (passwordInput.type === "password") {
        passwordInput.type = "text";
    } else {
        passwordInput.type = "password";
    }
}

