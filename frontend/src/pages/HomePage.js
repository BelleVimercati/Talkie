import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { AppLayout } from '@/components/layout/AppLayout';
import { StatsCards } from '@/components/occurrences/StatsCards';
import { OccurrencesTable } from '@/components/occurrences/OccurrencesTable';
import Alert from '@/components/ui/Alert';
import { useOccurrences } from '@/hooks/useOccurrences';
function HomePage() {
    const { occurrences, isLoading, error } = useOccurrences();
    return (_jsx(AppLayout, { children: _jsxs("div", { className: "p-8", children: [_jsx("h1", { className: "mb-8 text-3xl font-bold font-roboto text-black-900", children: "Minhas Ocorr\u00EAncias" }), error && _jsx(Alert, { variant: "error", children: error }), _jsxs("div", { className: "mb-8", children: [_jsx("h2", { className: "mb-4 text-lg font-medium text-black-500", children: "Vis\u00E3o Geral" }), _jsx(StatsCards, { occurrences: occurrences })] }), _jsxs("div", { children: [_jsx("h2", { className: "mb-4 text-lg font-medium text-black-500", children: "Todos os registros" }), _jsx(OccurrencesTable, { occurrences: occurrences, isLoading: isLoading })] })] }) }));
}
export default HomePage;
//# sourceMappingURL=HomePage.js.map