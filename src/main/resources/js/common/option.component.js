export default function optionComponent({id, name, select}) {
    return `<option value="${id}" ${select ? 'selected' : ''}>${name}</option>`
}