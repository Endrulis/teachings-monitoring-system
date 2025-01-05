export interface CurricularUnitDto {
    id: number;
    name: string;
    teacher: {
        id: number;
        email: string;
    };
}
