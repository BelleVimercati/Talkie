import { ReactNode } from 'react';
interface AlertProps {
    variant: 'error' | 'success';
    children: ReactNode;
    onClose?: () => void;
}
declare function Alert({ variant, children, onClose }: AlertProps): JSX.Element;
export default Alert;
//# sourceMappingURL=Alert.d.ts.map