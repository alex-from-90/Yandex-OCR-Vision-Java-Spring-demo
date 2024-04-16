function openNewWindow() {
    var ocrResult = document.getElementById("ocrResult").innerHTML;
    var newWindow = window.open("", "newWindow", "width=auto,height=auto");
    newWindow.document.write("<html><body><div id='ocrResult'></div></body></html>");
    newWindow.document.getElementById("ocrResult").innerHTML = ocrResult;
}

function manageButtonVisibility() {
    var ocrResponse = document.getElementById("ocrResponse").innerHTML.trim();
    var openButton = document.getElementById("openButton");
    var successMessage = document.querySelector('.success-message');
    var errorMessage = document.querySelector('.error-message');

    if (ocrResponse) {
        openButton.style.display = "block";
        successMessage.style.display = "block";
        errorMessage.style.display = "none";
    } else {
        openButton.style.display = "none";
        successMessage.style.display = "none";
        errorMessage.style.display = "block";
    }
}
