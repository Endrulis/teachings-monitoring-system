export interface StudentListPopupProps {
    curricularUnitId: number | null;
    selectedClassSessionId: number | null;
    isOpen: boolean;
    onClose: () => void;
}