// include function helper
import optionComponent from "./option.component.js";

const event = new Event("change", {"bubbles":true, "cancelable":false});

export function selectChange(DOM, callback, domDependOn) {
    if (!DOM || !domDependOn) return;
    DOM?.addEventListener('change', async event => {
        const result = await callback(event.target.value);
        const { value } = domDependOn.dataset;
        domDependOn.innerHTML = result?.html(r => optionComponent({...r, select: r.id == value}));
    })
    DOM?.dispatchEvent(event);
}