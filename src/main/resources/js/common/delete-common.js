const elements = document.querySelectorAll('.btn-delete');
const acceptDOM = document.querySelector('#confirm_accept');
const confirmationBodyDOM = document.querySelector('#confirmation_body');

Array.from(elements)
    .filter(ele => ele.tagName == 'A')
    .forEach(anchor => {
        anchor.addEventListener('click', event => {
            $('#confirmation_modal').modal();
            const { body, href } = event.target.dataset;
            confirmationBodyDOM.textContent = body;
            acceptDOM.addEventListener('click', event => {
                anchor.href = href;
                anchor.click();
            })
        })
    })