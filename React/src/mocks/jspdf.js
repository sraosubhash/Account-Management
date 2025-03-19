// __mocks__/jspdf.js
export default class jsPDF {
    constructor() {
        this.autoTable = jest.fn();
        this.save = jest.fn();
    }
}
