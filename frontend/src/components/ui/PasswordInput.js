import { jsx as _jsx } from "react/jsx-runtime";
import { forwardRef, useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import Input from './Input';
const PasswordInput = forwardRef(({ label, error, ...props }, ref) => {
    const [showPassword, setShowPassword] = useState(false);
    return (_jsx(Input, { ref: ref, type: showPassword ? 'text' : 'password', label: label, error: error, rightIcon: _jsx("button", { type: "button", onClick: () => setShowPassword(!showPassword), className: "text-black-700 transition-colors hover:text-black-800", "aria-label": showPassword ? 'Ocultar senha' : 'Mostrar senha', children: showPassword ? _jsx(EyeOff, { size: 16 }) : _jsx(Eye, { size: 16 }) }), ...props }));
});
PasswordInput.displayName = 'PasswordInput';
export default PasswordInput;
//# sourceMappingURL=PasswordInput.js.map