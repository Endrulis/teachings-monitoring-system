import { RoleDto } from "./RoleDto";

export interface UserDto {
    id: number;
    username: string;
    password: string;
    email: string;
    role: RoleDto; // This assumes that the RoleDto is also an object with `id` and `name`
  }
  