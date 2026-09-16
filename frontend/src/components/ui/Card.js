import { jsx as _jsx } from "react/jsx-runtime";
export function Card({ children, className = '' }) {
    return (_jsx("div", { className: `rounded-2xl bg-white p-6 shadow-sm ${className}`, children: children }));
}
export default Card;
//# sourceMappingURL=Card.js.map