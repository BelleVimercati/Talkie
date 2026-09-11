import { ButtonHTMLAttributes, ReactNode } from 'react';
interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'primary';
    isLoading?: boolean;
    children: ReactNode;
}
declare function Button({ variant, isLoading, children, disabled, className, ...props }: ButtonProps): JSX.Element;
export default Button;
//# sourceMappingURL=Button.d.ts.map