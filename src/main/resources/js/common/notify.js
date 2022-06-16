const notifyInterval = (selector, type) => {
    setTimeout(() => {
        const message = document.querySelector(selector);
        if (message && message.textContent) {
            toastr[type](message.textContent)
        }
    })
}

notifyInterval('#success-message', 'success');
notifyInterval('#error-message', 'error');
